package de.l3s.interweb.core.suggest;

import de.l3s.interweb.core.Connector;
import de.l3s.interweb.core.ConnectorException;

public interface SuggestConnector extends Connector {
    SuggestConnectorResults suggest(SuggestQuery query) throws ConnectorException;
}
