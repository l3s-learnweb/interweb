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
		Environment.logger.debug("start uploading ...");
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
		Environment.logger.debug("... uploading done");
	}
	

	public static void main(String[] args)
	    throws InterWebException
	{
		Database database = Environment.getInstance().getDatabase();
		Engine engine = new Engine(database);
		IWPrincipal principal = database.authenticate("olex", "123456");
		String[] words = "sound water people live set air follow house mother earth grow cover door tree hard start draw left night real children mark car feet carry idea fish mountain color girl list talk family direct class ship told farm top heard hold reach table ten simple war lay pattern science cold fall fine fly lead dark machine wait star box rest correct pound stood sleep free strong produce inch blue object game heat sit weight".split(" ");
		List<String> connectorNames = new ArrayList<String>();
		connectorNames.add("interweb");
		//		connectorNames.add("youtube");
		//		connectorNames.add("flickr");
		int retryCount = 50;
		for (int i = 0; i < retryCount; i++)
		{
			testSearch("people", connectorNames, engine, principal);
		}
		//		for (String word : words)
		//		{
		//			testSearch(word, connectorNames, engine, principal);
		//		}
		System.out.println("finished");
	}
	

	private static void testSearch(String word,
	                               List<String> connectorNames,
	                               Engine engine,
	                               IWPrincipal principal)
	    throws InterWebException
	{
		QueryFactory queryFactory = new QueryFactory();
		Query query = queryFactory.createQuery(word);
		query.addContentType(Query.CT_VIDEO);
		query.addContentType(Query.CT_IMAGE);
		query.addSearchScope(SearchScope.TEXT);
		query.addSearchScope(SearchScope.TAGS);
		query.setResultCount(50);
		query.setSortOrder(SortOrder.RELEVANCE);
		for (String connectorName : connectorNames)
		{
			query.addConnectorName(connectorName);
		}
		QueryResultCollector collector = engine.getQueryResultCollector(query,
		                                                                principal);
		QueryResult queryResult = collector.retrieve();
		System.out.println("query: [" + query + "]");
		System.out.println("elapsed time : [" + queryResult.getElapsedTime()
		                   + "]");
	}
}
