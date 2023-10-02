package de.l3s.interweb.server.search;

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
import org.jboss.logging.Logger;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.search.SearchConnector;
import de.l3s.interweb.core.search.SearchConnectorResults;
import de.l3s.interweb.core.search.SearchQuery;
import de.l3s.interweb.core.search.SearchResults;

@ApplicationScoped
public class SearchService {
    private static final Logger log = Logger.getLogger(SearchService.class);
    private static final int defaultTimeout = 10_000;

    private final Map<String, SearchConnector> services;

    @Inject
    @CacheName("search")
    Cache cache;

    @Inject
    public SearchService(@All List<SearchConnector> connectors) {
        services = new HashMap<>();
        connectors.forEach(connector -> services.put(connector.getId(), connector));
        log.info("Loaded " + services.size() + " search connectors");
    }

    public Collection<SearchConnector> getConnectors() {
        return this.services.values();
    }

    private Collection<SearchConnector> getConnectors(Set<String> services) {
        if (services != null && !services.isEmpty()) {
            return services.stream().map(val -> {
                SearchConnector connector = this.services.get(val.toLowerCase(Locale.ROOT));
                if (connector == null) {
                    throw new ConnectorException("Unknown service: " + val);
                }
                return connector;
            }).toList();
        }

        return this.services.values();
    }

    public Uni<SearchResults> search(SearchQuery query) {
        Duration timeout = Duration.ofMillis(Objects.requireNonNullElse(query.getTimeout(), defaultTimeout));
        return Multi.createFrom()
                .iterable(getConnectors(query.getServices()))
                .onItem().transformToUniAndMerge(connector -> searchIn(query, connector, timeout))
                .collect().asList().map(SearchResults::new);
    }

    private Uni<SearchConnectorResults> searchIn(SearchQuery query, SearchConnector connector, Duration timeout) {
        long start = System.currentTimeMillis();
        return searchWithCache(query, connector).ifNoItem().after(timeout).failWith(new ConnectorException("Timeout"))
                .onFailure(ConnectorException.class).recoverWithItem(failure -> {
                    log.error("Error in search connector " + connector.getId(), failure);
                    SearchConnectorResults results = new SearchConnectorResults();
                    results.setError((ConnectorException) failure);
                    return results;
                }).onItem().invoke(conRes -> connector.fillResult(conRes, System.currentTimeMillis() - start));
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
