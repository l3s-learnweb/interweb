package de.l3s.interweb.server.features.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.hibernate.reactive.mutiny.Mutiny;

import de.l3s.interweb.core.chat.Conversation;
import de.l3s.interweb.core.chat.Role;
import de.l3s.interweb.core.util.StringUtils;
import de.l3s.interweb.server.features.user.Token;

@Tag(name = "Chats", description = "Retrieve and manage chats")
@Path("/chats")
@Authenticated
public class ChatsResource {

    @Context
    SecurityIdentity securityIdentity;

    @GET
    public Uni<List<Chat>> chats(
        @QueryParam("user") String user,
        @QueryParam("sort") @DefaultValue("-created") String order,
        @QueryParam("page") @DefaultValue("1") Integer page,
        @QueryParam("perPage") @DefaultValue("20") Integer perPage
    ) {
        ApiKey apikey = securityIdentity.getCredential(ApiKey.class);

        return Chat.listByUser(apikey, user, order, page, perPage)
            .call(chats -> Multi.createFrom().iterable(chats)
                .filter(chat -> chat.title == null)
                .map(ChatsResource::createChatTitle)
                .collect()
                .asList());
    }

    private static Uni<List<ChatMessage>> createChatTitle(Chat chat) {
        return Mutiny.fetch(chat.getMessages()).call(() -> {
            if (!chat.getMessages().isEmpty()) {
                for (ChatMessage message : chat.getMessages()) {
                    if (message.role == Role.user) {
                        chat.title = StringUtils.shorten(message.content, 120);
                        return chat.updateTitle();
                    }
                }
            }

            return Uni.createFrom().voidItem();
        });
    }

    @GET
    @Path("{uuid}")
    public Uni<Conversation> chat(@PathParam("uuid") UUID id) {
        ApiKey apikey = securityIdentity.getCredential(ApiKey.class);

        return Chat.findById(apikey, id).call(chat -> Mutiny.fetch(chat.getMessages())).map(chat -> {
            Conversation conversation = new Conversation();
            conversation.setId(chat.id);
            conversation.setTitle(chat.title);
            conversation.setUsedTokens(chat.usedTokens);
            conversation.setEstimatedCost(chat.estimatedCost);
            conversation.setCreated(chat.created);
            conversation.setMessages(new ArrayList<>(chat.getMessages().stream().map(ChatMessage::toMessage).toList()));
            return conversation;
        });
    }
}
