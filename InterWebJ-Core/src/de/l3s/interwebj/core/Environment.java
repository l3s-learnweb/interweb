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
	private static final String CONFIG_PATH = "config/config.xml";
	
	private Configuration configuration;
	private Database database;
	private Engine engine;
	private AccessControll accessControll;
	

	private Environment()
	{
		this(CONFIG_PATH);
	}
	

	private Environment(String configPath)
	{
		logger.info("Logger initialized successfully");
		try
		{
			File configFile = new File(configPath);
			logger.info("Loading configuration file: ["
			            + configFile.getAbsolutePath() + "]");
			InputStream is;
			if (configFile.exists())
			{
				is = new FileInputStream(configFile);
			}
			else
			{
				ClassLoader cl = this.getClass().getClassLoader();
				is = cl.getResourceAsStream(configPath);
			}
			configuration = new Configuration(is);
			database = new JDBCDatabase(configuration);
			engine = new Engine(database);
			accessControll = new AccessControll();
		}
		catch (Exception e)
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
