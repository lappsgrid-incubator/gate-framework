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
 * Use the AbstractGateController to expose a GATE corpus controller pipeline that has been bundled as a sinlge
 * *.gapp by GATE.
 */
public class AbstractGateController extends BaseGateService
{
	protected Corpus corpus;
	protected CorpusController controller;

	public AbstractGateController(String path, Class<? extends AbstractGateController> baseClass) throws GateCoreException
	{
		super(baseClass);
		File app = new File(path);
		if (!app.exists() && !app.isFile()) {
			throw new GateCoreException("Invalid file path.");
		}
		try
		{
			corpus = Factory.newCorpus("gate-temp-corpus");
			controller = (CorpusController) PersistenceManager.loadObjectFromFile(app);
		}
		catch (ResourceInstantiationException | PersistenceException | IOException e)
		{
			logger.error("Unable to initialize GATE application", e);
			throw new GateCoreException("Unable to initialize GATE application", e);
		}
		controller.setCorpus(corpus);

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
			String msg = "Error running the GATE controller.";
			logger.error(msg, e);
			throw new GateCoreException(msg, e);

		}
		finally
		{
			corpus.clear();
		}
	}
}
