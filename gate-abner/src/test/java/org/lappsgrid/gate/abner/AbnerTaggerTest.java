package org.lappsgrid.gate.abner;

import gate.Document;
import gate.util.GateException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lappsgrid.gate.core.GateCoreException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 *
 */
public class AbnerTaggerTest
{
	@BeforeClass
	public static void setupClass() {
		System.setProperty(AbnerTagger.GATE_HOME, "/usr/local/lapps/gate_abner");

	}

	@Test
	public void tag() throws GateCoreException, GateException
	{
		AbnerTagger tagger = new AbnerTagger();
		String text = load("/covid.txt");
		Document doc = tagger.createDocumentFromText(text);
		tagger.execute(doc);
		System.out.println(doc.toXml());
	}

	private String load(String resource) {
		InputStream in = this.getClass().getResourceAsStream(resource);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		return reader.lines().collect(Collectors.joining("\n"));
	}
}