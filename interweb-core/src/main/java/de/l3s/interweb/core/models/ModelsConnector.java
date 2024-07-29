package de.l3s.interweb.core.models;

import java.util.List;

import io.smallrye.mutiny.Uni;

import de.l3s.interweb.core.Connector;

public interface ModelsConnector extends Connector {

    Uni<List<Model>> getModels();

}
