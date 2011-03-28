package de.l3s.interwebj.bean;


import java.util.*;

import javax.faces.bean.*;

import com.sun.istack.internal.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.connector.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.webutil.*;


@ManagedBean
@SessionScoped
public class SessionBean
{
	
	@NotNull
	private List<String> selectedContentTypes;
	private List<String> selectedConnectors;
	private Map<ServiceConnector, Parameters> pendingAuthorizationConnectors;
	private String savedRequestUrl;
	

	public SessionBean()
	    throws InterWebException
	{
		Engine engine = Environment.getInstance().getEngine();
		PrincipalBean principalBean = FacesUtils.getPrincipalBean();
		IWPrincipal principal = (principalBean == null)
		    ? null : principalBean.getPrincipal();
		selectedContentTypes = new ArrayList<String>(engine.getContentTypes());
		selectedConnectors = new ArrayList<String>();
		for (ServiceConnector connector : engine.getConnectors())
		{
			if (connector.isRegistered() && principal != null
			    && engine.isUserAuthenticated(connector, principal))
			{
				selectedConnectors.add(connector.getName());
			}
		}
		pendingAuthorizationConnectors = new HashMap<ServiceConnector, Parameters>();
	}
	

	public void addPendingAuthorizationConnector(ServiceConnector connector,
	                                             Parameters params)
	{
		pendingAuthorizationConnectors.put(connector, params);
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
		for (ServiceConnector connector : pendingAuthorizationConnectors.keySet())
		{
			Parameters parameters = pendingAuthorizationConnectors.get(connector);
			parameters.add(params);
			AuthCredentials authCredentials = connector.completeAuthentication(parameters);
			if (authCredentials != null)
			{
				pendingAuthorizationConnectors.remove(connector);
				Environment.logger.debug(connector.getName() + " authenticated");
				Engine engine = Environment.getInstance().getEngine();
				IWPrincipal principal = FacesUtils.getPrincipalBean().getPrincipal();
				engine.setUserAuthCredentials(connector,
				                              principal,
				                              authCredentials);
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
