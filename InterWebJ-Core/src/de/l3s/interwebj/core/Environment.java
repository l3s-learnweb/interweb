package de.l3s.interwebj.core;

import java.io.InputStream;

import de.l3s.interwebj.config.Configuration;
import de.l3s.interwebj.db.Database;
import de.l3s.interwebj.db.JDBCDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Environment
{
	private static final Logger log = LogManager.getLogger(Environment.class);

    private static Environment singleton;

    private Configuration configuration;
    private Database database;
    private Engine engine;
    private AccessControll accessControll;

    private Environment(InputStream inputStream)
    {
		try
		{
			configuration = new Configuration(inputStream);
			log.info("Configuration loaded");
			log.info("Initializing database ...");
			database = new JDBCDatabase(configuration);
			log.info("Database connected");
			engine = new Engine(database);
			accessControll = new AccessControll();
		}
		catch(Exception e)
		{
			log.error("Unable to load configuration file", e);
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

    public static Environment getInstance()
    {
		if(singleton == null)
		{
			InputStream localProperties = Environment.class.getClassLoader().getResourceAsStream("config_local.xml");
			if(localProperties == null)
				localProperties = Environment.class.getClassLoader().getResourceAsStream("config.xml");

			singleton = new Environment(localProperties);
		}
		return singleton;
    }

    public static Environment getInstance(InputStream inputStream)
    {
		if(singleton == null)
		{
			singleton = new Environment(inputStream);
		}
		return singleton;
    }
}
