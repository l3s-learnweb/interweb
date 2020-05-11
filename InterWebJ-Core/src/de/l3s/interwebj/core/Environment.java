package de.l3s.interwebj.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import de.l3s.interwebj.config.Configuration;
import de.l3s.interwebj.db.Database;
import de.l3s.interwebj.db.JDBCDatabase;
import de.l3s.interwebj.util.LoggerCreator;

public class Environment
{

    private static Environment singleton;

    public static final String INTERWEBJ_SERVICE_NAME = "interwebj";
    public static final Logger logger = LoggerCreator.create(INTERWEBJ_SERVICE_NAME);
    private static final String CONFIG_PATH = "config/config.xml";

    private Configuration configuration;
    private Database database;
    private Engine engine;
    private AccessControll accessControll;

    private Environment(String configPath)
    {
	logger.info("Logger initialized");
	try
	{
	    File configFile = new File(configPath);
	    logger.info("Loading configuration [" + configFile.getAbsolutePath() + "] ...");
	    InputStream is;
	    if(configFile.exists())
	    {
		is = new FileInputStream(configFile);
	    }
	    else
	    {
		throw new Exception("Configuration file does not exist");
	    }
	    configuration = new Configuration(is);
	    logger.info("Configuration loaded");
	    logger.info("Initializing database ...");
	    database = new JDBCDatabase(configuration);
	    logger.info("Database connected");
	    engine = new Engine(database);
	    accessControll = new AccessControll();
	}
	catch(Exception e)
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
	if(singleton == null)
	{
	    singleton = new Environment(Environment.CONFIG_PATH);
	}
	return singleton;
    }

    public static Environment getInstance(String configPath)
    {
	if(singleton == null)
	{
	    singleton = new Environment(configPath);
	}
	return singleton;
    }
}
