package de.l3s.interweb.server.principal;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/users/secrets")
@RolesAllowed({Roles.USER})
@Tag(name = "Secrets", description = "Use this methods to override secrets used by connectors.")
public class SecretsResource {

    @Context
    SecurityIdentity securityIdentity;

    @GET
    @Operation(summary = "List all secrets", description = "Use this method to list all secrets")
    public Uni<List<Secrets>> list() {
        return Secrets.listByPrincipal((Principal) securityIdentity.getPrincipal());
    }

    @POST
    @WithTransaction
    @Operation(summary = "Create a new secret", description = "Use this method to create a new secret")
    public Uni<Consumer> create(@Valid CreateBody model) {
        Secrets secrets = new Secrets();
        secrets.name = model.name;
        secrets.secret1 = model.key;
        secrets.secret2 = model.secret;
        secrets.principal = (Principal) securityIdentity.getPrincipal();
        return secrets.persist();
    }

    public record CreateBody(@NotNull @NotEmpty String name, @NotNull @NotEmpty String key, String secret) {
    }
}
