package de.l3s.interweb.core.completion;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import de.l3s.interweb.core.Connector;
import de.l3s.interweb.core.ConnectorException;

public interface CompletionConnector extends Connector {
    String[] getModels();

    UsagePrice getPrice(String model);

    Uni<CompletionResults> complete(CompletionQuery query) throws ConnectorException;

    default Multi<CompletionResults> completeStream(CompletionQuery query) throws ConnectorException {
        throw new UnsupportedOperationException("Streaming not implemented");
    }
}
