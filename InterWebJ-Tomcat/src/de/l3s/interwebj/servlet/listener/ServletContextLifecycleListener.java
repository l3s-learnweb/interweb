package de.l3s.interwebj.servlet.listener;

import java.io.InputStream;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import de.l3s.interwebj.core.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServletContextLifecycleListener implements ServletContextListener
{
	private static final Logger log = LogManager.getLogger(ServletContextLifecycleListener.class);

    @Override
    public void contextDestroyed(ServletContextEvent e)
    {
		Environment.getInstance().getDatabase().close();
    }

    @Override
    public void contextInitialized(ServletContextEvent e)
    {
		InputStream localProperties = getClass().getClassLoader().getResourceAsStream("config_local.xml");
		if(localProperties == null)
			localProperties = getClass().getClassLoader().getResourceAsStream("config.xml");

		Environment environment = Environment.getInstance(localProperties);
		log.info("Starting InterWebJ up...");
		environment.getEngine().loadConnectors();
    }

}
