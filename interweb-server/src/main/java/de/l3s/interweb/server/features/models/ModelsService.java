package de.l3s.interweb.server.features.models;

import java.util.*;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ServiceUnavailableException;

import io.quarkus.arc.All;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;

import de.l3s.interweb.core.models.Model;
import de.l3s.interweb.core.models.ModelsConnector;
import de.l3s.interweb.core.models.ModelsResults;

@ApplicationScoped
public class ModelsService {
    private static final Logger log = Logger.getLogger(ModelsService.class);

    @Inject
    @CacheName("model")
    Cache modelCache;

    private final Map<String, ModelsConnector> providers;

    @Inject
    public ModelsService(@All List<ModelsConnector> connectors) {
        providers = new HashMap<>();

        connectors.forEach(connector -> {
            if (connector.validate()) {
                providers.put(connector.getId(), connector);
            } else {
                log.warn("Connector skipped due to failed validation: " + connector.getClass().getName());
            }
        });

        log.info("Loaded " + providers.size() + " completion connectors");
    }

    public ModelsConnector getConnector(String id) {
        return this.providers.get(id);
    }

    public Collection<ModelsConnector> getConnectors() {
        return this.providers.values();
    }

    /**
     * Get all models from all providers, could be time expensive as each connector calls to their own source of data and waits for the response.
     */
    @CacheResult(cacheName = "models") // this is a short-term cache, to avoid spamming the providers
    public Uni<ModelsResults> getModels() {
        return Multi.createFrom().iterable(providers.values())
            .onItem().transformToUniAndMerge(connector -> connector.models()
                .map(ModelsResults::getData)
                .invoke(models -> models.forEach(model -> {
                    model.setId(model.getId().toLowerCase(Locale.ROOT));
                    model.setProvider(connector.getId());
                })))
            .collect()
            .in(ModelsResults::new, ModelsResults::addModel);
    }

    /**
     * The method implements a cache for the models. If the model is not in the cache, it will be fetched from the providers.
     * For Ollama, if the model is not available locally, it will attempt to pull it from the Ollama catalog.
     */
    public Uni<Model> getModel(String modelId) {
        return modelCache.getAsync(modelId.toLowerCase(Locale.ROOT), key -> getModels().chain(models -> {
            // Try to find the model in the available models
            Optional<Model> foundModel = models.getData().stream()
                .filter(model -> model.getId().equalsIgnoreCase(modelId.toLowerCase(Locale.ROOT)))
                .findFirst();

            if (foundModel.isPresent()) {
                return Uni.createFrom().item(foundModel.get());
            }

            // Model not found - check if we should try to pull it from Ollama
            return tryPullModel(modelId);
        }));
    }

    /**
     * Attempts to pull a model from the provider if supported.
     */
    public Uni<Model> tryPullModel(String modelId) {
        ModelsConnector connector = providers.get("ollama");
        if (connector == null) {
            throw new NotFoundException("Model `%s` not found. Use `/models` to get a list of available models.".formatted(modelId));
        }

        // Try to pull the model using the connector's pullModel method
        log.info("Model `%s` not found locally. Checking if pull is supported...".formatted(modelId));
        return connector.pullModel(modelId)
            .onItem().<Model>transformToUni(status -> {
                String statusMsg = status.getStatus();

                // Check if pull is not supported
                if ("unsupported".equals(statusMsg)) {
                    throw new NotFoundException("Model `%s` not found. Use `/models` to get a list of available models.".formatted(modelId));
                }

                // Check if pull failed
                if (statusMsg != null && statusMsg.startsWith("failed")) {
                    throw new NotFoundException("Model `%s` not found locally or in catalog. Error: %s".formatted(modelId, statusMsg));
                }

                // Pull in progress or just started
                int progress = status.getProgressPercent();
                throw new ServiceUnavailableException(
                    "Model `%s` is being pulled. Progress: %d%% - Status: %s. Please try again in a moment.".formatted(
                        modelId, progress, statusMsg != null ? statusMsg : "initiating"
                    )
                );
            })
            .onFailure().recoverWithUni(failure -> {
                if (failure instanceof ServiceUnavailableException || failure instanceof NotFoundException) {
                    return Uni.createFrom().failure(failure);
                }
                log.error("Failed to pull model: " + modelId, failure);
                return Uni.createFrom().failure(new NotFoundException(
                    "Model `%s` not found.".formatted(modelId)
                ));
            });
    }
}
