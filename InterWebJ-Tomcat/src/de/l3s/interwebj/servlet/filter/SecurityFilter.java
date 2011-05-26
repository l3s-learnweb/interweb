package de.l3s.interwebj.servlet.filter;


import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import de.l3s.interwebj.bean.*;
import de.l3s.interwebj.core.*;


public class SecurityFilter
    implements Filter
{
	
	public static final String LOGIN_PAGE = "/view/login.xhtml";
	public static final String START_PAGE = "/view/index.xhtml";
	
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
		SessionBean sessionBean = (SessionBean) httpRequest.getSession().getAttribute("sessionBean");
		if (sessionBean == null)
		{
			httpRequest.getSession(true);
			Environment.logger.debug("WARNING! SessionBean is NULL! Creating and storing new SessionBean instance");
			sessionBean = new SessionBean();
			httpRequest.getSession().setAttribute("sessionBean", sessionBean);
		}
		InterWebPrincipal principal = sessionBean.getPrincipal();
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
	

	@Override
	public void init(FilterConfig config)
	    throws ServletException
	{
		environment = Environment.getInstance();
	}
	

	private String getRequestUrl(HttpServletRequest httpRequest)
	{
		String requestURL = httpRequest.getServletPath();
		if (httpRequest.getPathInfo() != null)
		{
			requestURL += httpRequest.getPathInfo();
		}
		if (httpRequest.getQueryString() != null)
		{
			requestURL += "?" + httpRequest.getQueryString();
		}
		return requestURL;
	}
	

	private boolean isLoginPage(String requestUrl)
	{
		return "/view/login.xhtml".equals(requestUrl);
	}
	
}
