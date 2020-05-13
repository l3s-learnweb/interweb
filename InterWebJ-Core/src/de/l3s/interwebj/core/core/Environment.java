package de.l3s.interwebj.core.core;

import java.io.InputStream;
import java.util.Properties;

import de.l3s.interwebj.core.db.Database;
import de.l3s.interwebj.core.db.JDBCDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Environment
{
	private static final Logger log = LogManager.getLogger(Environment.class);

    private static Environment singleton;

    private Properties properties;
    private Database database;
    private Engine engine;
    private AccessControll accessControll;

    private Environment(InputStream inputStream)
    {
		try
		{
			properties = new Properties();
			properties.load(inputStream);
			log.info("Configuration loaded");
			log.info("Initializing database ...");
			database = new JDBCDatabase(properties);
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

    public Properties getProperties()
    {
	return properties;
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
			InputStream localProperties = Environment.class.getClassLoader().getResourceAsStream("interweb_local.properties");
			if(localProperties == null)
				localProperties = Environment.class.getClassLoader().getResourceAsStream("interweb.properties");

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
