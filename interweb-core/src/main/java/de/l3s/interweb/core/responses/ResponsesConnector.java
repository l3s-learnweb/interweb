package de.l3s.interweb.core.responses;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import de.l3s.interweb.core.Connector;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.models.ModelsConnector;

public interface ResponsesConnector extends ModelsConnector, Connector {

    Uni<ResponsesResults> responses(ResponsesQuery query) throws ConnectorException;

    default Multi<ResponsesResults> responsesStream(ResponsesQuery query) throws ConnectorException {
        throw new UnsupportedOperationException("Streaming responses are not implemented!");
    }

}
