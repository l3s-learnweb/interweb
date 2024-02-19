package de.l3s.interweb.server.features.suggest;

import java.util.*;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ValidationException;

import io.quarkus.arc.All;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.suggest.SuggestConnector;
import de.l3s.interweb.core.suggest.SuggestConnectorResults;
import de.l3s.interweb.core.suggest.SuggestQuery;
import de.l3s.interweb.core.suggest.SuggestResults;

@ApplicationScoped
public class SuggestService {
    private static final Logger log = Logger.getLogger(SuggestService.class);

    private Map<String, SuggestConnector> services;

    @Inject
    public SuggestService(@All List<SuggestConnector> connectors) {
        services = new HashMap<>();
        connectors.forEach(connector -> services.put(connector.getId(), connector));
        log.info("Loaded " + services.size() + " suggest connectors");
    }

    public Collection<SuggestConnector> getConnectors() {
        return this.services.values();
    }

    private Collection<SuggestConnector> getConnectors(Set<String> services) {
        if (services != null && !services.isEmpty()) {
            return services.stream().map(val -> {
                SuggestConnector connector = this.services.get(val.toLowerCase(Locale.ROOT));
                if (connector == null) {
                    throw new ConnectorException("Unknown service: " + val);
                }
                return connector;
            }).toList();
        }

        return this.services.values();
    }

    public void validateServices(Set<String> services) {
        if (services != null && !services.isEmpty()) {
            for (String service : services) {
                if (!this.services.containsKey(service)) {
                    throw new ValidationException("Service unknown.");
                }
            }
        }
    }

    public Uni<SuggestResults> suggest(SuggestQuery query) {
        return Multi.createFrom().iterable(getConnectors(query.getServices()))
                .onItem().transformToUniAndMerge(connector -> suggest(query, connector))
                .collect().asList().map(SuggestResults::new);
    }

    private Uni<SuggestConnectorResults> suggest(SuggestQuery query, SuggestConnector connector) {
        long start = System.currentTimeMillis();
        return connector.suggest(query).onFailure(ConnectorException.class).recoverWithItem(failure -> {
            log.error("Error in suggest connector " + connector.getId(), failure);
            SuggestConnectorResults results = new SuggestConnectorResults();
            results.setError((ConnectorException) failure);
            return results;
        }).onItem().invoke(conRes -> connector.fillResult(conRes, System.currentTimeMillis() - start));
    }
}
