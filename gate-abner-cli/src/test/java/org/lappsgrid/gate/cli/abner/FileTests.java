package org.lappsgrid.gate.cli.abner;

import gate.util.Files;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

/**
 *
 */
@Ignore
public class FileTests {

	@Test
	public void readString() throws IOException {
		File file = new File("/Users/suderman/Workspaces/IntelliJ/org.lappsgrid.pubannotation/pom.xml");
		String text = Files.getString(file);
		System.out.println(text);
	}

	@Test
	public void streamTests() {
		assertTrue(System.out instanceof OutputStream);
	}

	@Test
	public void blockingTest() throws InterruptedException {
		System.out.println("FileTests.blockingTest");
		BlockingQueue<Object> q = new ArrayBlockingQueue<>(10, false);
		CountDownLatch latch = new CountDownLatch(1);
		Blocking worker = new Blocking(q, latch);
//		Thread thread = new Thread(worker);
		worker.start();
		System.out.println("Waiting for awhile.");
		Thread.sleep(5000);
		System.out.println("Attempting to halt the worker.");
		worker.halt();
		System.out.println("Waiting for the latch");
		latch.await();
		System.out.println("Done.");
	}
}

class Blocking extends Thread {

	private BlockingQueue<Object> q;
	private CountDownLatch latch;
	private boolean running;

	public Blocking(BlockingQueue<Object> q, CountDownLatch latch) {
		this.q = q;
		this.latch = latch;
		this.running = false;
	}

	public void run() {
		System.out.println("Staring blocking thread.");
		running = true;
		while (running) {
			try {
				Object o = q.take();
			}
			catch (InterruptedException e) {
				System.out.println("Thread was interrupted.");
				Thread.currentThread().interrupt();
				running = false;
			}
		}
		latch.countDown();
		System.out.println("Thread terminated.");
	}

	public void halt() {
		System.out.println("Halting thread.");
		running = false;

		System.out.println("Notifying the queue.");
		this.interrupt();
	}
}