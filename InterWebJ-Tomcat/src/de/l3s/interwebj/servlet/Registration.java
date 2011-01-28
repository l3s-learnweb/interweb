package de.l3s.interwebj.servlet;


import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import de.l3s.interwebj.bean.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.db.*;
import de.l3s.interwebj.tomcat.*;


/**
 * Servlet implementation class Registration
 */
public class Registration
    extends HttpServlet
{
	
	private static final long serialVersionUID = 1L;
	

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Registration()
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
		IWEnvironment iwEnvironment = (IWEnvironment) getServletContext().getAttribute("interwebj.env");
		IWDatabase iwDatabase = iwEnvironment.getDatabase();
		RegistrationBean registrationBean = new RegistrationBean();
		registrationBean.setUsername(request.getParameter("userName"));
		registrationBean.setPassword(request.getParameter("password"));
		registrationBean.setPassword2(request.getParameter("password2"));
		registrationBean.setEmail(request.getParameter("email"));
		if (registrationBean.validate(iwDatabase))
		{
			IWPrincipal principal = new IWPrincipal(registrationBean.getUsername(),
			                                        registrationBean.getEmail());
			principal.addRole("user");
			boolean added = iwDatabase.addPrincipal(principal,
			                                        registrationBean.getPassword());
			IWRequestWrapper iwRequest = new IWRequestWrapper(request);
			if (added)
			{
				principal = iwDatabase.authenticate(registrationBean.getUsername(),
				                                    registrationBean.getPassword());
				iwRequest.setUserPrincipal(principal);
				response.sendRedirect(iwRequest.getContextPath()
				                      + "/profile.jsp");
			}
			else
			{
				iwRequest.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				                   "An error occured during the registration.");
				getServletContext().getRequestDispatcher(IWSecurityFilter.ERROR_PAGE).forward(iwRequest,
				                                                                              response);
			}
		}
		else
		{
			request.setAttribute("interwebj.registartion.bean",
			                     registrationBean);
			getServletContext().getRequestDispatcher("/register.jsp").forward(request,
			                                                                  response);
		}
	}
}
