package de.l3s.interwebj.core;


import java.security.*;
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
		ServiceConnector storedServiceConnector = connectors.get(connectorName);
		return (storedServiceConnector == null)
		    ? null : storedServiceConnector.clone();
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
	                                                    IWPrincipal principal,
	                                                    QueryResultMerger merger)
	    throws InterWebException
	{
		query.addParam("user", principal.getName());
		QueryResultCollector collector = new QueryResultCollector(query, merger);
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
	                                              Principal principal)
	{
		return (principal == null)
		    ? null : database.readUserAuthCredentials(connector.getName(),
		                                              principal.getName());
	}
	

	private void init()
	{
		connectors = new TreeMap<String, ServiceConnector>();
		contentTypes = new TreeSet<String>();
		standingQueryResultPool = new StandingQueryResultPool();
	}
	

	public boolean isUserAuthenticated(ServiceConnector connector,
	                                   Principal principal)
	{
		return getUserAuthCredentials(connector, principal) != null;
	}
	

	public void loadConnectors(String pluginDirPath)
	{
		init();
		ConnectorLoader connectorLoader = new ConnectorLoader();
		List<ServiceConnector> connectors = connectorLoader.load(pluginDirPath);
		for (ServiceConnector connector : connectors)
		{
			addConnector(connector);
		}
		//		addConnector(new FlickrConnector(null));
		//		addConnector(new YouTubeConnector(null));
		//		addConnector(new InterWebConnector(null));
	}
	

	public void setConsumerAuthCredentials(String connectorName,
	                                       AuthCredentials consumerAuthCredentials)
	{
		database.saveConsumer(connectorName,
		                      Environment.INTERWEBJ_SERVICE_NAME,
		                      consumerAuthCredentials);
		ServiceConnector connector = connectors.get(connectorName);
		connector.setConsumerAuthCredentials(consumerAuthCredentials);
	}
	

	public void setUserAuthCredentials(String connectorName,
	                                   IWPrincipal principal,
	                                   AuthCredentials consumerAuthCredentials)
	{
		database.saveUserAuthCredentials(connectorName,
		                                 principal.getName(),
		                                 consumerAuthCredentials);
	}
	

	public void upload(byte[] data,
	                   Principal principal,
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
	
	//	@SuppressWarnings("unused")
	//	public static void main(String[] args)
	//	    throws InterWebException
	//	{
	//		Database database = Environment.getInstance().getDatabase();
	//		Engine engine = new Engine(database);
	//		engine.loadConnectors("./connectors");
	//		IWPrincipal principal = database.authenticate("olex", "123456");
	//		String[] words = "sound water people live set air follow house mother earth grow cover door tree hard start draw left night real children mark car feet carry idea fish mountain color girl list talk family direct class ship told farm top heard hold reach table ten simple war lay pattern science cold fall fine fly lead dark machine wait star box rest correct pound stood sleep free strong produce inch blue object game heat sit weight".split(" ");
	//		List<String> connectorNames = engine.getConnectorNames();
	//		//		List<String> connectorNames = new ArrayList<String>();
	//		//		connectorNames.add("interweb");
	//		//		connectorNames.add("youtube");
	//		//		connectorNames.add("youtube2");
	//		//		connectorNames.add("flickr");
	//		
	//		System.out.println("Searching in connectors: " + connectorNames);
	//		int retryCount = 50;
	//		for (int i = 0; i < retryCount; i++)
	//		{
	//			testSearch("people", connectorNames, engine, principal);
	//		}
	//		//		for (String word : words)
	//		//		{
	//		//			testSearch(word, connectorNames, engine, principal);
	//		//		}
	//		System.out.println("finished");
	//	}
	//	
	//
	//	private static void testSearch(String word,
	//	                               List<String> connectorNames,
	//	                               Engine engine,
	//	                               IWPrincipal principal)
	//	    throws InterWebException
	//	{
	//		QueryFactory queryFactory = new QueryFactory();
	//		Query query = queryFactory.createQuery(word);
	//		query.addContentType(Query.CT_VIDEO);
	//		query.addContentType(Query.CT_IMAGE);
	//		query.addSearchScope(SearchScope.TEXT);
	//		query.addSearchScope(SearchScope.TAGS);
	//		query.setResultCount(50);
	//		query.setSortOrder(SortOrder.RELEVANCE);
	//		for (String connectorName : connectorNames)
	//		{
	//			query.addConnectorName(connectorName);
	//		}
	//		QueryResultMerger merger = new DumbQueryResultMerger();
	//		QueryResultCollector collector = engine.getQueryResultCollector(query,
	//		                                                                principal,
	//		                                                                merger);
	//		QueryResult queryResult = collector.retrieve();
	//		System.out.println("query: [" + query + "]");
	//		System.out.println("elapsed time : [" + queryResult.getElapsedTime()
	//		                   + "]");
	//	}
}
