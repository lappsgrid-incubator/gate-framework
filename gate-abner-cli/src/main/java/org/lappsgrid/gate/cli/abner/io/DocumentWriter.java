package org.lappsgrid.gate.cli.abner.io;

import gate.Document;
import org.lappsgrid.gate.cli.abner.error.AbnerCliException;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 *
 */
public interface DocumentWriter {
	void write(Document document, OutputStream stream) throws AbnerCliException;
	String getExtension();

	default void write(String data, OutputStream stream) throws AbnerCliException {
		if (stream instanceof PrintStream) {
			((PrintStream) stream).print(data);
			return;
		}

		try (BufferedOutputStream out = getStream(stream)) {
			out.write(data.getBytes());
		}
		catch (IOException e) {
			throw new AbnerCliException("Unable to write to output stream.", e);
		}
	}

	default BufferedOutputStream getStream(OutputStream stream) {
		if (stream instanceof BufferedOutputStream) {
			return (BufferedOutputStream) stream;
		}
		return new BufferedOutputStream(stream);
	}
}
