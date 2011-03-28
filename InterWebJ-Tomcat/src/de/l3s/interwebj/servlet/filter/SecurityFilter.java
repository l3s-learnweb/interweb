package de.l3s.interwebj.servlet.filter;


import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import de.l3s.interwebj.bean.*;
import de.l3s.interwebj.core.*;


public class SecurityFilter
    implements Filter
{
	
	public static final String CONFIG_PATH = "/WEB-INF/config.xml";
	public static final String LOGIN_PAGE = "/view/login.xhtml";
	
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
		String requestUrl = getRequestUrl(httpRequest);
		Environment.logger.debug("Requested URL: [" + requestUrl + "]");
		AccessControll accessControll = environment.getAccessControll();
		PrincipalBean principalBean = (PrincipalBean) httpRequest.getSession().getAttribute("principalBean");
		if (principalBean == null)
		{
			//			Environment.logger.debug("WARNING! PrincipalBean is NULL! Creating and storing new PrincipalBean instance");
			principalBean = new PrincipalBean();
			httpRequest.getSession().setAttribute("principalBean",
			                                      principalBean);
		}
		IWPrincipal principal = principalBean.getPrincipal();
		//		Environment.logger.debug("principal: " + principal);
		SessionBean sessionBean = (SessionBean) httpRequest.getSession().getAttribute("sessionBean");
		if (sessionBean == null)
		{
			//			Environment.logger.debug("WARNING! SessionBean is NULL! Creating and storing new SessionBean instance");
			try
			{
				sessionBean = new SessionBean();
				httpRequest.getSession().setAttribute("sessionBean",
				                                      sessionBean);
			}
			catch (InterWebException e)
			{
				e.printStackTrace();
				Environment.logger.error(e);
				chain.doFilter(request, response);
				return;
			}
		}
		String savedRequestUrl = sessionBean.getSavedRequestUrl();
		if (principal != null && savedRequestUrl != null)
		{
			sessionBean.setSavedRequestUrl(null);
			Environment.logger.debug("redirecting to: " + savedRequestUrl);
			httpResponse.sendRedirect(httpRequest.getContextPath()
			                          + savedRequestUrl);
			return;
		}
		boolean authorized = accessControll.isAuthorized(principal,
		                                                 requestUrl,
		                                                 null);
		if (!authorized)
		{
			if (!isLoginPage(requestUrl))
			{
				Environment.logger.debug("Login required. User: "
				                         + principal
				                         + " is not authorized to access the resource: "
				                         + requestUrl);
				Environment.logger.debug("saving requested URL: " + requestUrl);
				sessionBean.setSavedRequestUrl(requestUrl);
				httpResponse.sendRedirect(httpRequest.getContextPath()
				                          + LOGIN_PAGE);
				return;
			}
		}
		chain.doFilter(request, response);
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
		environment = Environment.getInstance();
	}
	

	private boolean isLoginPage(String requestUrl)
	{
		return "/view/login.xhtml".equals(requestUrl);
	}
	
}
