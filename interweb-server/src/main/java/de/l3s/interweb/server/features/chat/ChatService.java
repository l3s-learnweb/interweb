package de.l3s.interweb.server.features.chat;

import java.util.ArrayList;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

import io.smallrye.mutiny.Uni;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.completion.CompletionConnector;
import de.l3s.interweb.core.completion.CompletionQuery;
import de.l3s.interweb.core.completion.CompletionResults;
import de.l3s.interweb.core.completion.Message;
import de.l3s.interweb.core.models.Model;
import de.l3s.interweb.core.models.ModelsConnector;
import de.l3s.interweb.server.features.models.ModelsService;

@ApplicationScoped
public class ChatService {

    @Inject
    ModelsService modelsService;

    public Uni<CompletionResults> completions(CompletionQuery query) {
        return modelsService.getModel(query.getModel()).onItem()
            .ifNull().failWith(new NotFoundException("Model not found: " + query.getModel()))
            .flatMap(model -> {
                ModelsConnector connector = modelsService.getConnector(model.getProvider());
                if (connector instanceof CompletionConnector completionConnector) {
                    return completions(query, model, completionConnector);
                }

                return Uni.createFrom().failure(new ConnectorException("Model doesn't support chat: " + query.getModel()));
            });
    }

    private Uni<CompletionResults> completions(CompletionQuery query, Model model, CompletionConnector connector) {
        long start = System.currentTimeMillis();
        return connector.complete(query).map(results -> {
            results.updateCosts(model.getPrice());
            results.setElapsedTime(System.currentTimeMillis() - start);
            return results;
        });
    }

    public Uni<CompletionResults> generateTitle(final Chat chat) {
        CompletionQuery query = new CompletionQuery();
        query.setMessages(new ArrayList<>(chat.getMessages().stream().map(ChatMessage::toMessage).toList()));
        query.addMessage("don't use any formatting; length between 80 and 120 characters;", Message.Role.system);
        query.addMessage("Give a short name for this conversation", Message.Role.user);
        return completions(query);
    }
}
