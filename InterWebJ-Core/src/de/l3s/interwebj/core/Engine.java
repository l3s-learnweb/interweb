package de.l3s.interwebj.core;


import java.util.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.connector.*;
import de.l3s.interwebj.db.*;
import de.l3s.interwebj.query.*;


public class Engine
{
	
	private Map<String, ServiceConnector> connectors;
	private Set<String> contentTypes;
	private Database database;
	

	public Engine(Database database)
	    throws InterWebException
	{
		this.database = database;
		init();
	}
	

	private void addConnector(ServiceConnector connector)
	{
		AuthCredentials authCredentials = getConsumerAuthCredentials(connector);
		connector.setConsumerAuthCredentials(authCredentials);
		contentTypes.addAll(connector.getContentTypes());
		connectors.put(connector.getName(), connector);
	}
	

	public ServiceConnector getConnector(String connectorName)
	{
		return connectors.get(connectorName);
	}
	

	public List<ServiceConnector> getConnectors()
	{
		List<ServiceConnector> connectorList = new LinkedList<ServiceConnector>();
		Set<String> keys = connectors.keySet();
		for (String key : keys)
		{
			connectorList.add(connectors.get(key));
		}
		return connectorList;
	}
	

	public AuthCredentials getConsumerAuthCredentials(ServiceConnector connector)
	{
		return database.readConsumerAuthCredentials(connector.getName(),
		                                            Environment.INTERWEBJ_SERVICE_NAME);
	}
	

	public List<String> getContentTypes()
	{
		return new LinkedList<String>(contentTypes);
	}
	

	public AuthCredentials getUserAuthCredentials(ServiceConnector connector,
	                                              IWPrincipal principal)
	{
		return database.readUserAuthCredentials(connector.getName(),
		                                        principal.getName());
	}
	

	private void init()
	    throws InterWebException
	{
		connectors = new TreeMap<String, ServiceConnector>();
		contentTypes = new TreeSet<String>();
		loadConnectors();
	}
	

	public boolean isUserAuthenticated(ServiceConnector connector,
	                                   IWPrincipal principal)
	{
		return getUserAuthCredentials(connector, principal) != null;
	}
	

	private void loadConnectors()
	    throws InterWebException
	{
		// TODO: stub
		addConnector(new FlickrConnector(null));
		addConnector(new YouTubeConnector(null));
	}
	

	public QueryResult search(Query query, IWPrincipal principal)
	    throws InterWebException
	{
		QueryResult queryResult = new QueryResult(query);
		List<ServiceConnector> connectorList = getConnectors();
		Environment.logger.debug("Query: " + query.getQuery());
		Environment.logger.debug("Principal: " + principal);
		for (ServiceConnector connector : connectorList)
		{
			
			if (connector.isRegistered()
			    && isUserAuthenticated(connector, principal))
			{
				AuthCredentials authCredentials = getUserAuthCredentials(connector,
				                                                         principal);
				Environment.logger.debug("Connector: " + connector.getName()
				                         + " " + authCredentials);
				queryResult.addQueryResult(connector.get(query, authCredentials));
			}
			else
			{
				if (!connector.isRegistered())
				{
					Environment.logger.debug("connector is not registered");
				}
				if (!isUserAuthenticated(connector, principal))
				{
					Environment.logger.debug("user service is not authenticated");
				}
			}
		}
		return queryResult;
	}
	

	public void setConsumerAuthCredentials(ServiceConnector connector,
	                                       AuthCredentials consumerAuthCredentials)
	{
		database.saveConsumer(connector.getName(),
		                      Environment.INTERWEBJ_SERVICE_NAME,
		                      consumerAuthCredentials);
		connector.setConsumerAuthCredentials(consumerAuthCredentials);
	}
	

	public void setUserAuthCredentials(ServiceConnector connector,
	                                   IWPrincipal principal,
	                                   AuthCredentials consumerAuthCredentials)
	{
		database.saveUserAuthCredentials(connector.getName(),
		                                 principal.getName(),
		                                 consumerAuthCredentials);
	}
	

	public void upload(byte[] data, IWPrincipal principal)
	    throws InterWebException
	{
		List<ServiceConnector> connectorList = getConnectors();
		for (ServiceConnector connector : connectorList)
		{
			if (connector.isRegistered()
			    && isUserAuthenticated(connector, principal))
			{
			}
		}
	}
}
