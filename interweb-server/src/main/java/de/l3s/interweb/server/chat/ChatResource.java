package de.l3s.interweb.server.chat;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;

import de.l3s.interweb.core.completion.CompletionQuery;
import de.l3s.interweb.core.completion.CompletionResults;
import de.l3s.interweb.core.completion.Message;
import de.l3s.interweb.core.completion.UsagePrice;
import de.l3s.interweb.server.principal.Consumer;

@Path("/chat")
public class ChatResource {

    @Inject
    ChatService chatService;

    @Context
    SecurityIdentity securityIdentity;

    @GET
    @Authenticated
    public Uni<List<Chat>> chats(@QueryParam("user") String user) {
        Consumer consumer = securityIdentity.getCredential(Consumer.class);
        if (user == null) {
            return Chat.list("consumer.id = ?1 AND user = NULL ORDER BY created DESC LIMIT 20", consumer.id);
        } else {
            return Chat.list("consumer.id = ?1 AND user = ?2 ORDER BY created DESC LIMIT 20", consumer.id, user);
        }
    }

    @GET
    @Authenticated
    @Path("{uuid}")
    public Uni<List<ChatMessage>> chat(@PathParam("uuid") UUID id) {
        return ChatMessage.listByChat(id);
    }

    @GET
    @Authenticated
    @Path("/models")
    public Map<String, UsagePrice> models() {
        return chatService.getModels();
    }

    @POST
    @Authenticated
    @Path("/completions")
    public Uni<CompletionResults> completions(@Valid CompletionQuery query) {
        return getOrCreateChat(query).flatMap(chat -> {
            //noinspection CodeBlock2Expr
            return chatService.completions(query).call(results -> {
                results.setChatId(chat.id);
                return persistMessages(chat, query.getMessages(), results);
            }).invoke(results -> {
                chat.addCosts(results.getUsage().getTotalTokens(), results.getCost().getResponse());
                results.getCost().setChat(chat.estimatedCost);
            }).call(results -> {
                if (chat.title == null && query.isGenerateTitle()) {
                    return generateTitle(chat).invoke(titleCompletion -> {
                        chat.title = titleCompletion.getLastMessage().getContent();
                        results.setChatTitle(chat.title);

                        chat.addCosts(titleCompletion.getUsage().getTotalTokens(), titleCompletion.getCost().getResponse());
                        results.getCost().setChat(chat.estimatedCost);
                    });
                } else {
                    return Uni.createFrom().voidItem();
                }
            }).call(() -> Chat.update("title = ?1, usedTokens = ?2, estimatedCost = ?3 where id = ?4", chat.title, chat.usedTokens, chat.estimatedCost, chat.id));
        });
    }

    private Uni<Chat> getOrCreateChat(CompletionQuery query) {
        if (query.getId() != null) {
            return Chat.findById(query.getId()).call(chat -> Mutiny.fetch(chat.getMessages()));
        }

        Chat chat = new Chat();
        chat.consumer = securityIdentity.getCredential(Consumer.class);
        chat.model = query.getModel();
        chat.user = query.getUser();
        return Panache.withTransaction(chat::persist);
    }

    private Uni<Void> persistMessages(final Chat chat, final List<Message> queryMessages, final CompletionResults results) {
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

    private Uni<CompletionResults> generateTitle(final Chat chat) {
        CompletionQuery query = new CompletionQuery();
        query.setMessages(chat.getMessages().stream().map(ChatMessage::toMessage).collect(Collectors.toList()));
        query.addMessage("Give a short name for this conversation; don't use any formatting; length between 80 and 120 characters", Message.Role.user);
        return chatService.completions(query);
    }
}
