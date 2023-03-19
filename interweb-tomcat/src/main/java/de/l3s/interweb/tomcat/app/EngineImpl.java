package de.l3s.interweb.tomcat.app;

import static de.l3s.interweb.core.util.Assertions.notNull;

import java.security.Principal;
import java.util.*;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.InterWebException;
import de.l3s.interweb.core.Parameters;
import de.l3s.interweb.core.query.ContentType;
import de.l3s.interweb.core.query.Query;
import de.l3s.interweb.core.search.SearchProvider;
import de.l3s.interweb.core.search.SearchResponse;
import de.l3s.interweb.core.suggest.SuggestionProvider;
import de.l3s.interweb.tomcat.db.Database;

@ApplicationScoped
public class EngineImpl implements Engine {
    private static final Logger log = LogManager.getLogger(EngineImpl.class);

    private final Database database;
    private Set<ContentType> contentTypes;
    private Map<String, SearchProvider> searchProviders;
    private Map<String, SuggestionProvider> suggestProviders;
    private Map<String, Cache<SearchProvider, Parameters>> pendingAuthorizationConnectors;

    private Cache<String, Object> generalCache;
    private Cache<Query, SearchResponse> searchCache;

    @Inject
    public EngineImpl(Database database) {
        this.database = database;

        searchProviders = new TreeMap<>();
        suggestProviders = new TreeMap<>();
        contentTypes = new TreeSet<>();
        pendingAuthorizationConnectors = new HashMap<>();

        generalCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

        searchCache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .build();

        log.info("Starting InterWeb up...");
        loadConnectors();
    }

    public List<ContentType> getContentTypes() {
        return new ArrayList<>(contentTypes);
    }

    public Cache<String, Object> getGeneralCache() {
        return generalCache;
    }

    public Cache<Query, SearchResponse> getSearchCache() {
        return searchCache;
    }

    public void addPendingAuthorizationConnector(InterWebPrincipal principal, SearchProvider connector, Parameters params) {
        notNull(principal, "principal");
        notNull(connector, "connector");
        notNull(params, "params");
        log.info("Adding pending authorization connector [{}] for user [{}]", connector.getName(), principal.getName());
        log.info("params: [{}]", params);
        Cache<SearchProvider, Parameters> expirableCache = createExpirableCache(60);

        if (pendingAuthorizationConnectors.containsKey(principal.getName())) {
            expirableCache = pendingAuthorizationConnectors.get(principal.getName());
        } else {
            pendingAuthorizationConnectors.put(principal.getName(), expirableCache);
        }
        expirableCache.put(connector, params);
    }

    public SearchProvider getConnector(String connectorName) {
        SearchProvider storedSearchProvider = searchProviders.get(connectorName.toLowerCase());
        return (storedSearchProvider == null) ? null : storedSearchProvider.clone();
    }

    public AuthCredentials getConnectorAuthCredentials(SearchProvider connector) {
        return database.readConnectorAuthCredentials(connector.getName());
    }

    public List<String> getSearchServiceNames() {
        List<String> serviceNames = new ArrayList<>();
        for (SearchProvider connector : searchProviders.values()) {
            serviceNames.add(connector.getName().toLowerCase());
        }
        return serviceNames;
    }

    public List<String> getSuggestServiceNames() {
        List<String> serviceNames = new ArrayList<>();
        for (SuggestionProvider provider : suggestProviders.values()) {
            serviceNames.add(provider.getService().name().toLowerCase());
        }
        return serviceNames;
    }

    public List<SearchProvider> getSearchProviders() {
        List<SearchProvider> connectorList = new ArrayList<>();

        Set<String> connectorNames = searchProviders.keySet();
        for (String connectorName : connectorNames) {
            SearchProvider connector = getConnector(connectorName);
            connectorList.add(connector);
        }
        return connectorList;
    }

    public QueryResultCollector getQueryResultCollector(Query query, InterWebPrincipal principal) throws InterWebException {
        log.info(query);

        QueryResultCollector collector = new QueryResultCollector(this, query);
        for (String connectorName : query.getServices()) {
            SearchProvider connector = getConnector(connectorName);
            if (connector.isRegistered()) {
                AuthCredentials authCredentials = getUserAuthCredentials(connector, principal);
                collector.addQueryResultRetriever(connector, authCredentials);
            }
        }
        return collector;
    }

    public AuthCredentials getUserAuthCredentials(SearchProvider connector, Principal principal) {
        notNull(connector, "connector");
        return (principal == null) ? null : database.readUserAuthCredentials(connector.getName(), principal.getName());
    }

    public boolean isUserAuthenticated(SearchProvider connector, Principal principal) {
        return getUserAuthCredentials(connector, principal) != null;
    }

    public void loadConnectors() {
        ConnectorLoader connectorLoader = new ConnectorLoader();
        List<SearchProvider> connectors = connectorLoader.loadSearchProviders();

        for (SearchProvider connector : connectors) {
            addConnector(connector);
            if (!database.hasConnector(connector.getName())) {
                database.saveConnector(connector.getName(), null);
            }
        }
    }

    public void processAuthenticationCallback(InterWebPrincipal principal, SearchProvider connector, Parameters params) throws InterWebException {
        notNull(principal, "principal");
        notNull(connector, "connector");
        log.info("Trying to find pending authorization connector [{}] for user [{}]", connector.getName(), principal.getName());
        Parameters pendingParameters = getPendingAuthorizationParameters(principal, connector);
        params.add(pendingParameters, false);
        AuthCredentials authCredentials = connector.completeAuthentication(params);
        log.info(authCredentials);
        String userId = connector.getUserId(authCredentials);
        log.info("Connector [{}] for user [{}] authenticated", connector.getName(), principal.getName());
        setUserAuthCredentials(connector.getName(), principal, userId, authCredentials);
        log.info("authentication data saved");
    }

    public void setConsumerAuthCredentials(String connectorName, AuthCredentials connectorAuthCredentials) {
        database.saveConnector(connectorName, connectorAuthCredentials);
        SearchProvider connector = searchProviders.get(connectorName.toLowerCase());
        connector.setAuthCredentials(connectorAuthCredentials);
    }

    public void setUserAuthCredentials(String connectorName, InterWebPrincipal principal, String userId, AuthCredentials consumerAuthCredentials) {
        database.saveUserAuthCredentials(connectorName, principal.getName(), userId, consumerAuthCredentials);
    }

    private void addConnector(SearchProvider connector) {
        AuthCredentials authCredentials = getConnectorAuthCredentials(connector);
        connector.setAuthCredentials(authCredentials);
        contentTypes.addAll(connector.getContentTypes());
        searchProviders.put(connector.getName().toLowerCase(), connector);
    }

    private Cache<SearchProvider, Parameters> createExpirableCache(int minutes) {
        return CacheBuilder.newBuilder().expireAfterWrite(minutes, TimeUnit.MINUTES).build();
    }

    private Parameters getPendingAuthorizationParameters(InterWebPrincipal principal, SearchProvider connector) throws InterWebException {
        notNull(principal, "principal");
        notNull(connector, "connector");
        if (!pendingAuthorizationConnectors.containsKey(principal.getName()) || pendingAuthorizationConnectors.get(principal.getName()) == null) {
            pendingAuthorizationConnectors.remove(principal.getName());
            throw new InterWebException("There are no connectors with pending authorization info for user [" + principal.getName() + "]");
        }
        Cache<SearchProvider, Parameters> expirableCache = pendingAuthorizationConnectors.get(principal.getName());
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
