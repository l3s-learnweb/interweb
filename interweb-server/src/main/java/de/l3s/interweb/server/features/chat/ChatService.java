package de.l3s.interweb.server.features.chat;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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
        return modelsService.getModel(query.getModel()).flatMap(model -> {
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
        StringBuilder sb = new StringBuilder();
        for (ChatMessage message : chat.getMessages()) {
            sb.append(" - [").append(message.role).append("] ").append(message.content);
        }

        CompletionsQuery query = new CompletionsQuery();
        query.addMessage("""
            ---BEGIN Conversation---
            %s
            ---END Conversation---
            Summarize the conversation in 5 words or fewer, such that it could be a title of a book.
            Don't use any formatting. You can use emojis. Only print the title, nothing else.
            """.formatted(sb), Role.user);
        return completions(query);
    }
}
