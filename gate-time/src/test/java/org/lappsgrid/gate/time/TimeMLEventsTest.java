package org.lappsgrid.gate.time;

import gate.Document;
import gate.util.GateException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lappsgrid.gate.core.BaseGateService;
import org.lappsgrid.gate.core.GateCoreException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 *
 */
public class TimeMLEventsTest
{
	@BeforeClass
	public static void setupClass() {
		System.setProperty(BaseGateService.GATE_HOME, "/usr/local/lapps/gate");

	}

	@Test
	public void tag() throws GateCoreException, GateException
	{
		TimeMLEvents tagger = new TimeMLEvents("/usr/local/lapps/timeml-gapp/application.xgapp");
		String text = load("/covid.txt");
		Document doc = tagger.createDocumentFromText(text);
		tagger.execute(doc);
		System.out.println(doc.toXml());
	}

	private String load(String resource) {
		InputStream in = this.getClass().getResourceAsStream(resource);
		assert null != in;
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		return reader.lines().collect(Collectors.joining("\n"));
	}
}