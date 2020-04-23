package org.lappsgrid.gate.cli.abner.threads;

import java.io.File;

/**
 *
 */
public class FilePair {
	public static final FilePair EOF = new FilePair(null, null);

	public final File in;
	public final File out;

	public FilePair(File in, File out) {
		this.in = in;
		this.out = out;
	}
}
