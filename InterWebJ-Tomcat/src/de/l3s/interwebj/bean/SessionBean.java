package de.l3s.interwebj.bean;


import java.util.*;

import javax.faces.bean.*;

import com.sun.istack.internal.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.connector.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.util.*;


@ManagedBean
@SessionScoped
public class SessionBean
{
	
	@NotNull
	private List<String> selectedContentTypes;
	private List<String> selectedConnectors;
	private Set<ServiceConnector> awaitingAuthenticationConnectors;
	private String savedRequestUrl;
	

	public SessionBean()
	    throws InterWebException
	{
		Engine engine = Utils.getEngine();
		IWPrincipal principal = Utils.getPrincipalBean().getPrincipal();
		selectedContentTypes = new ArrayList<String>(engine.getContentTypes());
		for (ServiceConnector connector : engine.getConnectors())
		{
			if (engine.isConnectorRegistered(connector) && principal != null
			    && engine.isUserAuthenticated(connector, principal))
			{
				selectedConnectors.add(connector.getName());
			}
		}
		awaitingAuthenticationConnectors = new HashSet<ServiceConnector>();
	}
	

	public void addAwaitingAuthenticationConnectors(ServiceConnector connector)
	{
		awaitingAuthenticationConnectors.add(connector);
	}
	

	public String getSavedRequestUrl()
	{
		return savedRequestUrl;
	}
	

	public List<String> getSelectedConnectors()
	{
		return selectedConnectors;
	}
	

	public List<String> getSelectedContentTypes()
	{
		return selectedContentTypes;
	}
	

	public void processAuthenticationCallback(Map<String, String[]> params)
	    throws InterWebException
	{
		for (ServiceConnector connector : awaitingAuthenticationConnectors)
		{
			AuthData authData = connector.completeAuthentication(params);
			if (authData != null)
			{
				Environment.logger.debug(connector.getName() + " authenticated");
				Engine engine = Utils.getEngine();
				IWPrincipal principal = Utils.getPrincipalBean().getPrincipal();
				engine.setUserAuthData(connector, principal, authData);
				Environment.logger.debug("authentication data saved");
				return;
			}
		}
	}
	

	public void setSavedRequestUrl(String savedRequestUrl)
	{
		this.savedRequestUrl = savedRequestUrl;
	}
	

	public void setSelectedContentTypes(List<String> selectedContentTypes)
	{
		this.selectedContentTypes = selectedContentTypes;
	}
}
