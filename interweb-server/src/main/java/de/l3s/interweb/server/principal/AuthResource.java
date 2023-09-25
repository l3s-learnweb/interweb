package de.l3s.interweb.server.principal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Identity", description = "Identity & tokens management")
public class AuthResource {

    @Context
    SecurityIdentity securityIdentity;

    @POST
    @Path("/register")
    @WithTransaction
    @Operation(summary = "Register a new user", description = "Use this method to register a new user")
    public Uni<Principal> register(@Valid CreatePrincipal user) {
        return Principal.findByName(user.email)
                        .onItem().ifNotNull().failWith(() -> new BadRequestException("User already exists"))
                        .chain(() -> Principal.add(user.email, user.password));
    }

    @GET
    @Path("/login")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(summary = "Request JWT token for the given email and password", description = "Use this method to login to the app and manage tokens")
    public Uni<String> login(@NotEmpty @QueryParam("email") String email, @NotEmpty @QueryParam("password") String password) {
        return Principal.findByNameAndPassword(email, password)
                        .onItem().ifNotNull().transform(principal -> Jwt.upn(principal.getName()).groups(Roles.USER).sign())
                        .onItem().ifNull().failWith(() -> new BadRequestException("No user found or password is incorrect"));
    }

    @GET
    @Path("/users/me")
    @Authenticated
    @Operation(summary = "Return the current user", description = "Use this method to get the current user")
    public Principal me() {
        return (Principal) securityIdentity.getPrincipal();
    }

    public record CreatePrincipal(@NotNull @NotEmpty @Email @Size(max = 255) String email, @NotNull @NotEmpty @Size(max = 255) String password) {
    }
}
