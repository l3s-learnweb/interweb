package de.l3s.interweb.server.features.chat;

import java.util.ArrayList;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

import io.smallrye.mutiny.Uni;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.chat.ChatConnector;
import de.l3s.interweb.core.chat.CompletionsQuery;
import de.l3s.interweb.core.chat.CompletionsResults;
import de.l3s.interweb.core.chat.Role;
import de.l3s.interweb.core.models.Model;
import de.l3s.interweb.core.models.ModelsConnector;
import de.l3s.interweb.server.features.models.ModelsService;

@ApplicationScoped
public class ChatService {

    @Inject
    ModelsService modelsService;

    public Uni<CompletionsResults> completions(CompletionsQuery query) {
        return modelsService.getModel(query.getModel()).onItem()
            .ifNull().failWith(new NotFoundException("Model not found: " + query.getModel()))
            .flatMap(model -> {
                ModelsConnector connector = modelsService.getConnector(model.getProvider());
                if (connector instanceof ChatConnector chatConnector) {
                    return completions(query, model, chatConnector);
                }

                return Uni.createFrom().failure(new ConnectorException("Model doesn't support chat: " + query.getModel()));
            });
    }

    private Uni<CompletionsResults> completions(CompletionsQuery query, Model model, ChatConnector connector) {
        long start = System.currentTimeMillis();
        return connector.completions(query).map(results -> {
            results.updateCosts(model.getPrice());
            results.setElapsedTime(System.currentTimeMillis() - start);
            return results;
        });
    }

    public Uni<CompletionsResults> generateTitle(final Chat chat) {
        CompletionsQuery query = new CompletionsQuery();
        query.setMessages(new ArrayList<>(chat.getMessages().stream().map(ChatMessage::toMessage).toList()));
        query.addMessage("don't use any formatting; length between 80 and 120 characters;", Role.system);
        query.addMessage("Give a short name for this conversation", Role.user);
        return completions(query);
    }
}
