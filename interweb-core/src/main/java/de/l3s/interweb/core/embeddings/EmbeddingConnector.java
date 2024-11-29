package de.l3s.interweb.core.embeddings;

import io.smallrye.mutiny.Uni;

import de.l3s.interweb.core.Connector;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.models.ModelsConnector;

public interface EmbeddingConnector extends ModelsConnector, Connector {

    Uni<EmbeddingsResults> embeddings(EmbeddingsQuery query) throws ConnectorException;

}
