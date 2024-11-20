package de.l3s.interweb.server.features.chat;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.smallrye.mutiny.Uni;
import io.vertx.core.eventbus.EventBus;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.chat.*;
import de.l3s.interweb.core.models.Model;
import de.l3s.interweb.core.models.ModelsConnector;
import de.l3s.interweb.server.features.api.ApiKey;
import de.l3s.interweb.server.features.api.ApiRequestChat;
import de.l3s.interweb.server.features.api.UsageService;
import de.l3s.interweb.server.features.models.ModelsService;

@ApplicationScoped
public class ChatService {

    @Inject
    EventBus bus;

    @Inject
    UsageService usageService;

    @Inject
    ModelsService modelsService;

    public Uni<CompletionsResults> completions(CompletionsQuery query, ApiKey apikey) {
        return modelsService.getModel(query.getModel()).chain(model -> {
            if (model.isFree()) {
                return completions(query, model);
            } else {
                return usageService.allocate(apikey.user).chain(exceeded -> completions(query, model));
            }
        }).invoke(results -> {
            bus.send("api-request-chat", ApiRequestChat.of(results, apikey));
        });
    }

    private Uni<CompletionsResults> completions(CompletionsQuery query) {
        return modelsService.getModel(query.getModel()).chain(model -> completions(query, model));
    }

    private Uni<CompletionsResults> completions(CompletionsQuery query, Model model) {
        ModelsConnector connector = modelsService.getConnector(model.getProvider());
        if (connector instanceof ChatConnector chatConnector) {
            return completions(query, model, chatConnector);
        }

        return Uni.createFrom().failure(new ConnectorException("Model `" + query.getModel() + "` is not a chat model"));
    }

    private Uni<CompletionsResults> completions(CompletionsQuery query, Model model, ChatConnector connector) {
        long start = System.nanoTime();
        return connector.completions(query).map(results -> {
            if (results.getUsage() != null && model.getPrice() != null) {
                results.setCost(model.getPrice().calc(results.getUsage()));
            }
            if (results.getDuration() == null) {
                results.setDuration(Duration.of(System.nanoTime() - start));
            }
            return results;
        });
    }

    public Uni<String> generateTitle(final Chat chat) {
        StringBuilder sb = new StringBuilder();
        for (ChatMessage message : chat.getMessages()) {
            sb.append(" - [").append(message.role).append("] ").append(message.content);
        }

        CompletionsQuery query = new CompletionsQuery();
        query.setModel("gemma2:9b");
        query.addMessage("""
            ---BEGIN Conversation---
            %s
            ---END Conversation---
            Summarize the conversation in 5 words or less, in a way that sounds like a book title.
            Don't use any formatting. You can use emojis. Only print the title, nothing else.
            """.formatted(sb), Role.user);
        return completions(query).map(results -> results.getLastMessage().getContent().trim());
    }
}
