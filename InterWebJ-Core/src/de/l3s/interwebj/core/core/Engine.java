package de.l3s.interwebj.core.core;

import static de.l3s.interwebj.core.util.Assertions.notNull;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.Parameters;
import de.l3s.interwebj.core.connector.QueryResultCollector;
import de.l3s.interwebj.core.connector.ServiceConnector;
import de.l3s.interwebj.core.db.Database;
import de.l3s.interwebj.core.query.ContentType;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.ResultItem;
import de.l3s.interwebj.core.query.SearchResults;

public class Engine {
    private static final Logger log = LogManager.getLogger(Engine.class);

    private final Database database;

    private Set<ContentType> contentTypes;
    private Map<String, ServiceConnector> connectors;
    private Map<String, Cache<ServiceConnector, Parameters>> pendingAuthorizationConnectors;

    private Cache<String, Object> generalCache;
    private Cache<Query, SearchResults> searchCache;

    public Engine(Database database) {
        this.database = database;
        init();
    }

    private void init() {
        connectors = new TreeMap<>();
        contentTypes = new TreeSet<>();
        pendingAuthorizationConnectors = new HashMap<>();

        generalCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

        searchCache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .build();
    }

    public List<ContentType> getContentTypes() {
        return new ArrayList<>(contentTypes);
    }

    public Cache<String, Object> getGeneralCache() {
        return generalCache;
    }

    public Cache<Query, SearchResults> getSearchCache() {
        return searchCache;
    }

    public void addPendingAuthorizationConnector(InterWebPrincipal principal, ServiceConnector connector, Parameters params) {
        notNull(principal, "principal");
        notNull(connector, "connector");
        notNull(params, "params");
        log.info("Adding pending authorization connector [" + connector.getName() + "] for user [" + principal.getName() + "]");
        log.info("params: [" + params + "]");
        Cache<ServiceConnector, Parameters> expirableCache = createExpirableCache(60);

        if (pendingAuthorizationConnectors.containsKey(principal.getName())) {
            expirableCache = pendingAuthorizationConnectors.get(principal.getName());
        } else {
            pendingAuthorizationConnectors.put(principal.getName(), expirableCache);
        }
        expirableCache.put(connector, params);
    }

    public ServiceConnector getConnector(String connectorName) {
        ServiceConnector storedServiceConnector = connectors.get(connectorName.toLowerCase());
        return (storedServiceConnector == null) ? null : storedServiceConnector.clone();
    }

    public AuthCredentials getConnectorAuthCredentials(ServiceConnector connector) {
        return database.readConnectorAuthCredentials(connector.getName());
    }

    public List<String> getConnectorNames() {
        List<String> connectorList = new ArrayList<>();
        for (ServiceConnector connector : connectors.values()) {
            connectorList.add(connector.getName().toLowerCase());
        }
        return connectorList;
    }

    public List<ServiceConnector> getConnectors() {
        List<ServiceConnector> connectorList = new ArrayList<>();

        Set<String> connectorNames = connectors.keySet();
        for (String connectorName : connectorNames) {
            ServiceConnector connector = getConnector(connectorName);
            connectorList.add(connector);
        }
        return connectorList;
    }

    public QueryResultCollector getQueryResultCollector(Query query, InterWebPrincipal principal) throws InterWebException {
        log.info(query);

        QueryResultCollector collector = new QueryResultCollector(query);
        for (String connectorName : query.getConnectorNames()) {
            ServiceConnector connector = getConnector(connectorName);
            if (connector.isRegistered()) {
                AuthCredentials authCredentials = getUserAuthCredentials(connector, principal);
                collector.addQueryResultRetriever(connector, authCredentials);
            }
        }
        return collector;
    }

    public AuthCredentials getUserAuthCredentials(ServiceConnector connector, Principal principal) {
        notNull(connector, "connector");
        return (principal == null) ? null : database.readUserAuthCredentials(connector.getName(), principal.getName());
    }

    public boolean isUserAuthenticated(ServiceConnector connector, Principal principal) {
        return getUserAuthCredentials(connector, principal) != null;
    }

    public void loadConnectors() {
        init();
        ConnectorLoader connectorLoader = new ConnectorLoader();
        List<ServiceConnector> connectors = connectorLoader.load();

        for (ServiceConnector connector : connectors) {
            addConnector(connector);
            if (!database.hasConnector(connector.getName())) {
                database.saveConnector(connector.getName(), null);
            }
        }

    }

    public void processAuthenticationCallback(InterWebPrincipal principal, ServiceConnector connector, Parameters params) throws InterWebException {
        notNull(principal, "principal");
        notNull(connector, "connector");
        log.info("Trying to find pending authorization connector [" + connector.getName() + "] for user [" + principal.getName() + "]");
        Parameters pendingParameters = getPendingAuthorizationParameters(principal, connector);
        params.add(pendingParameters, false);
        AuthCredentials authCredentials = connector.completeAuthentication(params);
        log.info(authCredentials);
        String userId = connector.getUserId(authCredentials);
        log.info("Connector [" + connector.getName() + "] for user [" + principal.getName() + "] authenticated");
        setUserAuthCredentials(connector.getName(), principal, userId, authCredentials);
        log.info("authentication data saved");
    }

    public void setConsumerAuthCredentials(String connectorName, AuthCredentials connectorAuthCredentials) {
        database.saveConnector(connectorName, connectorAuthCredentials);
        ServiceConnector connector = connectors.get(connectorName.toLowerCase());
        connector.setAuthCredentials(connectorAuthCredentials);
    }

    public void setUserAuthCredentials(String connectorName, InterWebPrincipal principal, String userId, AuthCredentials consumerAuthCredentials) {
        database.saveUserAuthCredentials(connectorName, principal.getName(), userId, consumerAuthCredentials);
    }

    public ResultItem upload(byte[] data, Principal principal, List<String> connectorNames, ContentType contentType, Parameters params) throws InterWebException {
        log.info("start uploading ...");
        for (String connectorName : connectorNames) {
            log.info("connectorName: [" + connectorName + "]");
            ServiceConnector connector = getConnector(connectorName);
            if (connector != null && connector.supportContentType(contentType) && connector.isRegistered() && isUserAuthenticated(connector, principal)) {
                log.info("uploading to connector: " + connectorName);
                AuthCredentials userAuthCredentials = getUserAuthCredentials(connector, principal);
                ResultItem result = connector.put(data, contentType, params, userAuthCredentials);
                log.info("done");
                if (null != result) {
                    return result;
                }
            }
        }
        log.info("... uploading done");
        return null;
    }

    private void addConnector(ServiceConnector connector) {
        AuthCredentials authCredentials = getConnectorAuthCredentials(connector);
        connector.setAuthCredentials(authCredentials);
        contentTypes.addAll(connector.getContentTypes());
        connectors.put(connector.getName().toLowerCase(), connector);
    }

    private Cache<ServiceConnector, Parameters> createExpirableCache(int minutes) {
        return CacheBuilder.newBuilder().expireAfterWrite(minutes, TimeUnit.MINUTES).build();
    }

    private Parameters getPendingAuthorizationParameters(InterWebPrincipal principal, ServiceConnector connector) throws InterWebException {
        notNull(principal, "principal");
        notNull(connector, "connector");
        if (!pendingAuthorizationConnectors.containsKey(principal.getName()) || pendingAuthorizationConnectors.get(principal.getName()) == null) {
            pendingAuthorizationConnectors.remove(principal.getName());
            throw new InterWebException("There are no connectors with pending authorization info for user [" + principal.getName() + "]");
        }
        Cache<ServiceConnector, Parameters> expirableCache = pendingAuthorizationConnectors.get(principal.getName());
        if (expirableCache.getIfPresent(connector) == null) {
            throw new InterWebException("There are no parameters with pending authorization info for user [" + principal.getName()
                + "] and connector [" + connector.getName() + "]");
        }
        Parameters parameters = expirableCache.getIfPresent(connector);
        expirableCache.invalidate(connector);
        if (expirableCache.size() == 0) {
            pendingAuthorizationConnectors.remove(principal.getName());
        }
        return parameters;
    }
}
