package de.l3s.interwebj.servlet.listener;


import javax.servlet.*;

import de.l3s.interwebj.core.*;
import de.l3s.interwebj.db.*;


public class ServletContextLifecycleListener
    implements ServletContextListener
{
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0)
	{
		Database database = Environment.getInstance().getDatabase();
		database.close();
	}
	

	@Override
	public void contextInitialized(ServletContextEvent arg0)
	{
		// Do nothing
	}
	
}
