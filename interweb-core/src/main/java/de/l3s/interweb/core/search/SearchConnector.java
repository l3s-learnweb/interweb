package de.l3s.interweb.core.search;

import io.smallrye.mutiny.Uni;

import de.l3s.interweb.core.Connector;
import de.l3s.interweb.core.ConnectorException;

public interface SearchConnector extends Connector {
    ContentType[] getSearchTypes();

    Uni<SearchConnectorResults> search(SearchQuery query) throws ConnectorException;
}
