package de.l3s.interwebj.tomcat.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.l3s.interwebj.tomcat.bean.SessionBean;
import de.l3s.interwebj.core.core.AccessControll;
import de.l3s.interwebj.core.core.Environment;
import de.l3s.interwebj.core.core.InterWebPrincipal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SecurityFilter implements Filter
{
	private static final Logger log = LogManager.getLogger(SecurityFilter.class);

    public static final String LOGIN_PAGE = "/view/login.xhtml";
    public static final String START_PAGE = "/view/index.xhtml";

    private Environment environment;

    @Override
    public void destroy()
    {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
	if(!(request instanceof HttpServletRequest))
	{
	    chain.doFilter(request, response);
	    return;
	}
	HttpServletRequest httpRequest = (HttpServletRequest) request;
	HttpServletResponse httpResponse = (HttpServletResponse) response;
	String requestUrl = getRequestUrl(httpRequest);
	//		Environment.logger.debug("Requested URL: [" + requestUrl + "]");

	AccessControll accessControll = environment.getAccessControll();
	SessionBean sessionBean = (SessionBean) httpRequest.getSession().getAttribute("sessionBean");
	if(sessionBean == null)
	{
	    httpRequest.getSession(true);
	    log.info("WARNING! SessionBean is NULL! Creating and storing new SessionBean instance");
	    sessionBean = new SessionBean();
	    httpRequest.getSession().setAttribute("sessionBean", sessionBean);
	}
	InterWebPrincipal principal = sessionBean.getPrincipal();
	boolean authorized = accessControll.isAuthorized(principal, requestUrl, null);
	if(!authorized)
	{
	    if(!isLoginPage(requestUrl))
	    {
		log.info("Login required. User: " + principal + " is not authorized to access the resource: " + requestUrl);
		log.info("saving requested URL: " + requestUrl);
		sessionBean.setSavedRequestUrl(requestUrl);
		httpResponse.sendRedirect(httpRequest.getContextPath() + LOGIN_PAGE);
		return;
	    }
	}
	chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig config) throws ServletException
    {
	environment = Environment.getInstance();
    }

    private String getRequestUrl(HttpServletRequest httpRequest)
    {
	String requestURL = httpRequest.getServletPath();
	if(httpRequest.getPathInfo() != null)
	{
	    requestURL += httpRequest.getPathInfo();
	}
	if(httpRequest.getQueryString() != null)
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
