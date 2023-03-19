package de.l3s.interweb.tomcat.app;

import java.security.Principal;
import java.util.List;

import com.google.common.cache.Cache;

import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.InterWebException;
import de.l3s.interweb.core.Parameters;
import de.l3s.interweb.core.query.ContentType;
import de.l3s.interweb.core.query.Query;
import de.l3s.interweb.core.search.SearchProvider;
import de.l3s.interweb.core.search.SearchResponse;

public interface Engine {

    public List<ContentType> getContentTypes();

    public Cache<String, Object> getGeneralCache();

    public Cache<Query, SearchResponse> getSearchCache();

    public void addPendingAuthorizationConnector(InterWebPrincipal principal, SearchProvider connector, Parameters params);

    public SearchProvider getConnector(String connectorName);

    public AuthCredentials getConnectorAuthCredentials(SearchProvider connector);

    public List<String> getSearchServiceNames();

    public List<String> getSuggestServiceNames();

    public List<SearchProvider> getSearchProviders();

    public QueryResultCollector getQueryResultCollector(Query query, InterWebPrincipal principal) throws InterWebException;

    public AuthCredentials getUserAuthCredentials(SearchProvider connector, Principal principal);

    public boolean isUserAuthenticated(SearchProvider connector, Principal principal);

    public void loadConnectors();

    public void processAuthenticationCallback(InterWebPrincipal principal, SearchProvider connector, Parameters params) throws InterWebException;

    public void setConsumerAuthCredentials(String connectorName, AuthCredentials connectorAuthCredentials);

    public void setUserAuthCredentials(String connectorName, InterWebPrincipal principal, String userId, AuthCredentials consumerAuthCredentials);
}
