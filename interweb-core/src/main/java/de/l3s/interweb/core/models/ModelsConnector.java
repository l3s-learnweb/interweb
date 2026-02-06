package de.l3s.interweb.core.models;

import io.smallrye.mutiny.Uni;

import de.l3s.interweb.core.Connector;

public interface ModelsConnector extends Connector {

    Uni<ModelsResults> models();

    /**
     * Pull a model from the provider's catalog.
     * This is an optional operation - connectors that don't support pulling should use the default implementation.
     *
     * @param modelName the name of the model to pull
     * @return a Uni containing the pull status
     */
    default Uni<ModelPullStatus> pullModel(String modelName) {
        ModelPullStatus status = new ModelPullStatus("unsupported");
        return Uni.createFrom().item(status);
    }

}
