package de.l3s.interweb.server.features.api;

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
import de.l3s.interweb.server.features.user.User;

@Tag(name = "API Keys", description = "Manage application access (These endpoints does not work with Api-Key authentication)")
@Path("/api_keys")
@Authenticated
public class ApiKeysResource {

    @Context
    SecurityIdentity securityIdentity;

    @GET
    @RolesAllowed({Roles.USER, Roles.ADMIN})
    @Operation(summary = "List all api keys", description = "Use this method to list all api keys")
    public Uni<List<ApiKey>> list() {
        return ApiKey.findByUser((User) securityIdentity.getPrincipal());
    }

    @POST
    @WithTransaction
    @RolesAllowed({Roles.USER, Roles.ADMIN})
    @Operation(summary = "Create a new api key", description = "Use this method to create a new api key")
    public Uni<ApiKey> create(@Valid CreateToken model) {
        ApiKey apikey = ApiKey.generate();
        apikey.name = model.name;
        apikey.url = model.url;
        apikey.description = model.description;
        apikey.user = (User) securityIdentity.getPrincipal();
        return apikey.persist();
    }

    @DELETE
    @WithTransaction
    @Operation(summary = "Delete an api key", description = "Use this method to delete an api key. ")
    public Uni<Void> delete(@QueryParam("id") Long id, @QueryParam("apikey") String apikey) {
        Uni<ApiKey> item;
        User user = (User) securityIdentity.getPrincipal();
        if (id != null && user != null) {
            item = ApiKey.findById(id, user);
        } else {
            item = ApiKey.findByApikey(apikey);
        }

        return item.onItem().ifNotNull().call(PanacheEntityBase::delete).replaceWithVoid();
    }

    @GET
    @Path("/usage")
    @RolesAllowed({Roles.USER, Roles.APPLICATION})
    public Uni<UsageSummary> usage(@QueryParam("id") Long id) {
        Uni<ApiKey> item;
        if (id != null) {
            item = ApiKey.findById(id);
        } else {
            item = Uni.createFrom().item(securityIdentity.getCredential(ApiKey.class));
        }

        return item.flatMap(UsageSummary::findByApikey);
    }

    public record CreateToken(@NotNull @NotEmpty @Size(max = 255) String name, @Size(max = 512) String url, @Size(max = 1024) String description) {
    }
}
