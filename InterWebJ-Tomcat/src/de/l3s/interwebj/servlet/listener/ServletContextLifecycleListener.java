package de.l3s.interwebj.servlet.listener;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import de.l3s.interwebj.core.Engine;
import de.l3s.interwebj.core.Environment;
import de.l3s.interwebj.db.Database;

public class ServletContextLifecycleListener implements ServletContextListener
{

    @Override
    public void contextDestroyed(ServletContextEvent e)
    {
	Database database = Environment.getInstance().getDatabase();
	database.close();
    }

    @Override
    public void contextInitialized(ServletContextEvent e)
    {
	ServletContext servletContext = e.getServletContext();
	String webinfRealPath = servletContext.getRealPath("/WEB-INF");

	String configPath = webinfRealPath + "/config.xml";

	if(new File("E:\\webservicefolder").exists())
	{
	    configPath = webinfRealPath + "/config_local.xml";
	}
	else if(new File("C:\\").exists())
	{
	}
	else if(new File("/data2").exists())
	{
	}
	else
	{
	}

	Environment environment = Environment.getInstance(configPath);
	Environment.logger.info("Starting InterWebJ up...");
	Engine engine = environment.getEngine();
	String connectorsDirPath = webinfRealPath + "/connectors";
	engine.loadConnectors(servletContext.getRealPath(""), connectorsDirPath);
    }

}
