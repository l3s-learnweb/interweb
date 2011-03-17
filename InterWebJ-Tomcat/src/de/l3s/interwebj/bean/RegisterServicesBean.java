package de.l3s.interwebj.bean;


import java.util.*;

import javax.faces.bean.*;

import com.sun.istack.internal.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.connector.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.util.*;


@ManagedBean
@RequestScoped
public class RegisterServicesBean
{
	
	@NotNull
	private String key;
	@NotNull
	private String secret;
	

	public String getKey()
	{
		return key;
	}
	

	public String getRowClasses()
	    throws InterWebException
	{
		List<ServiceConnector> connectors = Utils.getEngine().getConnectors();
		StringBuilder sb = new StringBuilder();
		for (ServiceConnector connector : connectors)
		{
			sb.append(connector.requestRegistrationData()
			    ? "show," : "hide,");
		}
		return sb.toString();
	}
	

	public String getSecret()
	{
		return secret;
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
			Engine engine = Utils.getEngine();
			AuthCredentials authCredentials = new AuthCredentials(key, secret);
			Environment.logger.debug("registering " + authCredentials);
			engine.setConsumerAuthCredentials(connector, authCredentials);
		}
		catch (Exception e)
		{
			return "failed";
		}
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
			Environment.logger.debug("unregistering");
			Engine engine = Utils.getEngine();
			engine.setConsumerAuthCredentials(connector, null);
		}
		catch (Exception e)
		{
			return "failed";
		}
		return "success";
	}
	
}
