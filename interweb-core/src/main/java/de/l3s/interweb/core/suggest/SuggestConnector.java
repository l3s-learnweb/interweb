package de.l3s.interweb.core.suggest;

import io.smallrye.mutiny.Uni;

import de.l3s.interweb.core.Connector;
import de.l3s.interweb.core.ConnectorException;

public interface SuggestConnector extends Connector {
    Uni<SuggestConnectorResults> suggest(SuggestQuery query) throws ConnectorException;
}
