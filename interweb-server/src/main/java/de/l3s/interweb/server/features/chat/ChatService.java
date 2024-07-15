package de.l3s.interweb.server.features.chat;

import java.util.*;

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

    private final List<CompletionConnector> providers;
    private final Map<String, CompletionConnector> models;

    @Inject
    public ChatService(@All List<CompletionConnector> connectors) {
        providers = new ArrayList<>();
        models = new HashMap<>();

        connectors.forEach(connector -> {
            if (connector.validate()) {
                providers.add(connector);

                for (String model : connector.getModels()) {
                    models.put(model, connector);
                }
            } else {
                log.error("Connector skipped due to failed validation: " + connector.getClass().getName());
            }
        });

        log.info("Loaded " + providers.size() + " completion connectors");
    }

    public Collection<CompletionConnector> getConnectors() {
        return providers;
    }

    public Map<String, UsagePrice> getModels() {
        Map<String, UsagePrice> models = new HashMap<>();
        for (Map.Entry<String, CompletionConnector> modelEntry : this.models.entrySet()) {
            models.put(modelEntry.getKey(), modelEntry.getValue().getPrice(modelEntry.getKey()));
        }
        return models;
    }

    public Uni<CompletionResults> completions(CompletionQuery query) {
        return completions(query, models.get(query.getModel()));
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
