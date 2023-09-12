package de.l3s.interweb.core.search;

import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.Connector;
import de.l3s.interweb.core.ConnectorException;

public interface SearchConnector extends Connector {
    ContentType[] getSearchTypes();

    /**
     * Whether the connector is requiring credentials. If set to false, means connector can run without any credentials.
     */
    default boolean isCredentialsRequired() {
        return false;
    }

    default SearchConnectorResults search(SearchQuery query) throws ConnectorException {
        return search(query, null);
    }

    default SearchConnectorResults search(SearchQuery query, AuthCredentials credentials) throws ConnectorException {
        return search(query);
    }
}
