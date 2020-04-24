package org.lappsgrid.gate.cli.abner;

import gate.Document;
import gate.creole.ResourceInstantiationException;
import org.lappsgrid.gate.abner.AbnerTagger;
import org.lappsgrid.gate.cli.abner.error.AbnerCliException;
import org.lappsgrid.gate.cli.abner.io.DocumentWriter;
import org.lappsgrid.gate.cli.abner.io.FileLoader;
import org.lappsgrid.gate.cli.abner.io.Format;
import org.lappsgrid.gate.cli.abner.io.GateXmlWriter;
import org.lappsgrid.gate.cli.abner.io.LifWriter;
import org.lappsgrid.gate.cli.abner.threads.AbnerWorker;
import org.lappsgrid.gate.cli.abner.threads.FilePair;
import org.lappsgrid.gate.core.GateCoreException;
import picocli.CommandLine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;
import static picocli.CommandLine.Parameters;

/**
 *
 */
@Command(description = "Command line version of the Lappsgrid Abner Tagger from GATE.",
		name="abner",
		optionListHeading = "%nOPTIONS%n",
		parameterListHeading = "%nPARAMETERS%n",
		footerHeading = "%nNOTES%n",
		footer = {
			"If @|bold INFILE|@ specifies a directory location then @|bold OUTFILE|@ must be specified, must already exists, and must be a directory.",
			"If @|bold INFILE|@ is a single file then only a single thread will be used.",
				"If @|bold OUTFILE|@ is not specified output will be written to System.out"
		}
)
public class Main {

	private File input;
	@Option(names={"-t", "--threads"}, description = "number of processing theads to use.", paramLabel = "N", defaultValue = "1", arity = "1")
	private Integer nThreads;
	@Option(names={"-v", "--verbose"}, description = "enable verbose output", defaultValue = "false")
	private boolean verbose;
	@Option(names={"-V", "--version"}, description="display version information and exit", versionHelp = true)
	private boolean versionHelp;
	@Option(names={"-h", "--help"}, description = "display this help message and exit", usageHelp = true)
	private boolean usageHelp;
	@Option(names={"-i", "--input-format"}, description = "format of the input file.", defaultValue = "txt")
	Format inputFormat;
	@Option(names={"-o", "--output-format"}, description = "format of output files.", defaultValue = "lif")
	Format outputFormat;
	@Parameters(index="0", paramLabel="INFILE", description = "input file or directory to process", arity="1")
	private String inputPath;
	@Parameters(index="1", paramLabel = "OUTFILE", description = "output file or directory.", arity = "0..1")
	private String outputPath;


//	public static class OutputFormat {
//		@Option(names={"--xml"}, description = "writes GATE/XML")
//		private boolean xml;
//		@Option(names={"--lif"}, description = "writes LIF output")
//		private boolean lif;
//	}
//	@ArgGroup(exclusive = true, multiplicity = "0..1", heading = "Output format to generate")
//	OutputFormat outformat;

	private ExecutorService executor;

	public Main() {
	}

	protected void run() {
		if (invalidOutputFormatSelected()) {
			System.out.println("Invalid output format. Only LIF and GATE/XML is supported at this time.");
			return;
		}
		File input = new File(inputPath);
		if (!input.exists()) {
			error("Input %s not found", inputPath);
			return;
		}
		if (input.isFile()) {
			// Handle single files as a special case.
			log("Processing single file %s", input.getPath());
			SimpleTimer timer = new SimpleTimer();
			timer.start();
			try {
				processSingleFile(input);
			}
			catch (IOException | GateCoreException | AbnerCliException e) {
				System.err.println(e.getMessage());
			}
			finally {
				timer.stop();
			}
			System.out.println("Processed one file in " + timer.toString());
			return;
		}

		try {
			processDirectory(input);
		}
		catch (AbnerCliException | GateCoreException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void processSingleFile(File input) throws IOException, GateCoreException, AbnerCliException {
		if (!input.exists()) {
			System.err.println("Input file not found: " + input.getPath());
			return;
		}

		DocumentWriter writer = getWriter();
		OutputStream out;
		if (outputPath == null) {
			out = System.out;
		}
		else {
			File outfile = new File(outputPath);
			if (outfile.isDirectory()) {
				String filename = input.getName() + writer.getExtension();
				outfile = new File(outfile, filename);
			}
			out = new FileOutputStream(outfile);
		}
		FileLoader loader = new FileLoader(inputFormat);
		AbnerTagger tagger = new AbnerTagger();
		Document document = null;
		try {
			document = loader.load(input);
		}
		catch (IOException | ResourceInstantiationException e) {
			throw new AbnerCliException("Unable to load input file.", e);
		}
		tagger.execute(document);
		getWriter().write(document,out);
	}

	private DocumentWriter getWriter() throws AbnerCliException {
		if (outputFormat == Format.lif) {
			return new LifWriter();
		}
		if (outputFormat == Format.gate) {
			return new GateXmlWriter();
		}
		throw new AbnerCliException("Invalid output format. No writer available.");
	}

//	private String getExtension() {
//		switch (outputFormat) {
//			case lif:
//				return ".lif";
//			case gate:
//				return ".gate.xml";
//			case txt:
//				return ".txt";
//			case pubann:
//				return ".json";
//			case tcf:
//				return ".tcf.xml";
//			default:
//				throw new UnsupportedOperationException("Loading TCF/XML files is not supported at this time.");
//		}
//
//	}

	private boolean invalidOutputFormatSelected() {
		switch (outputFormat) {
			case lif:
			case gate:
				return false;
		}
		return true;
	}


	private void processDirectory(File directory) throws AbnerCliException, GateCoreException, InterruptedException {
		if (!directory.isDirectory()) {
			throw new RuntimeException("Expected a directory. Found " + directory.getPath());
		}
		// Input is a directory so outputPath can not be null and must point to
		// an existing directory.
		if (outputPath == null) {
			error("When INFILE is a directory OUTFILE must be specified and must be a directory.");
			return;
		}
		File output = new File(outputPath);
		if (!output.exists()) {
			if (!output.mkdirs()) {
				error("Output %s does not exist and could not be created.", outputPath);
				return;
			}
		}
		if (!output.isDirectory()) {
			error("Output %s is not a directory.");
			return;
		}
		SimpleTimer timer = new SimpleTimer();
		timer.start();
		AtomicInteger counter = new AtomicInteger();
		BlockingQueue<FilePair> files = new ArrayBlockingQueue<FilePair>(nThreads, true);
		CountDownLatch latch = new CountDownLatch(nThreads);

		// Start the worker pool.  We use a worker pool instead of an Executor as the AbnerTagger is expensive
		// to construct.  Since we need to pool objects, it may as well be the thread objects.
		List<AbnerWorker> threads = new ArrayList<>();
		for (int i = 0; i < nThreads; ++i) {
			AbnerWorker t = new AbnerWorker(i, files, new FileLoader(inputFormat), getWriter(), latch, counter);
			threads.add(t);
		}
		threads.stream().forEach( t -> t.start() );

		// Fill the work queue.
		recurse(new FilePair(directory, output), files);
		// Add poison pills to kill the workers.
		for (int i = 0; i < nThreads; ++i) {
			files.put(FilePair.EOF);
		}
		System.out.println("Waiting for the threads to trip the latch.");
		latch.await();

		System.out.println("Waiting for the threads to terminate");
		for (int i = 0; i < nThreads; ++i) {
			threads.get(0).join();
		}
		timer.stop();
		System.out.println("Processing complete");
		System.out.printf("Processed %d files\n", counter.get());
		System.out.println("Total time: " + timer.toString());
	}

	private void recurse(FilePair p, BlockingQueue<FilePair> q) throws InterruptedException {
		if (p.in.isFile()) {
//			q.add(p);
			q.put(p);
		}
		else {
			for (File f : p.in.listFiles()) {
				File subdir = new File(p.out, p.in.getName());
				recurse(new FilePair(f, subdir), q);
			}
		}
	}

	private void error(String fmt, Object... args) {
		System.err.printf(fmt, args);
		System.err.println();
	}

	private void log(String fmt, Object... args) {
		if (!verbose) {
			return;
		}
		System.out.printf(fmt, args);
		System.out.println();
	}

	public static void main(String[] args) {
		Main tagger = new Main();
		try {
			new CommandLine(tagger).parseArgs(args);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			CommandLine.usage(tagger, System.out);
			System.out.println();
			return;
		}

		if (tagger.usageHelp) {
			System.out.println();
			CommandLine.usage(tagger, System.out);
			System.out.println("\n");
			return;
		}
		if (tagger.versionHelp) {
			System.out.println();
			System.out.println("Lapps wrapper for the GATE AbnerTagger");
			System.out.println("Copyright 2020 The Language Applications Grid.");
			System.out.println();
			return;
		}
		tagger.run();
	}
}
