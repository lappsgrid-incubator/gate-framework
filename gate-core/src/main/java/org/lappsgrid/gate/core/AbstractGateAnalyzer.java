package org.lappsgrid.gate.core;

import gate.CreoleRegister;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;

/**
 * Use the AbstractGateAnalyzer to expose a single GATE processing resource.
 */
public class AbstractGateAnalyzer extends BaseGateService
{
	// User assigned name for the service.
	private final String name;

	// The GATE processing resource.
	private AbstractLanguageAnalyser pr;

	public AbstractGateAnalyzer(String name, Class<? extends AbstractGateAnalyzer> baseClass) throws GateCoreException
	{
		super(baseClass);
		this.name = name;

		File plugins = new File(home, "plugins");
		if (!plugins.exists() || !plugins.isDirectory()) {
			logger.error("Unable to find plugins directory.");
			throw new GateCoreException("Invalid plugins directory.");
		}
		try {
			CreoleRegister creole = Gate.getCreoleRegister();
			for (String plugin : plugins.list()) {
				logger.debug("Registering {}", plugin);
				File pluginDir = new File(plugins, plugin);
				creole.registerDirectories(pluginDir.toURI().toURL());
			}
			pr = (AbstractLanguageAnalyser) Factory.createResource(name);
		}
		catch(GateException | MalformedURLException e) {
			throw new GateCoreException("Error initializing the resource.", e);
		}
	}

	public void execute(Document document) throws GateCoreException
	{
		try {
			pr.setDocument(document);
			pr.execute();
		}
		catch (ExecutionException e)
		{
			logger.error("Error running language analyzer", e);
			throw new GateCoreException("Error running language analyzer.", e);
		}
		finally
		{
			pr.setDocument(null);
		}
	}

}
