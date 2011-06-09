package de.l3s.interwebj.core;


import static de.l3s.interwebj.util.Assertions.*;

import java.security.*;
import java.util.*;
import java.util.concurrent.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.db.*;
import de.l3s.interwebj.query.*;
import de.l3s.interwebj.query.Query.SearchScope;
import de.l3s.interwebj.query.Query.SortOrder;
import de.l3s.interwebj.util.*;


public class Engine
{
	
	private Map<String, ServiceConnector> connectors;
	private Set<String> contentTypes;
	private Database database;
	private ExpirableMap<String, Object> expirableMap;
	private Map<String, Map<ServiceConnector, Parameters>> pendingAuthorizationConnectors;
	

	public Engine(Database database)
	{
		this.database = database;
		init();
	}
	

	public void addPendingAuthorizationConnector(InterWebPrincipal principal,
	                                             ServiceConnector connector,
	                                             Parameters params)
	{
		notNull(principal, "principal");
		notNull(connector, "connector");
		notNull(params, "params");
		Environment.logger.debug("Adding pending authorization connector ["
		                         + connector.getName() + "] for user ["
		                         + principal.getName() + "]");
		Environment.logger.debug("params: [" + params + "]");
		Map<ServiceConnector, Parameters> expirableMap = createExpirableMap(60);
		if (pendingAuthorizationConnectors.containsKey(principal.getName()))
		{
			expirableMap = pendingAuthorizationConnectors.get(principal.getName());
		}
		else
		{
			pendingAuthorizationConnectors.put(principal.getName(),
			                                   expirableMap);
		}
		expirableMap.put(connector, params);
	}
	

	public ServiceConnector getConnector(String connectorName)
	{
		ServiceConnector storedServiceConnector = connectors.get(connectorName);
		return (storedServiceConnector == null)
		    ? null : storedServiceConnector.clone();
	}
	

	public AuthCredentials getConnectorAuthCredentials(ServiceConnector connector)
	{
		return database.readConnectorAuthCredentials(connector.getName());
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
	

	public List<String> getContentTypes()
	{
		return new ArrayList<String>(contentTypes);
	}
	

	public ExpirableMap<String, Object> getExpirableMap()
	{
		return expirableMap;
	}
	

	public QueryResultCollector getQueryResultCollector(Query query,
	                                                    InterWebPrincipal principal,
	                                                    QueryResultMerger merger)
	    throws InterWebException
	{
		Environment.logger.debug(query);
		String userName = (principal == null)
		    ? "anonymous" : principal.getName();
		query.addParam("user", userName);
		QueryResultCollector collector = new QueryResultCollector(query, merger);
		for (String connectorName : query.getConnectorNames())
		{
			ServiceConnector connector = getConnector(connectorName);
			if (connector.isConnectorRegistered())
			{
				AuthCredentials authCredentials = getUserAuthCredentials(connector,
				                                                         principal);
				collector.addQueryResultRetriever(connector, authCredentials);
			}
		}
		return collector;
	}
	

	public AuthCredentials getUserAuthCredentials(ServiceConnector connector,
	                                              Principal principal)
	{
		notNull(connector, "connector");
		return (principal == null)
		    ? null : database.readUserAuthCredentials(connector.getName(),
		                                              principal.getName());
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
			if (!database.hasConnector(connector.getName()))
			{
				database.saveConnector(connector.getName(), null);
			}
		}
	}
	

	public void processAuthenticationCallback(InterWebPrincipal principal,
	                                          ServiceConnector connector,
	                                          Parameters params)
	    throws InterWebException
	{
		notNull(principal, "principal");
		notNull(connector, "connector");
		Environment.logger.debug("Trying to find pending authorization connector ["
		                         + connector.getName()
		                         + "] for user ["
		                         + principal.getName() + "]");
		Parameters pendingParameters = getPendingAuthorizationParameters(principal,
		                                                                 connector);
		params.add(pendingParameters, false);
		AuthCredentials authCredentials = connector.completeAuthentication(params);
		System.out.println(authCredentials);
		String userId = connector.getUserId(authCredentials);
		Environment.logger.debug("Connector [" + connector.getName()
		                         + "] for user [" + principal.getName()
		                         + "] authenticated");
		setUserAuthCredentials(connector.getName(),
		                       principal,
		                       userId,
		                       authCredentials);
		Environment.logger.debug("authentication data saved");
	}
	

	public void setConsumerAuthCredentials(String connectorName,
	                                       AuthCredentials connectorAuthCredentials)
	{
		database.saveConnector(connectorName, connectorAuthCredentials);
		ServiceConnector connector = connectors.get(connectorName);
		connector.setAuthCredentials(connectorAuthCredentials);
	}
	

	public void setUserAuthCredentials(String connectorName,
	                                   InterWebPrincipal principal,
	                                   String userId,
	                                   AuthCredentials consumerAuthCredentials)
	{
		database.saveUserAuthCredentials(connectorName,
		                                 principal.getName(),
		                                 userId,
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
			    && connector.isConnectorRegistered()
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
	

	private void addConnector(ServiceConnector connector)
	{
		AuthCredentials authCredentials = getConnectorAuthCredentials(connector);
		connector.setAuthCredentials(authCredentials);
		contentTypes.addAll(connector.getContentTypes());
		connectors.put(connector.getName(), connector);
	}
	

	private ExpirableMap<ServiceConnector, Parameters> createExpirableMap(int minutes)
	{
		ExpirationPolicy.Builder builder = new ExpirationPolicy.Builder();
		return new ExpirableMap<ServiceConnector, Parameters>(builder.timeToIdle(minutes,
		                                                                         TimeUnit.MINUTES).build());
	}
	

	private Parameters getPendingAuthorizationParameters(InterWebPrincipal principal,
	                                                     ServiceConnector connector)
	    throws InterWebException
	{
		notNull(principal, "principal");
		notNull(connector, "connector");
		if (!pendingAuthorizationConnectors.containsKey(principal.getName())
		    || pendingAuthorizationConnectors.get(principal.getName()) == null)
		{
			pendingAuthorizationConnectors.remove(principal.getName());
			throw new InterWebException("There are no connectors with pending authorization info for user ["
			                            + principal.getName() + "]");
		}
		Map<ServiceConnector, Parameters> expirableMap = pendingAuthorizationConnectors.get(principal.getName());
		if (!expirableMap.containsKey(connector)
		    || expirableMap.get(connector) == null)
		{
			throw new InterWebException("There are no parameters with pending authorization info for user ["
			                            + principal.getName()
			                            + "] and connector ["
			                            + connector.getName() + "]");
		}
		Parameters parameters = expirableMap.get(connector);
		expirableMap.remove(connector);
		if (expirableMap.isEmpty())
		{
			pendingAuthorizationConnectors.remove(principal.getName());
		}
		return parameters;
	}
	

	private void init()
	{
		connectors = new TreeMap<String, ServiceConnector>();
		contentTypes = new TreeSet<String>();
		ExpirationPolicy.Builder builder = new ExpirationPolicy.Builder();
		expirableMap = new ExpirableMap<String, Object>(builder.timeToIdle(60,
		                                                                   TimeUnit.MINUTES).build());
		pendingAuthorizationConnectors = new HashMap<String, Map<ServiceConnector, Parameters>>();
	}
	

	@SuppressWarnings("unused")
	public static void main(String[] args)
	    throws InterWebException
	{
		Database database = Environment.getInstance().getDatabase();
		InterWebPrincipal principal;
		principal = database.authenticate("olex", "123456");
		AuthCredentials authCredentials;
		authCredentials = database.readConnectorAuthCredentials("flickr");
		System.out.println(authCredentials);
		Engine engine = new Engine(database);
		engine.loadConnectors("./connectors");
		String[] words = "sound water people live set air follow house mother earth grow cover door tree hard start draw left night real children mark car feet carry idea fish mountain color girl list talk family direct class ship told farm top heard hold reach table ten simple war lay pattern science cold fall fine fly lead dark machine wait star box rest correct pound stood sleep free strong produce inch blue object game heat sit weight".split(" ");
		List<String> connectorNames = engine.getConnectorNames();
		Environment.logger.debug("Searching in connectors: " + connectorNames);
		int retryCount = 5;
		for (int i = 0; i < retryCount; i++)
		{
			testSearch("people", connectorNames, engine, principal);
		}
		//		for (String word : words)
		//		{
		//			testSearch(word, connectorNames, engine, principal);
		//		}
		Environment.logger.debug("finished");
		
	}
	

	private static void testSearch(String word,
	                               List<String> connectorNames,
	                               Engine engine,
	                               InterWebPrincipal principal)
	    throws InterWebException
	{
		QueryFactory queryFactory = new QueryFactory();
		Query query = queryFactory.createQuery(word);
		query.addContentType(Query.CT_VIDEO);
		query.addContentType(Query.CT_IMAGE);
		query.addSearchScope(SearchScope.TEXT);
		query.addSearchScope(SearchScope.TAGS);
		query.setResultCount(10);
		query.setSortOrder(SortOrder.RELEVANCE);
		for (String connectorName : connectorNames)
		{
			query.addConnectorName(connectorName);
		}
		QueryResultMerger merger = new DumbQueryResultMerger();
		QueryResultCollector collector = engine.getQueryResultCollector(query,
		                                                                principal,
		                                                                merger);
		QueryResult queryResult = collector.retrieve();
		System.out.println("query: [" + query + "]");
		System.out.println("elapsed time : [" + queryResult.getElapsedTime()
		                   + "]");
	}
}
