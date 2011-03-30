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
	
	@SuppressWarnings("deprecation")
	public String getBaseUrl()
	{
		HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		URI currentUri = URI.create(HttpUtils.getRequestURL(req).toString());
		URI baseUri = currentUri.resolve("/" + getContextName() + "/");
		Environment.logger.debug("baseUri: " + baseUri);
		return baseUri.toASCIIString();
	}
	

	public String getContextName()
	{
		return FacesContext.getCurrentInstance().getExternalContext().getContextName();
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
