package de.l3s.interwebj.core;


import org.apache.log4j.*;

import de.l3s.interwebj.config.Configuration;
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
	

	private Environment()
	{
		logger.info("Logger initialized successfully");
		configuration = new Configuration();
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
	{
		if (singleton == null)
		{
			singleton = new Environment();
		}
		return singleton;
	}
	
}
