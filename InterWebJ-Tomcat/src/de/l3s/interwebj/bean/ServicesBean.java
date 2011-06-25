package de.l3s.interwebj.bean;


import java.io.*;
import java.util.*;

import javax.faces.application.*;
import javax.faces.bean.*;

import org.apache.commons.lang.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.db.*;
import de.l3s.interwebj.webutil.*;


@ManagedBean
@RequestScoped
public class ServicesBean
{
	
	private Engine engine;
	private Database database;
	private InterWebPrincipal principal;
	private List<ConnectorWrapper> connectorWrappers;
	private List<ConnectorWrapper> awaitingConnectorWrappers;
	private String error;
	

	public ServicesBean()
	    throws InterWebException
	{
		engine = Environment.getInstance().getEngine();
		database = Environment.getInstance().getDatabase();
		principal = FacesUtils.getSessionBean().getPrincipal();
		connectorWrappers = new ArrayList<ConnectorWrapper>();
		awaitingConnectorWrappers = new ArrayList<ConnectorWrapper>();
		for (ServiceConnector connector : engine.getConnectors())
		{
			if (connector.isRegistered()
			    && connector.isUserRegistrationRequired())
			{
				ConnectorWrapper connectorWrapper = new ConnectorWrapper();
				connectorWrapper.setConnector(connector);
				connectorWrappers.add(connectorWrapper);
				if (!isUserAuthenticated(connectorWrapper))
				{
					awaitingConnectorWrappers.add(connectorWrapper);
				}
			}
		}
		Parameters parameters = new Parameters();
		parameters.addMultivaluedParams(FacesUtils.getRequest().getParameterMap());
		if (parameters.hasParameter(Parameters.ERROR))
		{
			error = parameters.get(Parameters.ERROR);
		}
	}
	

	public String authenticate(Object obj)
	    throws InterWebException
	{
		String baseApiUrl = FacesUtils.getInterWebJBean().getBaseUrl();
		ConnectorWrapper connectorWrapper = (ConnectorWrapper) obj;
		ServiceConnector connector = connectorWrapper.getConnector();
		Parameters parameters = new Parameters();
		parameters.add(Parameters.IWJ_USER_ID, principal.getName());
		parameters.add(Parameters.IWJ_CONNECTOR_ID, connector.getName());
		parameters.add(Parameters.CLIENT_TYPE, "servlet");
		String interwebjCallbackUrl = baseApiUrl + "callback?"
		                              + parameters.toQueryString();
		Environment.logger.info("interwebjCallbackUrl: ["
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
			Environment.logger.info("redirecting to service authorization url: "
			                        + authorizationUrl);
			engine.addPendingAuthorizationConnector(principal,
			                                        connector,
			                                        parameters);
			try
			{
				FacesUtils.getExternalContext().redirect(authorizationUrl);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				Environment.logger.severe(e.getMessage());
				FacesUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e);
			}
		}
		return "success";
	}
	

	public List<ConnectorWrapper> getAwaitingConnectorWrappers()
	    throws InterWebException
	{
		return awaitingConnectorWrappers;
	}
	

	public List<ConnectorWrapper> getConnectorWrappers()
	    throws InterWebException
	{
		return connectorWrappers;
	}
	

	public String getError()
	{
		return error;
	}
	

	public boolean isUserAuthenticated(Object obj)
	    throws InterWebException
	{
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
		Environment.logger.info("revoking user authentication");
		engine.setUserAuthCredentials(connector.getName(),
		                              principal,
		                              null,
		                              null);
		connector.revokeAuthentication();
		return "success";
	}
	

	public String save(Object obj)
	{
		ConnectorWrapper connectorWrapper = (ConnectorWrapper) obj;
		ServiceConnector connector = connectorWrapper.getConnector();
		String key = StringUtils.isEmpty(connectorWrapper.getKey())
		    ? null : connectorWrapper.getKey();
		String secret = StringUtils.isEmpty(connectorWrapper.getSecret())
		    ? null : connectorWrapper.getSecret();
		AuthCredentials authCredentials = new AuthCredentials(key, secret);
		try
		{
			String userId = connector.getUserId(authCredentials);
			database.saveUserAuthCredentials(connector.getName(),
			                                 principal.getName(),
			                                 userId,
			                                 authCredentials);
		}
		catch (InterWebException e)
		{
			e.printStackTrace();
			Environment.logger.severe(e.getMessage());
			FacesUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e);
		}
		return "success";
	}
}
