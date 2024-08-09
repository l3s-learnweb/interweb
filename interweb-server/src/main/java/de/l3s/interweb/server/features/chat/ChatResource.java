package de.l3s.interweb.server.features.chat;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.hibernate.reactive.mutiny.Mutiny;

import de.l3s.interweb.core.chat.CompletionsQuery;
import de.l3s.interweb.core.chat.CompletionsResults;
import de.l3s.interweb.core.chat.Message;
import de.l3s.interweb.server.features.user.Token;

@Tag(name = "Chat", description = "OpenAI compatible chat completions")
@Path("/chat")
@Authenticated
public class ChatResource {

    @Inject
    ChatService chatService;

    @Context
    SecurityIdentity securityIdentity;

    @POST
    @Path("/completions")
    public Uni<CompletionsResults> completions(@Valid CompletionsQuery query) {
        ApiKey apikey = securityIdentity.getCredential(ApiKey.class);

        return getOrCreateChat(query, apikey).flatMap(chat -> {
            // noinspection CodeBlock2Expr
            return chatService.completions(query).call(results -> {
                results.setChatId(chat.id);
                return persistMessages(chat, query.getMessages(), results);
            }).invoke(results -> {
                chat.addCosts(results.getUsage().getTotalTokens(), results.getCost().getResponse());
                results.getCost().setChat(chat.estimatedCost);
            }).call(results -> {
                if (chat.title == null && query.isGenerateTitle()) {
                    return chatService.generateTitle(chat).invoke(titleCompletion -> {
                        chat.title = titleCompletion.getLastMessage().getContent();
                        results.setChatTitle(chat.title);

                        chat.addCosts(titleCompletion.getUsage().getTotalTokens(), titleCompletion.getCost().getResponse());
                        results.getCost().setChat(chat.estimatedCost);
                    });
                } else {
                    return Uni.createFrom().voidItem();
                }
            }).call(chat::updateTitleAndUsage);
        });
    }

    private Uni<Chat> getOrCreateChat(CompletionsQuery query, ApiKey apikey) {
        if (query.getId() != null) {
            return Chat.findById(apikey, query.getId()).call(chat -> Mutiny.fetch(chat.getMessages()));
        }

        Chat chat = new Chat();
        chat.apikey = apikey;
        chat.model = query.getModel();
        chat.user = query.getUser();
        return Panache.withTransaction(chat::persist);
    }

    private Uni<Void> persistMessages(final Chat chat, final List<Message> queryMessages, final CompletionsResults results) {
        for (Message message : queryMessages) {
            if (message.getId() == null) {
                ChatMessage cm = new ChatMessage(message);
                if (!chat.getMessages().contains(cm)) {
                    chat.addMessage(cm);
                }
            }
        }

        chat.addMessage(new ChatMessage(results.getLastMessage()));
        return Panache.withTransaction(() -> ChatMessage.persist(chat.getMessages()));
    }
}
