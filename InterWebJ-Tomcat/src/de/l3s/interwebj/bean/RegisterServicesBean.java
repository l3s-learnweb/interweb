package de.l3s.interwebj.bean;


import java.util.*;

import javax.faces.bean.*;
import javax.servlet.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.webutil.*;


@ManagedBean
@RequestScoped
public class RegisterServicesBean
{
	
	private String key;
	private String secret;
	

	public List<ServiceConnector> getConnectors()
	{
		Engine engine = Environment.getInstance().getEngine();
		return engine.getConnectors();
	}
	

	public String getKey()
	{
		return null;
	}
	

	public String getRowClasses()
	    throws InterWebException
	{
		Engine engine = Environment.getInstance().getEngine();
		List<ServiceConnector> connectors = engine.getConnectors();
		StringBuilder sb = new StringBuilder();
		for (ServiceConnector connector : connectors)
		{
			sb.append(connector.isRegistrationRequired()
			    ? "show," : "hide,");
		}
		return sb.toString();
	}
	

	public String getSecret()
	{
		return null;
	}
	

	public boolean isRegistered(Object connector)
	    throws InterWebException
	{
		return ((ServiceConnector) connector).isRegistered();
	}
	

	public String register(ServiceConnector connector)
	{
		try
		{
			Engine engine = Environment.getInstance().getEngine();
			AuthCredentials authCredentials = new AuthCredentials(key, secret);
			Environment.logger.debug("registering connector: ["
			                         + connector.getName()
			                         + "] with credentials: " + authCredentials);
			connector.setAuthCredentials(authCredentials);
			engine.setConsumerAuthCredentials(connector.getName(),
			                                  authCredentials);
		}
		catch (Exception e)
		{
			return "failed";
		}
		return "success";
	}
	

	public String reload()
	{
		Environment.logger.debug("Reloading installed connectors...");
		ServletContext servletContext = (ServletContext) FacesUtils.getExternalContext().getContext();
		String contextRealPath = servletContext.getRealPath("/");
		String connectorsDirPath = contextRealPath + "connectors";
		Engine engine = Environment.getInstance().getEngine();
		engine.loadConnectors(connectorsDirPath);
		return "success";
	}
	

	public void setKey(String key)
	{
		this.key = key;
	}
	

	public void setSecret(String secret)
	{
		this.secret = secret;
	}
	

	public String unregister(ServiceConnector connector)
	{
		try
		{
			Environment.logger.debug("unregistering connector: ["
			                         + connector.getName() + "]");
			Engine engine = Environment.getInstance().getEngine();
			connector.setAuthCredentials(null);
			engine.setConsumerAuthCredentials(connector.getName(), null);
		}
		catch (Exception e)
		{
			return "failed";
		}
		return "success";
	}
	
}
