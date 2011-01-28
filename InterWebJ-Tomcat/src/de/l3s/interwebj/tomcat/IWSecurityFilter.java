package de.l3s.interwebj.tomcat;


import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;

import de.l3s.interwebj.core.*;
import de.l3s.interwebj.db.*;


public class IWSecurityFilter
    implements Filter
{
	
	public static final String CONFIG_PATH = "/WEB-INF/config.xml";
	public static final String SESSION_PRINCIPAL_KEY = IWRequestWrapper.class.getName()
	                                                   + ".PRINCIPAL";
	public static final String SAVED_URL = IWRequestWrapper.class.getName()
	                                       + ".SAVED_URL";
	
	public static String DEFAULT_PAGE = "/index.jsp";
	public static String LOGIN_PAGE = "/login.jsp";
	public static String ERROR_PAGE = "/error.jsp";
	
	private ServletContext servletContext;
	private IWEnvironment iwEnvironment;
	

	@Override
	public void destroy()
	{
	}
	

	@Override
	public void doFilter(ServletRequest request,
	                     ServletResponse response,
	                     FilterChain chain)
	    throws IOException, ServletException
	{
		IWRequestWrapper iwRequest = new IWRequestWrapper((HttpServletRequest) request);
		IWResponseWrapper iwResponse = new IWResponseWrapper((HttpServletResponse) response);
		String requestURL = iwRequest.getRequestUrl();
		if ("/logout".equals(requestURL))
		{
			doLogout(iwRequest);
			iwResponse.sendRedirect(iwRequest.getContextPath() + DEFAULT_PAGE);
			return;
		}
		if ("/j_security_check".equals(requestURL))
		{
			String userName = iwRequest.getParameter("j_username");
			String userPassword = iwRequest.getParameter("j_password");
			if (userName != null && userPassword != null)
			{
				if (iwRequest.getUserPrincipal() != null)
				{
					doLogout(iwRequest);
				}
				IWDatabase iwDatabase = iwEnvironment.getDatabase();
				IWPrincipal principal = iwDatabase.authenticate(userName,
				                                                userPassword);
				if (principal == null)
				{
					iwRequest.setError(HttpServletResponse.SC_UNAUTHORIZED,
					                   "Incorrect login. Please check your username/password.");
					servletContext.getRequestDispatcher(ERROR_PAGE).forward(iwRequest,
					                                                        iwResponse);
				}
				else
				{
					iwRequest.setUserPrincipal(principal);
					String savedRequestURL = iwRequest.loadSavedRequestURL();
					if (savedRequestURL == null)
					{
						iwResponse.sendRedirect(iwRequest.getContextPath()
						                        + DEFAULT_PAGE);
					}
					else
					{
						iwResponse.sendRedirect(iwRequest.getContextPath()
						                        + savedRequestURL);
					}
					return;
				}
			}
			iwResponse.sendRedirect(iwRequest.getContextPath() + LOGIN_PAGE);
		}
		IWPrincipal principal = (IWPrincipal) iwRequest.getUserPrincipal();
		IWAccessControll iwAccessControll = iwEnvironment.getAccessControll();
		boolean authorized = iwAccessControll.isAuthorized(principal,
		                                                   requestURL,
		                                                   null);
		if (!authorized)
		{
			if (principal == null)
			{
				iwRequest.saveRequestURL();
				iwRequest.setError(HttpServletResponse.SC_UNAUTHORIZED,
				                   "You are not authorized.");
			}
			else
			{
				iwRequest.setError(HttpServletResponse.SC_FORBIDDEN,
				                   "User ("
				                       + iwRequest.getRemoteUser()
				                       + ") does not have necessary privileges.");
			}
			servletContext.getRequestDispatcher(ERROR_PAGE).forward(iwRequest,
			                                                        iwResponse);
		}
		chain.doFilter(iwRequest, iwResponse);
	}
	

	private void doLogout(IWRequestWrapper httpRequest)
	{
		httpRequest.getSession().invalidate();
		httpRequest.getSession(true);
	}
	

	private IWEnvironment getEnvironment(ServletContext servletContext)
	    throws MalformedURLException
	{
		IWEnvironment env = (IWEnvironment) servletContext.getAttribute("interwebj.env");
		if (env == null)
		{
			URL configUrl = servletContext.getResource(CONFIG_PATH);
			env = IWEnvironment.getInstance(configUrl);
			servletContext.setAttribute("interwebj.env", env);
		}
		return env;
	}
	

	@Override
	public void init(FilterConfig config)
	    throws ServletException
	{
		servletContext = config.getServletContext();
		try
		{
			iwEnvironment = getEnvironment(servletContext);
		}
		catch (MalformedURLException e)
		{
			throw new ServletException(e);
		}
	}
	
}
