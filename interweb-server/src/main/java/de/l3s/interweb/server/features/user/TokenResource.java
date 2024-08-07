package de.l3s.interweb.server.features.user;

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
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import de.l3s.interweb.server.Roles;

@Tag(name = "API Keys", description = "Manage application access")
@Path("/tokens")
@Authenticated
public class TokenResource {

    @Context
    SecurityIdentity securityIdentity;

    @GET
    @RolesAllowed({Roles.USER})
    @Operation(summary = "List all tokens", description = "Use this method to list all tokens")
    public Uni<List<Token>> tokens() {
        return Token.findByUser((User) securityIdentity.getPrincipal());
    }

    @POST
    @WithTransaction
    @RolesAllowed({Roles.USER})
    @Operation(summary = "Create a new token", description = "Use this method to create a new token")
    public Uni<Token> newToken(@Valid CreateToken model) {
        Token token = Token.generate();
        token.name = model.name;
        token.url = model.url;
        token.description = model.description;
        token.user = (User) securityIdentity.getPrincipal();
        return token.persist();
    }

    @DELETE
    @WithTransaction
    @Operation(summary = "Delete a token", description = "Use this method to delete a token. ")
    public Uni<Void> deleteToken(@QueryParam("id") Long tokenId, @QueryParam("token") String token) {
        Uni<Token> item;
        User user = (User) securityIdentity.getPrincipal();
        if (tokenId != null && user != null) {
            item = Token.findById(tokenId, user);
        } else {
            item = Token.findByApiKey(token);
        }

        return item.onItem().ifNotNull().call(PanacheEntityBase::delete).replaceWithVoid();
    }

    public record CreateToken(@NotNull @NotEmpty @Size(max = 255) String name, @Size(max = 512) String url, @Size(max = 1024) String description) {
    }
}
