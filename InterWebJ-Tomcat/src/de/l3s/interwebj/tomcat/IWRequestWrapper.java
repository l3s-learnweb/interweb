package de.l3s.interwebj.tomcat;


import javax.servlet.http.*;


public class IWRequestWrapper
    extends HttpServletRequestWrapper
{
	
	public IWRequestWrapper(HttpServletRequest request)
	{
		super(request);
	}
	

	public String getRequestUrl()
	{
		String requestURL = getServletPath();
		if (getPathInfo() != null)
		{
			requestURL += getPathInfo();
		}
		return requestURL;
	}
	
}
