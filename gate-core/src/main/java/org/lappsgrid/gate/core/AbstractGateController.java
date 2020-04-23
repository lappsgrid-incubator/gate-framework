package org.lappsgrid.gate.core;

import gate.Corpus;
import gate.CorpusController;
import gate.Document;
import gate.Factory;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.util.persistence.PersistenceManager;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class AbstractGateController extends BaseGateService
{
	public static final String PATH_KEY = "GATE_TIMEML_PATH";
	protected Corpus corpus;
	protected CorpusController controller;

	public AbstractGateController(String path, Class<? extends AbstractGateController> baseClass) throws GateCoreException
	{
		super(baseClass);
		File timeml = new File(path);
		if (!timeml.exists() && !timeml.isFile()) {
			throw new GateCoreException("Invalid GAPP file path.");
		}
		try
		{
			corpus = Factory.newCorpus("timeml");
			controller = (CorpusController) PersistenceManager.loadObjectFromFile(timeml);
		}
		catch (ResourceInstantiationException | PersistenceException | IOException e)
		{
			logger.error("Unable to initialize TimeML", e);
			throw new GateCoreException("Unable to initialize TimeML", e);
		}
		controller.setCorpus(corpus);

	}

	private File findGappFile() {
		String[] locations = {
				System.getenv(PATH_KEY),
				System.getProperty(PATH_KEY),
				"/usr/local/timeml/application.xgapp",
				"/home/timeml/application.xgapp",
				"/application.xgapp"
		};
		for (String path : locations) {
			File file = check(path);
			if (file != null) {
				return file;
			}
		}
		return null;
	}

	public void execute(Document document) throws GateCoreException
	{
		corpus.add(document);
		try
		{
			controller.execute();
		}
		catch (ExecutionException e)
		{
			String msg = "Error running the TimeML controller.";
			logger.error(msg, e);
			throw new GateCoreException(msg, e);

		}
		finally
		{
			corpus.clear();
		}
	}
}
