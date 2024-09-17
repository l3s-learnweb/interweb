package de.l3s.interweb.server.features.describe;

import java.util.*;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ValidationException;

import io.quarkus.arc.All;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.describe.DescribeConnector;
import de.l3s.interweb.core.describe.DescribeQuery;
import de.l3s.interweb.core.describe.DescribeResults;

@ApplicationScoped
public class DescribeService {
    private static final Logger log = Logger.getLogger(DescribeService.class);

    private Map<String, DescribeConnector> services;

    @Inject
    public DescribeService(@All List<DescribeConnector> connectors) {
        services = new HashMap<>();
        connectors.forEach(connector -> services.put(connector.getId(), connector));
        log.info("Loaded " + services.size() + " describe connectors");
    }

    public Collection<DescribeConnector> getConnectors() {
        return this.services.values();
    }

    private DescribeConnector getConnector(Set<String> services) {
        String val = services.iterator().next();
        DescribeConnector connector = this.services.get(val.toLowerCase(Locale.ROOT));
        if (connector == null) {
            throw new ConnectorException("Service `" + val + "` is unknown");
        }
        return connector;
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

    public Uni<DescribeResults> describe(DescribeQuery query) {
        if (!query.getServices().isEmpty()) {
            return describe(query, getConnector(query.getServices()));
        }

        for (DescribeConnector connector : services.values()) {
            final String foundId = connector.findId(query.getLink());
            if (foundId != null) {
                query.setId(foundId);
                return describe(query, connector);
            }
        }

        throw new ValidationException("No connector found matching the given link.");
    }

    private Uni<DescribeResults> describe(DescribeQuery query, DescribeConnector connector) {
        long start = System.currentTimeMillis();
        return connector.describe(query).onFailure(ConnectorException.class).recoverWithItem(failure -> {
            log.error("Error in describe connector " + connector.getId(), failure);
            DescribeResults results = new DescribeResults();
            results.setError((ConnectorException) failure);
            return results;
        }).onItem().invoke(conRes -> connector.fillResult(conRes, System.currentTimeMillis() - start));
    }
}
