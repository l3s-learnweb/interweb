package de.l3s.interwebj.bean;


import java.net.*;

import javax.faces.bean.*;
import javax.faces.context.*;

import de.l3s.interwebj.core.*;


@ManagedBean(name = "interwebj")
@ApplicationScoped
public class InterWebJBean
{
	
	public URL getConfigUrl()
	    throws MalformedURLException
	{
		return FacesContext.getCurrentInstance().getExternalContext().getResource("/WEB-INF/config.xml");
	}
	

	public String getContext()
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
