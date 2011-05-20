package de.l3s.interwebj.bean;


import java.util.*;

import javax.faces.bean.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;


@ManagedBean
@SessionScoped
public class SessionBean
{
	
	private Map<ServiceConnector, Parameters> pendingAuthorizationConnectors;
	private InterWebPrincipal principal;
	private String savedRequestUrl;
	

	public SessionBean()
	    throws InterWebException
	{
		pendingAuthorizationConnectors = new HashMap<ServiceConnector, Parameters>();
	}
	

	public void addPendingAuthorizationConnector(ServiceConnector connector,
	                                             Parameters params)
	{
		pendingAuthorizationConnectors.put(connector, params);
	}
	

	public InterWebPrincipal getPrincipal()
	{
		return principal;
	}
	

	public String getSavedRequestUrl()
	{
		return savedRequestUrl;
	}
	

	public void processAuthenticationCallback(Map<String, String[]> params)
	    throws InterWebException
	{
		for (ServiceConnector connector : pendingAuthorizationConnectors.keySet())
		{
			Parameters parameters = pendingAuthorizationConnectors.get(connector);
			parameters.addMultivaluedParams(params);
			AuthCredentials authCredentials = connector.completeAuthentication(parameters);
			if (authCredentials != null)
			{
				pendingAuthorizationConnectors.remove(connector);
				Environment.logger.debug(connector.getName() + " authenticated");
				Engine engine = Environment.getInstance().getEngine();
				String userId = connector.getUserId(authCredentials);
				engine.setUserAuthCredentials(connector.getName(),
				                              principal,
				                              userId,
				                              authCredentials);
				Environment.logger.debug("authentication data saved");
				return;
			}
		}
	}
	

	public void setPrincipal(InterWebPrincipal principal)
	{
		this.principal = principal;
	}
	

	public void setSavedRequestUrl(String savedRequestUrl)
	{
		this.savedRequestUrl = savedRequestUrl;
	}
}
