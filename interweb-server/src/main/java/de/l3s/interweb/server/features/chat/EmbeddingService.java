package de.l3s.interweb.server.features.chat;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.smallrye.mutiny.Uni;
import io.vertx.core.eventbus.EventBus;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.chat.Duration;
import de.l3s.interweb.core.embeddings.EmbeddingConnector;
import de.l3s.interweb.core.embeddings.EmbeddingsQuery;
import de.l3s.interweb.core.embeddings.EmbeddingsResults;
import de.l3s.interweb.core.models.Model;
import de.l3s.interweb.core.models.ModelsConnector;
import de.l3s.interweb.server.features.api.ApiKey;
import de.l3s.interweb.server.features.api.ApiRequestChat;
import de.l3s.interweb.server.features.api.UsageService;
import de.l3s.interweb.server.features.models.ModelsService;

@ApplicationScoped
public class EmbeddingService {

    @Inject
    EventBus bus;

    @Inject
    UsageService usageService;

    @Inject
    ModelsService modelsService;

    public Uni<EmbeddingsResults> embeddings(EmbeddingsQuery query, ApiKey apikey) {
        return modelsService.getModel(query.getModel()).chain(model -> {
            if (model.isFree()) {
                return embeddings(query, model);
            } else {
                return usageService.allocate(apikey.user).chain(exceeded -> embeddings(query, model));
            }
        }).invoke(results -> {
            bus.send("api-request-chat", ApiRequestChat.of(results, apikey));
        });
    }

    private Uni<EmbeddingsResults> embeddings(EmbeddingsQuery query, Model model) {
        ModelsConnector connector = modelsService.getConnector(model.getProvider());
        if (connector instanceof EmbeddingConnector embeddingConnector) {
            return embeddings(query, model, embeddingConnector);
        }

        return Uni.createFrom().failure(new ConnectorException("Model `" + query.getModel() + "` is not an embedding model"));
    }

    private Uni<EmbeddingsResults> embeddings(EmbeddingsQuery query, Model model, EmbeddingConnector connector) {
        long start = System.nanoTime();
        return connector.embeddings(query).map(results -> {
            if (results.getUsage() != null && model.getPrice() != null) {
                results.setCost(model.getPrice().calc(results.getUsage()));
            }
            results.setElapsedTime(Duration.of(System.nanoTime() - start).getTotal());
            return results;
        });
    }
}
