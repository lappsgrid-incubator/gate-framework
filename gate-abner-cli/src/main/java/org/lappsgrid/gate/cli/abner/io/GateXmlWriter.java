package org.lappsgrid.gate.cli.abner.io;

import gate.Document;
import org.lappsgrid.gate.cli.abner.error.AbnerCliException;

import java.io.OutputStream;

/**
 *
 */
public class GateXmlWriter implements DocumentWriter {

	@Override
	public void write(Document document, OutputStream stream) throws AbnerCliException {
		write(document.toXml(), stream);
	}

	@Override
	public String getExtension() {
		return ".gate.xml";
	}
}
