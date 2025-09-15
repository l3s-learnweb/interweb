package de.l3s.interweb.server.components.auth;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;

import de.l3s.interweb.server.Roles;
import de.l3s.interweb.server.features.api.ApiKey;

import org.hibernate.reactive.mutiny.Mutiny;

@ApplicationScoped
public class ApiKeyIdentityProvider implements IdentityProvider<ApiKeyAuthenticationRequest> {

    @Override
    public Class<ApiKeyAuthenticationRequest> getRequestType() {
        return ApiKeyAuthenticationRequest.class;
    }

    @Override
    @WithSession
    public Uni<SecurityIdentity> authenticate(ApiKeyAuthenticationRequest request, AuthenticationRequestContext context) {
        return ApiKey.findByApikey(request.getValue())
            .onItem().ifNotNull()
            .call(apikey -> Mutiny.fetch(apikey.user))
            .onItem().transform(apikey -> QuarkusSecurityIdentity.builder()
                .setPrincipal(apikey.user)
                .addCredential(apikey)
                .setAnonymous(false)
                .addRole(Roles.APPLICATION)
                .build());
    }
}
