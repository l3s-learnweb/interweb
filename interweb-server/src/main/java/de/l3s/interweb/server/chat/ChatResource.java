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
import io.quarkus.vertx.VertxContextSupport;
import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;

import de.l3s.interweb.core.completion.Choice;
import de.l3s.interweb.core.completion.CompletionQuery;
import de.l3s.interweb.core.completion.CompletionResults;
import de.l3s.interweb.core.completion.Message;
import de.l3s.interweb.server.principal.Consumer;
import de.l3s.interweb.server.principal.Principal;

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
    public CompletionResults completions(@Valid CompletionQuery query) throws Throwable {
        Chat chat = VertxContextSupport.subscribeAndAwait(() -> getOrCreateChat(query));
        CompletionResults results = chatService.completions(query, (Principal) securityIdentity.getPrincipal());
        results.setChatId(chat.id);
        VertxContextSupport.subscribeAndAwait(() -> persistMessages(chat, query.getMessages(), results.getChoices()));
        return results;
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

    private Uni<Void> persistMessages(final Chat chat, final List<Message> history, final List<Choice> choices) {
        for (Message message : history) {
            if (message.getId() == null) {
                ChatMessage cm = new ChatMessage(message);
                if (!chat.getMessages().contains(cm)) {
                    chat.addMessage(cm);
                }
            }
        }

        chat.addMessage(new ChatMessage(choices.get(0).getMessage()));
        return Panache.withTransaction(() -> ChatMessage.persist(chat.getMessages()));
    }
}
