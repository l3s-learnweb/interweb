package de.l3s.interwebj.bean;


import java.net.*;

import javax.faces.bean.*;
import javax.faces.context.*;
import javax.servlet.http.*;

import de.l3s.interwebj.core.*;


@ManagedBean(name = "interwebj")
@ApplicationScoped
public class InterWebJBean
{
	
	public String getBaseUrl()
	{
		HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		URI currentUri = URI.create(HttpUtils.getRequestURL(req).toString());
		URI baseUri = currentUri.resolve("/" + getContextName() + "/");
		Environment.logger.debug("baseUri: " + baseUri);
		return baseUri.toASCIIString();
	}
	

	public URL getConfigUrl()
	    throws MalformedURLException
	{
		return FacesContext.getCurrentInstance().getExternalContext().getResource("/WEB-INF/config.xml");
	}
	

	public String getContextName()
	{
		return FacesContext.getCurrentInstance().getExternalContext().getContextName();
	}
	

	public Environment getEnvironment()
	    throws InterWebException
	{
		try
		{
			URL configUrl = FacesContext.getCurrentInstance().getExternalContext().getResource("/WEB-INF/config.xml");
			return Environment.getInstance(configUrl);
		}
		catch (MalformedURLException e)
		{
			throw new InterWebException(e);
		}
	}
	

	public String getServiceName()
	{
		return Environment.INTERWEBJ_SERVICE_NAME;
	}
}
