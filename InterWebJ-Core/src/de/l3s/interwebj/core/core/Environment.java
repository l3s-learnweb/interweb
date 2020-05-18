package de.l3s.interwebj.core.core;

import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.l3s.interwebj.core.db.Database;
import de.l3s.interwebj.core.db.JdbcDatabase;

public final class Environment {
    private static final Logger log = LogManager.getLogger(Environment.class);

    private static Environment singleton;

    private Properties properties;
    private Database database;
    private Engine engine;
    private AccessControll accessControll;

    private Environment(InputStream inputStream) {
        try {
            properties = new Properties();
            properties.load(inputStream);
            log.info("Configuration loaded");
            log.info("Initializing database ...");
            database = new JdbcDatabase(properties);
            log.info("Database connected");
            engine = new Engine(database);
            accessControll = new AccessControll();
        } catch (Exception e) {
            log.error("Unable to load configuration file", e);
        }
    }

    public static Environment getInstance() {
        if (singleton == null) {
            InputStream localProperties = Environment.class.getClassLoader().getResourceAsStream("interweb_local.properties");
            if (localProperties == null) {
                localProperties = Environment.class.getClassLoader().getResourceAsStream("interweb.properties");
            }

            singleton = new Environment(localProperties);
        }
        return singleton;
    }

    public static Environment getInstance(InputStream inputStream) {
        if (singleton == null) {
            singleton = new Environment(inputStream);
        }
        return singleton;
    }

    public AccessControll getAccessControll() {
        return accessControll;
    }

    public Properties getProperties() {
        return properties;
    }

    public Database getDatabase() {
        return database;
    }

    public Engine getEngine() {
        return engine;
    }
}