package de.l3s.interweb.server.principal;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/users/tokens")
@Tag(name = "Tokens", description = "Use to create a new client with Api-Key auth.")
public class TokensResource {

    @Context
    SecurityIdentity securityIdentity;

    @GET
    @RolesAllowed({Roles.USER})
    @Operation(summary = "List all tokens", description = "Use this method to list all tokens")
    public Uni<List<Consumer>> tokens() {
        return Consumer.findByPrincipal((User) securityIdentity.getPrincipal());
    }

    @POST
    @WithTransaction
    @RolesAllowed({Roles.USER})
    @Operation(summary = "Create a new token", description = "Use this method to create a new token")
    public Uni<Consumer> newToken(@Valid CreateToken model) {
        Consumer consumer = Consumer.generate();
        consumer.name = model.name;
        consumer.url = model.url;
        consumer.description = model.description;
        consumer.principal = (User) securityIdentity.getPrincipal();
        return consumer.persist();
    }

    @DELETE
    @WithTransaction
    @Operation(summary = "Delete a token", description = "Use this method to delete a token. ")
    public Uni<Void> deleteToken(@QueryParam("id") Long tokenId, @QueryParam("token") String token) {
        Uni<Consumer> item;
        User user = (User) securityIdentity.getPrincipal();
        if (tokenId != null && user != null) {
            item = Consumer.findById(tokenId, user);
        } else {
            item = Consumer.findByApiKey(token);
        }

        return item.onItem().ifNotNull().call(PanacheEntityBase::delete).replaceWithVoid();
    }

    public record CreateToken(@NotNull @NotEmpty @Size(max = 255) String name, @Size(max = 512) String url, @Size(max = 1024) String description) {
    }
}
