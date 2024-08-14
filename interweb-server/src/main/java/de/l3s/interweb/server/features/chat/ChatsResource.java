package de.l3s.interweb.server.features.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.hibernate.reactive.mutiny.Mutiny;

import de.l3s.interweb.core.chat.Conversation;
import de.l3s.interweb.server.Roles;
import de.l3s.interweb.server.features.api.ApiKey;

@Tag(name = "Chats", description = "Retrieve and manage chats")
@Path("/chats")
@RolesAllowed({Roles.APPLICATION})
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

        return Chat.listByUser(apikey, user, order, page, perPage);
    }

    @GET
    @Path("{uuid}")
    public Uni<Conversation> chat(@PathParam("uuid") UUID id) {
        ApiKey apikey = securityIdentity.getCredential(ApiKey.class);

        return Chat.findById(apikey, id).call(chat -> Mutiny.fetch(chat.getMessages())).map(chat -> {
            Conversation conversation = new Conversation();
            conversation.setId(chat.id);
            conversation.setModel(chat.model);
            conversation.setTitle(chat.title);
            conversation.setUsedTokens(chat.usedTokens);
            conversation.setEstimatedCost(chat.estimatedCost);
            conversation.setCreated(chat.created);
            conversation.setMessages(new ArrayList<>(chat.getMessages().stream().map(ChatMessage::toMessage).toList()));
            return conversation;
        });
    }

    @DELETE
    @WithTransaction
    @Path("{uuid}")
    public Uni<Void> delete(@PathParam("uuid") UUID id) {
        ApiKey apikey = securityIdentity.getCredential(ApiKey.class);

        return Chat.findById(apikey, id).call(PanacheEntityBase::delete).replaceWithVoid();
    }
}
