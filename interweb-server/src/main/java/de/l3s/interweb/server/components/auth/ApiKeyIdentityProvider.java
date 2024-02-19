package de.l3s.interweb.server.components.auth;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;

import de.l3s.interweb.server.features.user.Token;
import de.l3s.interweb.server.Roles;

@ApplicationScoped
public class ApiKeyIdentityProvider implements IdentityProvider<ApiKeyAuthenticationRequest> {

    @Override
    public Class<ApiKeyAuthenticationRequest> getRequestType() {
        return ApiKeyAuthenticationRequest.class;
    }

    @Override
    @WithSession
    public Uni<SecurityIdentity> authenticate(ApiKeyAuthenticationRequest request, AuthenticationRequestContext authenticationRequestContext) {
        return Token.findByApiKey(request.getValue())
                .onItem().ifNotNull()
                .transform(consumer -> QuarkusSecurityIdentity.builder()
                        .setPrincipal(consumer.user)
                        .addCredential(consumer)
                        .setAnonymous(false)
                        .addRole(Roles.SERVICE)
                        .build());
    }
}
