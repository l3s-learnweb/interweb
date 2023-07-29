package de.l3s.interweb.server.search;

import java.util.*;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkus.arc.All;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import org.jboss.logging.Logger;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.search.SearchConnector;
import de.l3s.interweb.core.search.SearchConnectorResults;
import de.l3s.interweb.core.search.SearchQuery;
import de.l3s.interweb.core.search.SearchResults;
import de.l3s.interweb.server.principal.Principal;
import de.l3s.interweb.server.principal.SecretsService;

@ApplicationScoped
public class SearchService {
    private static final Logger log = Logger.getLogger(SearchService.class);

    private final Map<String, SearchConnector> services;
    private final SecretsService secretsService;

    @Inject
    public SearchService(@All List<SearchConnector> connectors, SecretsService secretsService) {
        services = new HashMap<>();
        connectors.forEach(connector -> services.put(connector.getId(), connector));
        log.info("Loaded " + services.size() + " search connectors");
        this.secretsService = secretsService;
    }

    public Collection<SearchConnector> getConnectors() {
        return this.services.values();
    }

    private Collection<SearchConnector> getConnectors(Set<String> services) {
        if (services != null && !services.isEmpty()) {
            return services.stream().map(this.services::get).toList();
        }

        return this.services.values();
    }

    public Uni<SearchResults> search(SearchQuery query, Principal principal) {
        return Multi.createFrom()
                .iterable(getConnectors(query.getServices()))
                .emitOn(Infrastructure.getDefaultWorkerPool())
                .onItem().transformToUniAndMerge(connector -> search(query, principal, connector))
                .collect().asList().map(SearchResults::new);
    }

    private Uni<SearchConnectorResults> search(SearchQuery query, Principal principal, SearchConnector connector) {
        long start = System.currentTimeMillis();
        return secretsService.getAuthCredentials(connector.getId(), principal)
                .onItem().ifNull().failWith(new ConnectorException("No credentials found"))
                .map(credentials -> connector.search(query, credentials))
                .onFailure(ConnectorException.class).recoverWithItem(failure -> {
                    SearchConnectorResults results = new SearchConnectorResults();
                    results.setError((ConnectorException) failure);
                    return results;
                })
                .onItem().invoke(conRes -> connector.fillResult(conRes, System.currentTimeMillis() - start));
    }
}
