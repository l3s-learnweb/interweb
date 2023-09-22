package de.l3s.interweb.server.chat;

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

@ApplicationScoped
public class ChatService {
    private static final Logger log = Logger.getLogger(ChatService.class);

    private final Map<String, CompletionConnector> services;

    @Inject
    public ChatService(@All List<CompletionConnector> connectors) {
        services = new HashMap<>();
        connectors.forEach(connector -> {
            for (String model : connector.getModels()) {
                services.put(model, connector);
            }
        });

        log.info("Loaded " + services.size() + " completion connectors");
    }

    public Collection<CompletionConnector> getConnectors() {
        return this.services.values();
    }

    public Uni<CompletionResults> completions(CompletionQuery query) {
        return completions(query, services.get(query.getModel()));
    }

    private Uni<CompletionResults> completions(CompletionQuery query, CompletionConnector connector) {
        return connector.complete(query).map(results -> {
            results.setModel(query.getModel());
            results.updateCosts(connector.getPrice(query.getModel()));
            return results;
        });
    }
}