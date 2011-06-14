package de.l3s.interwebj.servlet.listener;


import javax.servlet.*;

import de.l3s.interwebj.core.*;
import de.l3s.interwebj.db.*;


public class ServletContextLifecycleListener
    implements ServletContextListener
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
		Environment environment = Environment.getInstance(configPath);
		Environment.logger.info("Starting InterWebJ up...");
		Engine engine = environment.getEngine();
		String connectorsDirPath = webinfRealPath + "/connectors";
		engine.loadConnectors(connectorsDirPath);
	}
	
}
