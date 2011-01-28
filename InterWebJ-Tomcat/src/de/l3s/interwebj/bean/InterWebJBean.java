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
	

	public IWEnvironment getEnvironment()
	    throws MalformedURLException
	{
		URL configUrl = FacesContext.getCurrentInstance().getExternalContext().getResource("/WEB-INF/config.xml");
		return IWEnvironment.getInstance(configUrl);
	}
	
}
