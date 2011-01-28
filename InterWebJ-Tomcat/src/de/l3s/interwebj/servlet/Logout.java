package de.l3s.interwebj.servlet;


import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;


/**
 * Servlet implementation class Logout
 */
public class Logout
    extends HttpServlet
{
	
	private static final long serialVersionUID = 1L;
	

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Logout()
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
		process(request, response);
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
		process(request, response);
	}
	

	public void process(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException
	{
		HttpSession session = request.getSession();
		session.invalidate();
		response.sendRedirect(request.getContextPath());
	}
	
}
