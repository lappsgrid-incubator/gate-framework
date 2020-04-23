package org.lappsgrid.gate.cli.abner.io;

import gate.Document;
import org.anc.lapps.gate.serialization.GateSerializer;
import org.lappsgrid.gate.cli.abner.error.AbnerCliException;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.lif.Annotation;
import org.lappsgrid.serialization.lif.Container;
import org.lappsgrid.serialization.lif.View;

import java.io.OutputStream;

import static org.lappsgrid.discriminator.Discriminators.Uri;

/**
 *
 */
public class LifWriter implements DocumentWriter {

	public void write(Document doc, OutputStream stream) throws AbnerCliException {
		Container container = GateSerializer.convertToContainer(doc);
		for (View view : container.getViews()) {
			for (Annotation a : view.getAnnotations()) {
				if ("Tagger".equals(a.getAtType())) {
					a.setAtType(Uri.NE);
				}
			}
		}
		String json = new Data(Uri.LIF, container).asPrettyJson();
		write(json, stream);
	}

	@Override
	public String getExtension() {
		return ".lif";
	}
}
