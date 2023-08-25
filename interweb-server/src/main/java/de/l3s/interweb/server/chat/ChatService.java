package de.l3s.interweb.server.chat;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkus.arc.All;
import org.jboss.logging.Logger;

import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.completion.CompletionConnector;
import de.l3s.interweb.core.completion.CompletionQuery;
import de.l3s.interweb.core.completion.CompletionResults;
import de.l3s.interweb.server.principal.Principal;
import de.l3s.interweb.server.principal.SecretsService;

@ApplicationScoped
public class ChatService {
    private static final Logger log = Logger.getLogger(ChatService.class);

    private final Map<String, CompletionConnector> services;
    private final SecretsService secretsService;

    @Inject
    public ChatService(@All List<CompletionConnector> connectors, SecretsService secretsService) {
        services = new HashMap<>();
        connectors.forEach(connector -> {
            for (String model : connector.getModels()) {
                services.put(model, connector);
            }
        });
        log.info("Loaded " + services.size() + " completion connectors");
        this.secretsService = secretsService;
    }

    public Collection<CompletionConnector> getConnectors() {
        return this.services.values();
    }

    public CompletionResults completions(CompletionQuery query, Principal principal) {
        return completions(query, principal, services.get(query.getModel()));
    }

    private CompletionResults completions(CompletionQuery query, Principal principal, CompletionConnector connector) {
        long start = System.currentTimeMillis();
        AuthCredentials credentials = secretsService.getAuthCredentials(connector.getId(), principal)
                .onItem().ifNull().failWith(new ConnectorException("No credentials found")).await().indefinitely();
        CompletionResults conRes = connector.complete(query, credentials);
        conRes.setElapsedTime(System.currentTimeMillis() - start);
        return conRes;
    }
}
