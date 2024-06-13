package de.l3s.interweb.server.features.chat;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkus.arc.All;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;

import de.l3s.interweb.core.completion.CompletionConnector;
import de.l3s.interweb.core.completion.CompletionQuery;
import de.l3s.interweb.core.completion.CompletionResults;
import de.l3s.interweb.core.completion.UsagePrice;

@ApplicationScoped
public class ChatService {
    private static final Logger log = Logger.getLogger(ChatService.class);

    private final Map<String, CompletionConnector> services;

    @Inject
    public ChatService(@All List<CompletionConnector> connectors) {
        services = new HashMap<>();
        connectors.forEach(connector -> {
            if (connector.validate()) {
                for (String model : connector.getModels()) {
                    services.put(model, connector);
                }
            } else {
                log.error("Connector skipped due to failed validation: " + connector.getClass().getName());
            }
        });

        log.info("Loaded " + services.size() + " completion connectors");
    }

    public Collection<CompletionConnector> getConnectors() {
        return this.services.values();
    }

    public Map<String, UsagePrice> getModels() {
        Map<String, UsagePrice> models = new HashMap<>();
        for (CompletionConnector connector : this.services.values()) {
            for (String model : connector.getModels()) {
                models.put(model, connector.getPrice(model));
            }
        }
        return models;
    }

    public Uni<CompletionResults> completions(CompletionQuery query) {
        return completions(query, services.get(query.getModel()));
    }

    private Uni<CompletionResults> completions(CompletionQuery query, CompletionConnector connector) {
        long start = System.currentTimeMillis();
        return connector.complete(query).map(results -> {
            results.updateCosts(connector.getPrice(query.getModel()));
            results.setElapsedTime(System.currentTimeMillis() - start);
            return results;
        });
    }
}
