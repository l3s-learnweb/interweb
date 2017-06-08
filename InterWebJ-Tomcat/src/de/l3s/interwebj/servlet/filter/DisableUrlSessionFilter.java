package de.l3s.interwebj.servlet.filter;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Servlet Filter implementation class DisableUrlSessionFilter
 */
public class DisableUrlSessionFilter implements Filter
{

    /**
     * @see Filter#destroy()
     */
    public void destroy()
    {
    }

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
	if(!(request instanceof HttpServletRequest))
	{
	    chain.doFilter(request, response);
	    return;
	}
	HttpServletRequest httpRequest = (HttpServletRequest) request;
	HttpServletResponse httpResponse = (HttpServletResponse) response;
	if(httpRequest.isRequestedSessionIdFromURL())
	{
	    HttpSession session = httpRequest.getSession();
	    if(session != null)
	    {
		session.invalidate();
	    }
	}
	HttpServletResponseWrapper wrappedResponse = new HttpServletResponseWrapper(httpResponse)
	{

	    @Override
	    public String encodeRedirectUrl(String url)
	    {
		return url;
	    }

	    @Override
	    public String encodeRedirectURL(String url)
	    {
		return url;
	    }

	    @Override
	    public String encodeUrl(String url)
	    {
		return url;
	    }

	    @Override
	    public String encodeURL(String url)
	    {
		return url;
	    }
	};
	chain.doFilter(request, wrappedResponse);
    }

    /**
     * @see Filter#init(FilterConfig)
     */
    public void init(FilterConfig fConfig) throws ServletException
    {
    }

}
