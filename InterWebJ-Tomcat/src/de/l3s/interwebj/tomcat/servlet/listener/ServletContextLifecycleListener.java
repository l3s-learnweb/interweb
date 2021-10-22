package de.l3s.interwebj.tomcat.servlet.listener;

import java.io.InputStream;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.l3s.interwebj.core.core.Environment;

public class ServletContextLifecycleListener implements ServletContextListener {
    private static final Logger log = LogManager.getLogger(ServletContextLifecycleListener.class);

    @Override
    public void contextDestroyed(ServletContextEvent e) {
        Environment.getInstance().getDatabase().close();
    }

    @Override
    public void contextInitialized(ServletContextEvent e) {
        InputStream localProperties = getClass().getClassLoader().getResourceAsStream("interweb_local.properties");
        if (localProperties == null) {
            localProperties = getClass().getClassLoader().getResourceAsStream("interweb.properties");
        }

        Environment environment = Environment.getInstance(localProperties);
        log.info("Starting InterWebJ up...");
        environment.getEngine().loadConnectors();
    }

}
