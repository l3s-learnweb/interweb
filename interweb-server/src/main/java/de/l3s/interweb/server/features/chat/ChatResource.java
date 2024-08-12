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
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.hibernate.reactive.mutiny.Mutiny;

import de.l3s.interweb.core.chat.CompletionsQuery;
import de.l3s.interweb.core.chat.CompletionsResults;
import de.l3s.interweb.core.chat.Message;
import de.l3s.interweb.server.Roles;
import de.l3s.interweb.server.features.user.ApiKey;

@Tag(name = "Chat", description = "OpenAI compatible chat completions")
@Path("/chat")
@RolesAllowed({Roles.APPLICATION})
public class ChatResource {

    @Inject
    ChatService chatService;

    @Context
    SecurityIdentity securityIdentity;

    @POST
    @Path("/completions")
    public Uni<CompletionsResults> completions(@Valid CompletionsQuery query) {
        return chatService.completions(query).chain(results -> {
            if (!query.isSave()) {
                return Uni.createFrom().item(results);
            }

            ApiKey apikey = securityIdentity.getCredential(ApiKey.class);
            return getOrCreateChat(query, apikey).call(chat -> {
                results.setChatId(chat.id);

                chat.addCosts(results.getUsage().getTotalTokens(), results.getCost().getResponse());
                results.getCost().setChat(chat.estimatedCost);

                Uni<Void> messagesUni = persistMessages(chat, query.getMessages(), results);

                Uni<Void> chatUni = Uni.createFrom().voidItem();
                if (chat.title == null) {
                    chatUni = chatUni.call(() -> chatService.generateTitle(chat).invoke(titleCompletion -> {
                        chat.title = titleCompletion.getLastMessage().getContent().trim();
                        results.setChatTitle(chat.title);
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
