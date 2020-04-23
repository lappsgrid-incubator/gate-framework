package org.lappsgrid.gate.cli.abner.threads;

import gate.Document;
import gate.FeatureMap;
import gate.creole.ResourceInstantiationException;
import org.lappsgrid.gate.abner.AbnerTagger;
import org.lappsgrid.gate.cli.abner.Version;
import org.lappsgrid.gate.cli.abner.error.AbnerCliException;
import org.lappsgrid.gate.cli.abner.io.DocumentWriter;
import org.lappsgrid.gate.cli.abner.io.FileLoader;
import org.lappsgrid.gate.core.GateCoreException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class AbnerWorker extends Thread {
	private int id;
	private BlockingQueue<FilePair> inQ;
	private FileLoader loader;
	private DocumentWriter writer;
	private CountDownLatch latch;
	private AtomicInteger counter;
	private AbnerTagger tagger;
	private boolean running;

	public AbnerWorker(int id, BlockingQueue<FilePair> inQ, FileLoader fileLoader, DocumentWriter writer,
					   CountDownLatch latch, AtomicInteger counter) throws GateCoreException {
		this.id = id;
		this.inQ = inQ;
		this.loader = fileLoader;
		this.writer = writer;
		this.latch = latch;
		this.counter = counter;
		this.tagger = new AbnerTagger();
	}

	public void run() {
		System.out.println("Staring thread " + id);
		running = true;
		while (running) {
			try {
				FilePair pair = inQ.take();
				if (FilePair.EOF == pair) {
					System.out.printf("Thread %d shutting down.\n", id);
					running = false;
				}
				else try {
					process(pair);
				}
				catch (AbnerCliException e) {
//					System.err.println(e.getMessage());
					e.printStackTrace();
				}
			}
			catch (InterruptedException e) {
				running = false;
				Thread.currentThread().interrupt();
			}
		}
		latch.countDown();
	}

	public void halt() {
		running = false;
		this.interrupt();
	}

	private void process(FilePair pair) throws AbnerCliException {
		Document document = null;
		try {
			document = loader.load(pair.in);
			tagger.execute(document);
			FeatureMap docFeatures = document.getFeatures();
			Integer step = (Integer) docFeatures.get("lapps:step");
			if (step == null) {
				step = 1;
			}
			docFeatures.put("lapps:step", step + 1);
			docFeatures.put("lapps:Tagger", step + " " + getProducer() + " gate");

			String filename = pair.in.getName() + writer.getExtension();
			File outputFile = new File(pair.out, filename);
			File parent = outputFile.getParentFile();
			if (!parent.exists()) {
				if (!parent.mkdirs()) {
					throw new AbnerCliException("Unable to create output directory " + parent.getPath());
				}
			}

			try(FileOutputStream fos = new FileOutputStream(outputFile)) {
				writer.write(document, new FileOutputStream(outputFile));
				int count = counter.incrementAndGet();
				System.out.printf("%05d: thread %d wrote %s\n", count, id, outputFile.getPath());
			}
		}
		catch (IOException | ResourceInstantiationException | GateCoreException e) {
			String message = String.format("Thread %d unable to process input document %s", id, pair.in.getPath());
			throw new AbnerCliException(message, e);
		}
	}

	public String getProducer() {
		return this.getClass().getName() + "_" + Version.getVersion();
	}


}
