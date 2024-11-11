package de.l3s.interweb.server.features.search;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkus.arc.All;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.cache.CaffeineCache;
import io.quarkus.cache.CompositeCacheKey;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.eventbus.EventBus;
import org.jboss.logging.Logger;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.search.SearchConnector;
import de.l3s.interweb.core.search.SearchConnectorResults;
import de.l3s.interweb.core.search.SearchQuery;
import de.l3s.interweb.core.search.SearchResults;
import de.l3s.interweb.server.features.api.ApiKey;
import de.l3s.interweb.server.features.api.ApiRequestSearch;
import de.l3s.interweb.server.features.api.UsageService;

@ApplicationScoped
public class SearchService {
    private static final Logger log = Logger.getLogger(SearchService.class);
    private static final int defaultTimeout = 10_000;

    @Inject
    EventBus bus;

    @Inject
    UsageService usageService;

    @Inject
    @CacheName("search")
    Cache cache;

    private final Map<String, SearchConnector> providers;

    @Inject
    public SearchService(@All List<SearchConnector> connectors) {
        providers = new HashMap<>();
        connectors.forEach(connector -> providers.put(connector.getId(), connector));
        log.info("Loaded " + providers.size() + " search connectors");
    }

    public Collection<SearchConnector> getConnectors() {
        return this.providers.values();
    }

    private Collection<SearchConnector> getConnectors(Set<String> services) {
        if (services != null && !services.isEmpty()) {
            return services.stream().map(val -> {
                SearchConnector connector = this.providers.get(val.toLowerCase(Locale.ROOT));
                if (connector == null) {
                    throw new ConnectorException("Service `" + val + "` is unknown");
                }
                return connector;
            }).toList();
        }

        return this.providers.values();
    }

    public Uni<SearchResults> search(SearchQuery query, ApiKey apikey) {
        Duration timeout = Duration.ofMillis(Objects.requireNonNullElse(query.getTimeout(), defaultTimeout));
        return Multi.createFrom()
            .iterable(getConnectors(query.getServices()))
            .onItem().transformToUniAndMerge(connector -> searchIn(query, connector, timeout, apikey))
            .collect().asList().map(SearchResults::new);
    }

    private Uni<SearchConnectorResults> searchIn(SearchQuery query, SearchConnector connector, Duration timeout, ApiKey apikey) {
        long start = System.currentTimeMillis();
        return usageService.allocate(apikey.user)
            .chain(() -> searchWithCache(query, connector))
            .ifNoItem().after(timeout).failWith(new ConnectorException(connector.getName() + " reached timeout after " + timeout.toMillis() + "ms"))
            .onFailure(ConnectorException.class).recoverWithItem(failure -> {
                log.error("Error in search connector " + connector.getId(), failure);
                SearchConnectorResults results = new SearchConnectorResults();
                results.setError((ConnectorException) failure);
                return results;
            }).onItem().invoke(conRes -> {
                connector.fillResult(conRes, System.currentTimeMillis() - start);
                bus.send("api-request-search", ApiRequestSearch.of(connector.getName(), query.getContentTypes().toString(), query.getQuery(), conRes.getEstimatedCost(), apikey));
            });
    }

    private Uni<SearchConnectorResults> searchWithCache(SearchQuery query, SearchConnector connector) {
        CompositeCacheKey key = generateKey(query, connector);
        if (query.getIgnoreCache()) {
            return connector.search(query).invoke(results -> cache.as(CaffeineCache.class).put(key, CompletableFuture.completedFuture(results)));
        }
        return cache.getAsync(key, k -> connector.search(query));
    }

    private CompositeCacheKey generateKey(SearchQuery query, SearchConnector connector) {
        return new CompositeCacheKey(
            query.getQuery(),
            query.getContentTypes(),
            query.getLanguage(),
            query.getPage(),
            query.getPerPage(),
            query.getExtras(),
            query.getSort(),
            query.getDateTo(),
            query.getDateFrom(),
            connector.getId()
        );
    }
}
