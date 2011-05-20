package de.l3s.interwebj.servlet.filter;


import java.io.*;

import javax.servlet.*;


public class EncodingFilter
    implements Filter
{
	
	@Override
	public void destroy()
	{
		// Do nothing
	}
	

	@Override
	public void doFilter(ServletRequest request,
	                     ServletResponse response,
	                     FilterChain chain)
	    throws IOException, ServletException
	{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		chain.doFilter(request, response);
	}
	

	@Override
	public void init(FilterConfig arg0)
	    throws ServletException
	{
		// Do nothing
	}
	
}
