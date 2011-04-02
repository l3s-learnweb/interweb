package de.l3s.interwebj.core;


import java.util.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.connector.*;
import de.l3s.interwebj.db.*;
import de.l3s.interwebj.query.*;
import de.l3s.interwebj.query.Query.SearchScope;
import de.l3s.interwebj.query.Query.SortOrder;


public class Engine
{
	
	private Map<String, ServiceConnector> connectors;
	private Set<String> contentTypes;
	private Database database;
	private StandingQueryResultPool standingQueryResultPool;
	

	public Engine(Database database)
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
		return connectors.get(connectorName).clone();
	}
	

	public List<String> getConnectorNames()
	{
		List<String> connectorList = new ArrayList<String>();
		Set<String> keys = connectors.keySet();
		for (String key : keys)
		{
			connectorList.add(connectors.get(key).getName());
		}
		return connectorList;
	}
	

	public List<ServiceConnector> getConnectors()
	{
		List<ServiceConnector> connectorList = new ArrayList<ServiceConnector>();
		Set<String> connectorNames = connectors.keySet();
		for (String connectorName : connectorNames)
		{
			connectorList.add(getConnector(connectorName));
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
		return new ArrayList<String>(contentTypes);
	}
	

	public QueryResultCollector getQueryResultCollector(Query query,
	                                                    IWPrincipal principal)
	    throws InterWebException
	{
		query.addParam("user", principal.getName());
		QueryResultCollector collector = new QueryResultCollector(query);
		for (String connectorName : query.getConnectorNames())
		{
			ServiceConnector connector = getConnector(connectorName);
			if (connector.isRegistered()
			    && isUserAuthenticated(connector, principal))
			{
				AuthCredentials authCredentials = getUserAuthCredentials(connector,
				                                                         principal);
				collector.addQueryResultRetriever(connector, authCredentials);
			}
		}
		return collector;
	}
	

	public StandingQueryResultPool getStandingQueryResultPool()
	{
		return standingQueryResultPool;
	}
	

	public AuthCredentials getUserAuthCredentials(ServiceConnector connector,
	                                              IWPrincipal principal)
	{
		return (principal == null)
		    ? null : database.readUserAuthCredentials(connector.getName(),
		                                              principal.getName());
	}
	

	private void init()
	{
		connectors = new TreeMap<String, ServiceConnector>();
		contentTypes = new TreeSet<String>();
		loadConnectors();
		standingQueryResultPool = new StandingQueryResultPool();
	}
	

	public boolean isUserAuthenticated(ServiceConnector connector,
	                                   IWPrincipal principal)
	{
		return getUserAuthCredentials(connector, principal) != null;
	}
	

	private void loadConnectors()
	{
		// TODO: stub
		addConnector(new FlickrConnector(null));
		addConnector(new YouTubeConnector(null));
		addConnector(new InterWebConnector(null));
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
	

	public void upload(byte[] data,
	                   IWPrincipal principal,
	                   List<String> connectorNames,
	                   String contentType,
	                   Parameters params)
	    throws InterWebException
	{
		Environment.logger.debug("start uploading...");
		for (String connectorName : connectorNames)
		{
			
			Environment.logger.debug("connectorName: [" + connectorName + "]");
			ServiceConnector connector = getConnector(connectorName);
			if (connector != null && connector.supportContentType(contentType)
			    && connector.isRegistered()
			    && isUserAuthenticated(connector, principal))
			{
				Environment.logger.debug("uploading to connector: "
				                         + connectorName);
				AuthCredentials userAuthCredentials = getUserAuthCredentials(connector,
				                                                             principal);
				connector.put(data, contentType, params, userAuthCredentials);
				Environment.logger.debug("done");
			}
		}
		Environment.logger.debug("...uploading done");
	}
	

	public static void main(String[] args)
	    throws InterWebException
	{
		Database database = Environment.getInstance().getDatabase();
		Engine engine = new Engine(database);
		IWPrincipal principal = database.authenticate("olex", "123456");
		QueryFactory queryFactory = new QueryFactory();
		Query query = queryFactory.createQuery("auto");
		query.addContentType(Query.CT_VIDEO);
		query.addContentType(Query.CT_IMAGE);
		query.addContentType(Query.CT_TEXT);
		query.addContentType(Query.CT_AUDIO);
		query.addSearchScope(SearchScope.TEXT);
		query.addSearchScope(SearchScope.TAGS);
		query.setResultCount(50);
		query.setSortOrder(SortOrder.RELEVANCE);
		for (String connectorName : engine.getConnectorNames())
		{
			query.addConnectorName(connectorName);
		}
		QueryResultCollector collector = engine.getQueryResultCollector(query,
		                                                                principal);
		long[] times = run(20, collector);
		long sum = 0;
		for (long l : times)
		{
			sum += l;
		}
		System.out.println(sum / (times.length + 0d));
		//		QueryResult queryResult = collector.retrieve();
		//		System.out.println("queryResult.getResultItems().size(): ["
		//		                   + queryResult.getResultItems().size() + "]");
		//		System.out.println("queryResult.getElapsedTime(): ["
		//		                   + queryResult.getElapsedTime() + "]");
	}
	

	private static long[] run(int count, QueryResultCollector collector)
	    throws InterWebException
	{
		long[] times = new long[count];
		for (int i = 0; i < times.length; i++)
		{
			QueryResult queryResult = collector.retrieve();
			times[i] = queryResult.getElapsedTime();
		}
		return times;
	}
}
