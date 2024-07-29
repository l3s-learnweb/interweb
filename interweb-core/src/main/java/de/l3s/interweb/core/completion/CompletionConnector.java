package de.l3s.interweb.core.completion;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import de.l3s.interweb.core.Connector;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.models.ModelsConnector;

public interface CompletionConnector extends ModelsConnector, Connector {

    Uni<CompletionResults> complete(CompletionQuery query) throws ConnectorException;

    default Multi<CompletionResults> completeStream(CompletionQuery query) throws ConnectorException {
        throw new UnsupportedOperationException("Streaming completions are not implemented!");
    }
}
