package de.l3s.interwebj.bean;


import java.io.*;
import java.util.*;

import javax.faces.bean.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.webutil.*;


@ManagedBean
@RequestScoped
public class ServicesBean
{
	
	private List<ConnectorWrapper> connectorWrappers;
	

	public ServicesBean()
	{
		Engine engine = Environment.getInstance().getEngine();
		connectorWrappers = new ArrayList<ConnectorWrapper>();
		for (ServiceConnector connector : engine.getConnectors())
		{
			if (connector.isConnectorRegistered())
			{
				ConnectorWrapper connectorWrapper = new ConnectorWrapper();
				connectorWrapper.setConnector(connector);
				connectorWrappers.add(connectorWrapper);
			}
		}
	}
	

	public String authenticate(Object obj)
	    throws InterWebException
	{
		String baseApiUrl = FacesUtils.getInterWebJBean().getBaseUrl();
		ConnectorWrapper connectorWrapper = (ConnectorWrapper) obj;
		ServiceConnector connector = connectorWrapper.getConnector();
		InterWebPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
		Parameters parameters = new Parameters();
		parameters.add(Parameters.IWJ_USER_ID, principal.getName());
		parameters.add(Parameters.IWJ_CONNECTOR_ID, connector.getName());
		String interwebjCallbackUrl = baseApiUrl + "callback?"
		                              + parameters.toQueryString();
		Environment.logger.debug("interwebjCallbackUrl: ["
		                         + interwebjCallbackUrl + "]");
		parameters = connector.authenticate(interwebjCallbackUrl);
		if (connectorWrapper.getKey() != null)
		{
			parameters.add(Parameters.USER_KEY, connectorWrapper.getKey());
		}
		if (connectorWrapper.getSecret() != null)
		{
			parameters.add(Parameters.USER_SECRET, connectorWrapper.getSecret());
		}
		String authorizationUrl = parameters.get(Parameters.AUTHORIZATION_URL);
		if (authorizationUrl != null)
		{
			Environment.logger.debug("redirecting to service authorization url: "
			                         + authorizationUrl);
			Engine engine = Environment.getInstance().getEngine();
			parameters.add(Parameters.CLIENT_TYPE, "SERVLET");
			engine.addPendingAuthorizationConnector(principal,
			                                        connector,
			                                        parameters);
			try
			{
				FacesUtils.getExternalContext().redirect(authorizationUrl);
			}
			catch (IOException e)
			{
				Environment.logger.error(e);
				throw new InterWebException(e);
			}
		}
		return null;
	}
	

	public List<ConnectorWrapper> getConnectorWrappers()
	    throws InterWebException
	{
		return connectorWrappers;
	}
	

	public boolean isUserAuthenticated(Object obj)
	    throws InterWebException
	{
		Engine engine = Environment.getInstance().getEngine();
		InterWebPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
		if (principal != null)
		{
			ConnectorWrapper connectorWrapper = (ConnectorWrapper) obj;
			ServiceConnector connector = connectorWrapper.getConnector();
			return engine.isUserAuthenticated(connector, principal);
		}
		return false;
	}
	

	public boolean isUserRegistrationRequired(Object obj)
	    throws InterWebException
	{
		ConnectorWrapper connectorWrapper = (ConnectorWrapper) obj;
		ServiceConnector connector = connectorWrapper.getConnector();
		return connector.isUserRegistrationDataRequired()
		       && !isUserAuthenticated(obj);
	}
	

	public String revoke(Object obj)
	    throws InterWebException
	{
		ConnectorWrapper connectorWrapper = (ConnectorWrapper) obj;
		ServiceConnector connector = connectorWrapper.getConnector();
		Environment.logger.debug("revoking user authentication");
		Engine engine = Environment.getInstance().getEngine();
		InterWebPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
		engine.setUserAuthCredentials(connector.getName(),
		                              principal,
		                              null,
		                              null);
		connector.revokeAuthentication();
		return null;
	}
}
