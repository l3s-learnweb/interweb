package de.l3s.interweb.core.chat;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import de.l3s.interweb.core.Connector;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.models.ModelsConnector;

public interface ChatConnector extends ModelsConnector, Connector {

    Uni<CompletionsResults> completions(CompletionsQuery query) throws ConnectorException;

    default Multi<CompletionsResults> completionsStream(CompletionsQuery query) throws ConnectorException {
        throw new UnsupportedOperationException("Streaming completions are not implemented!");
    }
}
