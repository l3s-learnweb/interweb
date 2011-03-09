package de.l3s.interwebj.servlet.filter;


import java.io.*;
import java.net.*;

import javax.faces.context.*;
import javax.servlet.*;
import javax.servlet.http.*;

import de.l3s.interwebj.bean.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.util.*;


public class SecurityFilter
    implements Filter
{
	
	public static final String CONFIG_PATH = "/WEB-INF/config.xml";
	public static final String LOGIN_PAGE = "/view/login.xhtml";
	
	private ServletContext servletContext;
	private Environment environment;
	

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
		if (!(request instanceof HttpServletRequest))
		{
			chain.doFilter(request, response);
			return;
		}
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		FacesContext fc = Utils.getFacesContext(httpRequest, httpResponse);
		String requestUrl = getRequestUrl(httpRequest);
		Environment.logger.debug("requested URL: " + requestUrl);
		AccessControll accessControll = environment.getAccessControll();
		if (accessControll.isPublicResource(requestUrl))
		{
			chain.doFilter(request, response);
			return;
		}
		IWPrincipal principal = Utils.getPrincipalBean(fc).getPrincipal();
		Environment.logger.debug("principal: " + principal);
		boolean authorized = accessControll.isAuthorized(principal,
		                                                 requestUrl,
		                                                 null);
		SessionBean sessionBean = (SessionBean) Utils.getManagedBean(fc,
		                                                             "sessionBean");
		String savedRequestUrl = sessionBean.getSavedRequestUrl();
		if (!authorized)
		{
			Environment.logger.debug("user: " + principal
			                         + " is not authorized for the resource: "
			                         + requestUrl);
			Environment.logger.debug("saving requested URL: " + requestUrl);
			sessionBean.setSavedRequestUrl(requestUrl);
			
			Environment.logger.debug("redirecting to: " + LOGIN_PAGE);
			httpResponse.sendRedirect(httpRequest.getContextPath() + LOGIN_PAGE);
		}
		else if (savedRequestUrl != null)
		{
			sessionBean.setSavedRequestUrl(null);
			Environment.logger.debug("redirecting to: " + savedRequestUrl);
			httpResponse.sendRedirect(httpRequest.getContextPath()
			                          + savedRequestUrl);
		}
		else
		{
			chain.doFilter(request, response);
		}
		//		IWRequestWrapper iwRequest = new IWRequestWrapper((HttpServletRequest) request);
		//		IWResponseWrapper iwResponse = new IWResponseWrapper((HttpServletResponse) response);
		//		String requestURL = iwRequest.getRequestUrl();
		//		if ("/j_security_check".equals(requestURL))
		//		{
		//			String userName = iwRequest.getParameter("j_username");
		//			String userPassword = iwRequest.getParameter("j_password");
		//			if (userName != null && userPassword != null)
		//			{
		//				if (iwRequest.getUserPrincipal() != null)
		//				{
		//					doLogout(iwRequest);
		//				}
		//				Database iwDatabase = environment.getDatabase();
		//				IWPrincipal principal = iwDatabase.authenticate(userName,
		//				                                                userPassword);
		//				if (principal == null)
		//				{
		//					iwRequest.setError(HttpServletResponse.SC_UNAUTHORIZED,
		//					                   "Incorrect login. Please check your username/password.");
		//					servletContext.getRequestDispatcher(ERROR_PAGE).forward(iwRequest,
		//					                                                        iwResponse);
		//				}
		//				else
		//				{
		//					iwRequest.setUserPrincipal(principal);
		//					String savedRequestURL = iwRequest.loadSavedRequestURL();
		//					if (savedRequestURL == null)
		//					{
		//						iwResponse.sendRedirect(iwRequest.getContextPath()
		//						                        + DEFAULT_PAGE);
		//					}
		//					else
		//					{
		//						iwResponse.sendRedirect(iwRequest.getContextPath()
		//						                        + savedRequestURL);
		//					}
		//					return;
		//				}
		//			}
		//			iwResponse.sendRedirect(iwRequest.getContextPath() + LOGIN_PAGE);
		//		}
		//		IWPrincipal principal = (IWPrincipal) iwRequest.getUserPrincipal();
		//		AccessControll iwAccessControll = environment.getAccessControll();
		//		boolean authorized = iwAccessControll.isAuthorized(principal,
		//		                                                   requestURL,
		//		                                                   null);
		//		if (!authorized)
		//		{
		//			if (principal == null)
		//			{
		//				iwRequest.saveRequestURL();
		//				iwRequest.setError(HttpServletResponse.SC_UNAUTHORIZED,
		//				                   "You are not authorized.");
		//			}
		//			else
		//			{
		//				iwRequest.setError(HttpServletResponse.SC_FORBIDDEN,
		//				                   "User ("
		//				                       + iwRequest.getRemoteUser()
		//				                       + ") does not have necessary privileges.");
		//			}
		//			servletContext.getRequestDispatcher(ERROR_PAGE).forward(iwRequest,
		//			                                                        iwResponse);
		//		}
	}
	

	private Environment getEnvironment(ServletContext servletContext)
	    throws MalformedURLException, InterWebException
	{
		Environment env = (Environment) servletContext.getAttribute("interwebj.env");
		if (env == null)
		{
			URL configUrl = servletContext.getResource(CONFIG_PATH);
			env = Environment.getInstance(configUrl);
			servletContext.setAttribute("interwebj.env", env);
		}
		return env;
	}
	

	private String getRequestUrl(HttpServletRequest httpRequest)
	{
		String requestURL = httpRequest.getServletPath();
		if (httpRequest.getPathInfo() != null)
		{
			requestURL += httpRequest.getPathInfo();
		}
		return requestURL;
	}
	

	@Override
	public void init(FilterConfig config)
	    throws ServletException
	{
		servletContext = config.getServletContext();
		try
		{
			environment = getEnvironment(servletContext);
		}
		catch (MalformedURLException e)
		{
			throw new ServletException(e);
		}
		catch (InterWebException e)
		{
			throw new ServletException(e);
		}
	}
	
}
