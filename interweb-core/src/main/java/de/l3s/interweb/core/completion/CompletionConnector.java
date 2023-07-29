package de.l3s.interweb.core.completion;

import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.Connector;
import de.l3s.interweb.core.ConnectorException;

public interface CompletionConnector extends Connector {
    String getModel();

    CompletionResults complete(CompletionQuery query, AuthCredentials credentials) throws ConnectorException;
}
