package de.l3s.interwebj.tomcat;


import java.security.*;

import javax.servlet.http.*;


public class IWRequestWrapper
    extends HttpServletRequestWrapper
{
	
	public IWRequestWrapper(HttpServletRequest request)
	{
		super(request);
	}
	

	@Override
	public String getRemoteUser()
	{
		String userName = null;
		Principal principal = getUserPrincipal();
		if (principal != null)
		{
			userName = principal.getName();
		}
		return userName;
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
	

	@Override
	public Principal getUserPrincipal()
	{
		return (Principal) getSession().getAttribute(IWSecurityFilter.SESSION_PRINCIPAL_KEY);
	}
	

	public String loadSavedRequestURL()
	{
		return (String) getSession().getAttribute(IWSecurityFilter.SAVED_URL);
	}
	

	public void saveRequestURL()
	{
		getSession().setAttribute(IWSecurityFilter.SAVED_URL, getRequestUrl());
	}
	

	public void setError(int code, String message)
	{
		setAttribute("interwebj.error.code", Integer.valueOf(code));
		setAttribute("interwebj.error.message", message);
	}
	

	public void setUserPrincipal(Principal principal)
	{
		getSession().setAttribute(IWSecurityFilter.SESSION_PRINCIPAL_KEY,
		                          principal);
	}
	
}
