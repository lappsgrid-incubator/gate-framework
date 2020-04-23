package org.lappsgrid.gate.cli.abner.io;

import gate.Document;
import gate.Factory;
import gate.creole.ResourceInstantiationException;
import gate.util.Files;
import org.anc.lapps.gate.serialization.GateSerializer;
import org.lappsgrid.discriminator.Discriminators;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.DataContainer;
import org.lappsgrid.serialization.Serializer;
import org.lappsgrid.serialization.lif.Container;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class FileLoader {

	private final Format format;

	public FileLoader(final Format format) {
		this.format = format;
	}

	public Document load(File file) throws IOException, ResourceInstantiationException {
		switch (format) {
			case lif:
				return loadLif(file);
			case gate:
				return Factory.newDocument(Files.getString(file));
			case txt:
				return Factory.newDocument(Files.getString(file));
			case cord:
				return loadCord(file);
			default:
				throw new UnsupportedOperationException("Loading TCF/XML files is not supported at this time.");
		}
	}

	private Document loadLif(File file) throws IOException, ResourceInstantiationException {
		String lif = Files.getString(file);
		Data data = Serializer.parse(lif, DataContainer.class);
		if (Discriminators.Uri.LIF.equals(data.getDiscriminator())) {
			return GateSerializer.convertToDocument((Container)data.getPayload());
		}
		if (Discriminators.Uri.GATE.equals(data.getDiscriminator())) {
			return Factory.newDocument((String) data.getPayload());
		}
		return GateSerializer.convertToDocument((Container)data.getPayload());
	}

	private Document loadCord(File file) throws IOException, ResourceInstantiationException {
		String json = Files.getString(file);
		Map<String,Map> document = Serializer.parse(json, HashMap.class);
		String text = collectText(document);
		return Factory.newDocument(text);
	}

	private String collectText(Map document) {
		StringWriter writer = new StringWriter();
		PrintWriter printer = new PrintWriter(writer);
		List<Map> bodyText = (List<Map>) document.get("body_text");
		for (Map section : bodyText) {
			print(printer, section, "section");
			print(printer, section, "text");
			printer.println();
		}
		return writer.toString();
	}

	private void print(PrintWriter writer, Map map, String key) {
		String value = (String) map.get(key);
		if (value != null) {
			writer.println(value);
		}
	}
}
