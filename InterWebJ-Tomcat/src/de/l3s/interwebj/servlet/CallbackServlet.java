package de.l3s.interwebj.servlet;


import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import de.l3s.interwebj.bean.*;
import de.l3s.interwebj.core.*;


/**
 * Servlet implementation class Logout
 */
public class CallbackServlet
    extends HttpServlet
{
	
	private static final long serialVersionUID = 6534209215912582685L;
	

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CallbackServlet()
	{
		super();
	}
	

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
	                     HttpServletResponse response)
	    throws ServletException, IOException
	{
		try
		{
			process(request, response);
		}
		catch (InterWebException e)
		{
			Environment.logger.error(e);
		}
	}
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
	                      HttpServletResponse response)
	    throws ServletException, IOException
	{
		try
		{
			process(request, response);
		}
		catch (InterWebException e)
		{
		}
	}
	

	public void process(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, InterWebException
	{
		Map<String, String[]> params = request.getParameterMap();
		SessionBean sessionBean = (SessionBean) request.getSession().getAttribute("sessionBean");
		sessionBean.processAuthenticationCallback(params);
		try
		{
			response.sendRedirect("/InterWebJ/view/services.xhtml");
		}
		catch (IOException e)
		{
			throw new InterWebException(e);
		}
	}
	
}
