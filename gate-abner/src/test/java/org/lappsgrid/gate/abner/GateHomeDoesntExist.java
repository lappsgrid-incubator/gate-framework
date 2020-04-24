package org.lappsgrid.gate.abner;

import gate.util.GateException;
import org.junit.Test;
import org.lappsgrid.gate.core.GateCoreException;

/**
 * GATE maintains static fields that make it impossible to test some things, in particular configuration and
 * startup errors. Once Gate.init() has been called the GATE subsystem can't be uninitialized.
 */
public class GateHomeDoesntExist
{
	@Test(expected = GateCoreException.class)
	public void gateHomeDoesntExist() throws GateCoreException, GateException
	{
		System.setProperty(AbnerTagger.GATE_HOME, "/foobar");
		new AbnerTagger();
	}
}
