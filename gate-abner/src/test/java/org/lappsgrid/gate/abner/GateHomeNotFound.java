package org.lappsgrid.gate.abner;

import gate.util.GateException;
import org.junit.Test;
import org.lappsgrid.gate.core.GateCoreException;

/**
 * GATE maintains static fields that make it impossible to test some things, in particular configuration and
 * startup errors. Once Gate.init() has been called the GATE subsystem can't be uninitialized.
 */
public class GateHomeNotFound
{
	@Test(expected = GateCoreException.class)
	public void directoryExistsButIsNotGateHome() throws GateCoreException, GateException
	{
		System.setProperty(AbnerTagger.GATE_HOME, "/tmp");
		new AbnerTagger();
	}
}
