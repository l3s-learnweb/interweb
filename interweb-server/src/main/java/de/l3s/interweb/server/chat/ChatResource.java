package de.l3s.interweb.server.chat;

import java.util.List;
import java.util.UUID;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;

import de.l3s.interweb.core.completion.CompletionQuery;
import de.l3s.interweb.core.completion.CompletionResults;
import de.l3s.interweb.core.completion.Message;
import de.l3s.interweb.server.principal.Consumer;

@Path("/chat")
public class ChatResource {

    @Inject
    ChatService chatService;

    @Context
    SecurityIdentity securityIdentity;

    @GET
    @Authenticated
    public Uni<List<Chat>> chats() {
        return Chat.findByConsumer(securityIdentity.getCredential(Consumer.class));
    }

    @GET
    @Authenticated
    @Path("{uuid}")
    public Uni<List<ChatMessage>> chat(@PathParam("uuid") UUID id) {
        return ChatMessage.listByChat(id);
    }

    @POST
    @Authenticated
    @Path("/completions")
    public Uni<CompletionResults> completions(@Valid CompletionQuery query) {
        return getOrCreateChat(query).flatMap(chat -> {
            long start = System.currentTimeMillis();
            return chatService.completions(query).call(results -> {
                results.setElapsedTime(System.currentTimeMillis() - start);
                results.setChatId(chat.id);
                return persistMessages(chat, query.getMessages(), results);
            });
        });
    }

    private Uni<Chat> getOrCreateChat(CompletionQuery query) {
        if (query.getChatId() != null) {
            return Chat.findById(query.getChatId()).call(chat -> Mutiny.fetch(chat.getMessages()));
        }

        Chat chat = new Chat();
        chat.consumer = securityIdentity.getCredential(Consumer.class);
        chat.model = query.getModel();
        return Panache.withTransaction(chat::persist);
    }

    private Uni<Void> persistMessages(final Chat chat, final List<Message> history, final CompletionResults results) {
        for (Message message : history) {
            if (message.getId() == null) {
                ChatMessage cm = new ChatMessage(message);
                if (!chat.getMessages().contains(cm)) {
                    chat.addMessage(cm);
                }
            }
        }

        if (results.getCost() != null) {
            chat.addCosts(results.getUsage().getTotalTokens(), results.getCost().getResponse());
            results.getCost().setChat(chat.estimated_cost);
        }

        chat.addMessage(new ChatMessage(results.getChoices().get(0).getMessage()));
        return Panache.withTransaction(() -> ChatMessage.persist(chat.getMessages()));
    }
}
