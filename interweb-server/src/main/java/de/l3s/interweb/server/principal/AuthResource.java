package de.l3s.interweb.server.principal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/")
@Tag(name = "Identity", description = "Identity & tokens management")
public class AuthResource {

    @Context
    SecurityIdentity securityIdentity;

    @POST
    @Path("/register")
    @WithTransaction
    @Operation(summary = "Register a new user", description = "Use this method to register a new user")
    public Uni<Principal> register(@Valid Principal user) {
        return user.persist();
    }

    @GET
    @Path("/login")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(summary = "Request JWT token for the given username and password", description = "Use this method to login to the app and manage secrets/tokens")
    public Uni<String> login(@NotEmpty @QueryParam("login") String login, @NotEmpty @QueryParam("password") String password) {
        return Principal.findByName(login).map(existingUser -> {
            if (existingUser == null || !existingUser.password.equals(password)) {
                throw new WebApplicationException(Response.status(404).entity("No user found or password is incorrect").build());
            }

            return Jwt.upn(existingUser.username).groups(Roles.USER).sign();
        });
    }

    @GET
    @Path("/users/me")
    @Authenticated
    @Operation(summary = "Return the current user", description = "Use this method to get the current user")
    public Principal me() {
        return (Principal) securityIdentity.getPrincipal();
    }
}
