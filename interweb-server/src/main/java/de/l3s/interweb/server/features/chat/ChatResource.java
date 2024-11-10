package de.l3s.interweb.server.features.chat;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import io.vertx.core.eventbus.EventBus;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.hibernate.reactive.mutiny.Mutiny;

import de.l3s.interweb.core.chat.CompletionsQuery;
import de.l3s.interweb.core.chat.CompletionsResults;
import de.l3s.interweb.core.chat.Message;
import de.l3s.interweb.core.util.StringUtils;
import de.l3s.interweb.server.Roles;
import de.l3s.interweb.server.features.api.ApiChatRequest;
import de.l3s.interweb.server.features.api.ApiKey;

@Tag(name = "Chat", description = "OpenAI compatible chat completions")
@Path("/chat")
@RolesAllowed({Roles.APPLICATION})
public class ChatResource {

    @Inject
    EventBus bus;

    @Inject
    ChatService chatService;

    @Context
    SecurityIdentity securityIdentity;

    @POST
    @Path("/completions")
    public Uni<CompletionsResults> completions(@Valid CompletionsQuery query) {
        ApiKey apikey = securityIdentity.getCredential(ApiKey.class);
        return chatService.completions(query).chain(results -> {
            results.setChatId(null); // reset chatId if it was set
            bus.send("api-request-chat", ApiChatRequest.of(results, apikey));

            if (!query.isSave()) {
                return Uni.createFrom().item(results);
            }

            return getOrCreateChat(query, apikey).call(chat -> {
                results.setChatId(chat.id);

                chat.usedTokens += results.getUsage().getTotalTokens();
                if (results.getCost() != null) {
                    chat.estimatedCost += results.getCost().getTotal();
                    results.getCost().setChatTotal(chat.estimatedCost);
                }

                Uni<Void> messagesUni = persistMessages(chat, query.getMessages(), results);

                Uni<Void> chatUni = Uni.createFrom().voidItem();
                if (chat.title == null) {
                    chatUni = chatUni.call(() -> chatService.generateTitle(chat)
                        .onFailure().recoverWithItem(() -> StringUtils.shorten(query.getMessages().getLast().getContent(), 120))
                        .invoke(title -> {
                            chat.title = title;
                            results.setChatTitle(title);
                        }));
                }
                chatUni = chatUni.call(chat::updateTitleAndUsage);

                return Uni.combine().all().unis(messagesUni, chatUni).discardItems();
            }).map(ignore -> results);
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
