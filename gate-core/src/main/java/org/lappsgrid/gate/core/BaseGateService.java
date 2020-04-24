package org.lappsgrid.gate.core;

import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * The BaseGateService class is responsible for initializing the GATE system and ensuring it is
 * only initialized once.  The class expects GATE Embedded and will look in the following locations:
 *
 * 1. The path specified by the system environment variable GATE_HOME
 * 2. The path specified by the Java system property GATE_HOME
 * 3. /usr/local/gate
 * 4. /home/gate
 * 5. /gate
 */
public class BaseGateService
{
	public static final String GATE_HOME = "GATE_HOME";

	protected File home;
	protected final Logger logger;


	public BaseGateService(Class<? extends BaseGateService> baseClass) throws GateCoreException
	{
		this.logger = LoggerFactory.getLogger(baseClass);

		home = findGateHome();
		if (home == null)
		{
			throw new GateCoreException("Unable to find a GATE installation.  Did you set GATE_HOME?");
		}
		if (Gate.isInitialised()) {
			return;
		}

		File site = new File(home, "gate.xml");
		File user = new File(home, "user-gate.xml");
		File plugins = new File(home, "plugins");
		Gate.setGateHome(home);
		Gate.setSiteConfigFile(site);
		Gate.setUserConfigFile(user);
		Gate.setPluginsHome(plugins);
		try
		{
			Gate.init();
		}
		catch (GateException e)
		{
			logger.error("Unable to initialize GATE", e);
			throw new GateCoreException("Unable to initialize GATE", e);
		}
	}

	public Document createDocumentFromText(String text) throws ResourceInstantiationException
	{
		return Factory.newDocument(text);
	}

	public Document createDocumentFromXml(String xml) throws ResourceInstantiationException
	{
		FeatureMap features = Factory.newFeatureMap();
		features.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, xml);
		features.put(Document.DOCUMENT_MIME_TYPE_PARAMETER_NAME, "text/xml");
		return (Document) Factory.createResource("gate.corpora.DocumentImpl", features);
	}


	/**
	 * Try to determine where GATE has been installed. We check, in order:
	 * 1. the environment variable GATE_HOME
	 * 2. the system property GATE_HOME
	 * 3. /usr/local/gate
	 * 4. /home/gate
	 * 5. /gate
	 *
	 * @return A java.io.File object for the GATE home directory, or NULL if
	 * the directory can't be found.
	 */
	protected File findGateHome() {
		String[] locations = { System.getenv(GATE_HOME), System.getProperty(GATE_HOME),
				"/usr/local/gate", "/home/gate", "/gate" };
		for (String path : locations) {
			File file = check(path);
			if (file != null) {
				return file;
			}
		}
		return null;
	}

	protected File check(String path) {
		if (path == null) {
			return null;
		}
		File file = new File(path);
		if (file.exists() && file.isDirectory()) {
			return file;
		}
		return null;
	}

}
