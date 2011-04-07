package de.l3s.interwebj.core;


import java.io.*;

import org.apache.log4j.*;

import de.l3s.interwebj.config.*;
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
		ClassLoader cl = this.getClass().getClassLoader();
		InputStream is = cl.getResourceAsStream("de/l3s/interwebj/config/config.xml");
		try
		{
			configuration = new Configuration(is);
			database = new JDBCDatabase(configuration);
			engine = new Engine(database);
			accessControll = new AccessControll();
		}
		catch (org.apache.commons.configuration.ConfigurationException e)
		{
			System.out.println("Unable to load configuration file");
			e.printStackTrace();
			System.exit(-1);
		}
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
