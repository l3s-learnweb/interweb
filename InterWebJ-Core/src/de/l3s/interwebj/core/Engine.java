package de.l3s.interwebj.core;


import static de.l3s.interwebj.util.Assertions.notNull;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import de.l3s.interwebj.AuthCredentials;
import de.l3s.interwebj.InterWebException;
import de.l3s.interwebj.Parameters;
import de.l3s.interwebj.db.Database;
import de.l3s.interwebj.query.DumbQueryResultMerger;
import de.l3s.interwebj.query.PrivacyQueryResultMerger;
import de.l3s.interwebj.query.Query;
import de.l3s.interwebj.query.Query.SearchScope;
import de.l3s.interwebj.query.Query.SortOrder;
import de.l3s.interwebj.query.QueryFactory;
import de.l3s.interwebj.query.QueryResult;
import de.l3s.interwebj.query.QueryResultCollector;
import de.l3s.interwebj.query.QueryResultMerger;
import de.l3s.interwebj.util.ExpirableMap;
import de.l3s.interwebj.util.ExpirationPolicy;


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
		Environment.logger.info("Adding pending authorization connector ["
		                        + connector.getName() + "] for user ["
		                        + principal.getName() + "]");
		Environment.logger.info("params: [" + params + "]");
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
	

	public QueryResultCollector getQueryResultCollector(Query query, InterWebPrincipal principal)
	    throws InterWebException
	{
		Environment.logger.info(query.toString());
		String userName = (principal == null)? "anonymous" : principal.getName();
		query.addParam("user", userName);		
		
		QueryResultMerger merger = new DumbQueryResultMerger();
		
		if(query.getPrivacy() != -1) // increase the number of results, to fetch enough public and private results
		{
			if(query.getPrivacy() < 0f) query.setPrivacy(0f);
			if(query.getPrivacy() > 1f) query.setPrivacy(1f);
				
			int estimatedPrivateResults = Math.round(query.getResultCount() * query.getConnectorNames().size() * query.getPrivacy());
			int estimatedPublicResults = query.getResultCount() * query.getConnectorNames().size() - estimatedPrivateResults;
				
			merger = new PrivacyQueryResultMerger(query.getResultCount(), estimatedPrivateResults, estimatedPublicResults);
			
			int tmp_number_of_results =  estimatedPrivateResults*5 + (int)Math.ceil(estimatedPublicResults*1.25);		
			
			query.setResultCount(tmp_number_of_results);
			
		}
		
		QueryResultCollector collector = new QueryResultCollector(query, merger);
		for (String connectorName : query.getConnectorNames())
		{
			ServiceConnector connector = getConnector(connectorName);
			if (connector.isRegistered())
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
	

	public void loadConnectors(String realPath, String pluginDirPath)
	{
		init();
		ConnectorLoader connectorLoader = new ConnectorLoader();
		List<ServiceConnector> connectors = connectorLoader.load(pluginDirPath);
		
		//HashSet<String> linkedConnectors=new HashSet<String>();
		//linkedConnectors.add("bing");

		boolean isDebug = java.lang.management.ManagementFactory
				.getRuntimeMXBean().getInputArguments().toString()
				.indexOf("-agentlib:jdwp") > 0;
		System.out.println("In debug : " + isDebug);

		for (ServiceConnector connector : connectors)
		{
			if(isDebug) 
			{
				connector=connectorLoader.loadLinkedConnector(realPath,connector.getName());
			}
			
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
		Environment.logger.info("Trying to find pending authorization connector ["
		                        + connector.getName()
		                        + "] for user ["
		                        + principal.getName() + "]");
		Parameters pendingParameters = getPendingAuthorizationParameters(principal,
		                                                                 connector);
		params.add(pendingParameters, false);
		AuthCredentials authCredentials = connector.completeAuthentication(params);
		System.out.println(authCredentials);
		String userId = connector.getUserId(authCredentials);
		Environment.logger.info("Connector [" + connector.getName()
		                        + "] for user [" + principal.getName()
		                        + "] authenticated");
		setUserAuthCredentials(connector.getName(),
		                       principal,
		                       userId,
		                       authCredentials);
		Environment.logger.info("authentication data saved");
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
		Environment.logger.info("start uploading ...");
		for (String connectorName : connectorNames)
		{
			
			Environment.logger.info("connectorName: [" + connectorName + "]");
			ServiceConnector connector = getConnector(connectorName);
			if (connector != null && connector.supportContentType(contentType)
			    && connector.isRegistered()
			    && isUserAuthenticated(connector, principal))
			{
				Environment.logger.info("uploading to connector: "
				                        + connectorName);
				AuthCredentials userAuthCredentials = getUserAuthCredentials(connector,
				                                                             principal);
				connector.put(data, contentType, params, userAuthCredentials);
				Environment.logger.info("done");
			}
		}
		Environment.logger.info("... uploading done");
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
		engine.loadConnectors("","./connectors");
		//String[] words = "sound water people live set air follow house mother earth grow cover door tree hard start draw left night real children mark car feet carry idea fish mountain color girl list talk family direct class ship told farm top heard hold reach table ten simple war lay pattern science cold fall fine fly lead dark machine wait star box rest correct pound stood sleep free strong produce inch blue object game heat sit weight".split(" ");
		List<String> connectorNames = engine.getConnectorNames();
		Environment.logger.info("Searching in connectors: " + connectorNames);
		int retryCount = 5;
		for (int i = 0; i < retryCount; i++)
		{
			testSearch("london", connectorNames, engine, principal);
		}
		//		for (String word : words)
		//		{
		//			testSearch(word, connectorNames, engine, principal);
		//		}
		Environment.logger.info("finished");
		
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
		query.setPrivacy(0.5f);
		query.setSortOrder(SortOrder.RELEVANCE);
		for (String connectorName : connectorNames)
		{
			query.addConnectorName(connectorName);
		}
		QueryResultCollector collector = engine.getQueryResultCollector(query, principal);
		
		QueryResult queryResult = collector.retrieve();
		System.out.println("query: [" + query + "]");
		System.out.println("elapsed time : [" + queryResult.getElapsedTime()
		                   + "]");
	}
}
