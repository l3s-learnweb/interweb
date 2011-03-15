package de.l3s.interwebj.core;


import java.net.*;

import org.apache.log4j.*;

import de.l3s.interwebj.db.*;


public class Environment
{
	
	private static Environment singleton;
	
	public static final String INTERWEBJ_SERVICE_NAME = "interwebj";
	
	public static Logger logger = Logger.getLogger("interwebj");
	
	private Configuration configuration;
	private Database database;
	private Engine engine;
	private AccessControll accessControll;
	

	private Environment(URL configUrl)
	    throws InterWebException
	{
		logger.info("Logger initialized successfully");
		configuration = new Configuration(configUrl);
		database = new JDBCDatabase(configuration);
		engine = new Engine(database);
		accessControll = new AccessControll();
	}
	

	public AccessControll getAccessControll()
	{
		return accessControll;
	}
	

	public Configuration getConfiguration()
	{
		return configuration;
	}
	

	public Database getDatabase()
	{
		return database;
	}
	

	public Engine getEngine()
	{
		return engine;
	}
	

	public Logger getLogger()
	{
		return logger;
	}
	

	public static Environment getInstance()
	    throws InterWebException
	{
		if (singleton == null)
		{
			throw new InterWebException("IWEnvironment is not yet initialazied. Run first IWEnvironment.getInstance(URL configUrl).");
		}
		return singleton;
	}
	

	public static Environment getInstance(URL configUrl)
	    throws InterWebException
	{
		if (singleton == null)
		{
			singleton = new Environment(configUrl);
		}
		return singleton;
	}
	
}
