package de.l3s.interwebj.servlet;


import javax.servlet.*;
import javax.servlet.http.*;

import de.l3s.interwebj.core.*;


/**
 * Servlet implementation class StartupServlet
 */
public class StartupServlet
    extends HttpServlet
{
	
	private static final long serialVersionUID = -2871157064917697931L;
	

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public StartupServlet()
	{
		super();
	}
	

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	@Override
	public void init(ServletConfig config)
	    throws ServletException
	{
		String contextRealPath = config.getServletContext().getRealPath("/");
		String configPath = contextRealPath + "config/config.xml";
		Environment environment = Environment.getInstance(configPath);
		Environment.logger.info("Starting InterWebJ up...");
		Engine engine = environment.getEngine();
		String connectorsDirPath = contextRealPath + "connectors";
		engine.loadConnectors(connectorsDirPath);
	}
	
}
