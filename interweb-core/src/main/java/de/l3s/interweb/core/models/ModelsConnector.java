package de.l3s.interweb.core.models;

import io.smallrye.mutiny.Uni;

import de.l3s.interweb.core.Connector;

public interface ModelsConnector extends Connector {

    Uni<ModelsResults> models();

}
