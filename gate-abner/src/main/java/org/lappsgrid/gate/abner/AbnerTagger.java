package org.lappsgrid.gate.abner;

import org.lappsgrid.gate.core.AbstractGateAnalyzer;
import org.lappsgrid.gate.core.GateCoreException;

/**
 *
 */
public class AbnerTagger extends AbstractGateAnalyzer
{
	public AbnerTagger() throws GateCoreException
	{
		super("gate.abner.AbnerTagger", AbnerTagger.class);
	}
}
