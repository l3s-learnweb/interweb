package de.l3s.interwebj.bean;


import java.net.*;

import javax.faces.bean.*;
import javax.servlet.http.*;

import de.l3s.interwebj.core.*;
import de.l3s.interwebj.webutil.*;


@ManagedBean(name = "interwebj")
@ApplicationScoped
public class InterWebJBean
{
	
	@SuppressWarnings("deprecation")
	public String getBaseUrl()
	{
		HttpServletRequest req = (HttpServletRequest) FacesUtils.getExternalContext().getRequest();
		URI currentUri = URI.create(HttpUtils.getRequestURL(req).toString());
		URI baseUri = currentUri.resolve(getContextPath() + "/");
		return baseUri.toASCIIString();
	}
	

	public String getContextPath()
	{
		return FacesUtils.getContextPath();
	}
	

	public Environment getEnvironment()
	{
		return Environment.getInstance();
	}
	

	public String getServiceName()
	{
		return Environment.INTERWEBJ_SERVICE_NAME;
	}
}
