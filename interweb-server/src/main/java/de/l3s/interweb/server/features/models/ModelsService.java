package de.l3s.interweb.server.features.models;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkus.arc.All;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.cache.CacheResult;
import io.quarkus.cache.CaffeineCache;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;

import de.l3s.interweb.core.models.Model;
import de.l3s.interweb.core.models.ModelsConnector;

@ApplicationScoped
public class ModelsService {
    private static final Logger log = Logger.getLogger(ModelsService.class);

    @Inject
    @CacheName("model")
    Cache cache;

    private final Map<String, ModelsConnector> providers;

    @Inject
    public ModelsService(@All List<ModelsConnector> connectors) {
        providers = new HashMap<>();

        connectors.forEach(connector -> {
            if (connector.validate()) {
                providers.put(connector.getId(), connector);
            } else {
                log.error("Connector skipped due to failed validation: " + connector.getClass().getName());
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
    @CacheResult(cacheName = "models") // this is short cache, to avoid spamming the providers
    public Uni<List<Model>> getModels() {
        CaffeineCache cache = this.cache.as(CaffeineCache.class);

        return Multi.createFrom().iterable(providers.values())
            // invalidate cache
            .call(cache::invalidateAll)
            .onItem().transformToUniAndMerge(connector -> {
                return connector.getModels()
                    // populate cache with models
                    .invoke(models -> models.forEach(model -> {
                        model.setId(model.getId().toLowerCase(Locale.ROOT));
                        model.setProvider(connector.getId());
                        cache.put(model.getId(), CompletableFuture.completedFuture(model));
                    }));
            })
            .collect().in(ArrayList::new, List::addAll);
    }

    /**
     * The method implements a cache for the models. If the model is not in the cache, it will be fetched from the providers.
     */
    public Uni<Model> getModel(String modelId) {
        CaffeineCache cache = this.cache.as(CaffeineCache.class);
        modelId = modelId.toLowerCase(Locale.ROOT);

        CompletableFuture<Model> value = cache.getIfPresent(modelId);
        if (value != null) {
            return Uni.createFrom().future(value);
        }

        return getModels().replaceWith(Uni.createFrom().future(cache.getIfPresent(modelId)));
    }
}
