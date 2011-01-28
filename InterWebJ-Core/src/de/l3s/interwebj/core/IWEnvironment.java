package de.l3s.interwebj.core;


import java.net.*;

import org.apache.log4j.*;

import de.l3s.interwebj.db.*;


public class IWEnvironment
{
	
	private static IWEnvironment singleton;
	
	public static Logger logger = Logger.getLogger("interwebj");
	
	private IWConfiguration configuration;
	private IWDatabase database;
	private IWEngine engine;
	private IWAccessControll accessControll;
	

	private IWEnvironment(URL configUrl)
	{
		logger.info("Logger initialized successfully");
		configuration = new IWConfiguration(configUrl);
		database = new IWJDBCDatabase(configuration);
		engine = new IWEngine(configuration);
		accessControll = new IWAccessControll();
	}
	

	public IWAccessControll getAccessControll()
	{
		return accessControll;
	}
	

	public IWConfiguration getConfiguration()
	{
		return configuration;
	}
	

	public IWDatabase getDatabase()
	{
		return database;
	}
	

	public IWEngine getEngine()
	{
		return engine;
	}
	

	public Logger getLogger()
	{
		return logger;
	}
	

	public static IWEnvironment getInstance()
	{
		if (singleton == null)
		{
			throw new IllegalStateException("IWEnvironment is not yet initialazied. Run first IWEnvironment.getInstance(URL configUrl).");
		}
		return singleton;
	}
	

	public static IWEnvironment getInstance(URL configUrl)
	{
		if (singleton == null)
		{
			singleton = new IWEnvironment(configUrl);
		}
		return singleton;
	}
	
}
