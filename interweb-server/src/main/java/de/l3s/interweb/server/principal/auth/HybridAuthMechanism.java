package de.l3s.interweb.server.principal.auth;

import java.util.Set;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;

import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.AuthenticationRequest;
import io.quarkus.security.identity.request.TokenAuthenticationRequest;
import io.quarkus.smallrye.jwt.runtime.auth.JWTAuthMechanism;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;

@Alternative
@Priority(1)
@ApplicationScoped
public class HybridAuthMechanism implements HttpAuthenticationMechanism {

    @Inject
    JWTAuthMechanism jwtMechanism;

    @Inject
    ApiKeyAuthMechanism apiKeyMechanism;

    @Override
    public Uni<SecurityIdentity> authenticate(RoutingContext context, IdentityProviderManager identityProviderManager) {
        return apiKeyMechanism.authenticate(context, identityProviderManager)
                .onItem().ifNull().switchTo(() -> jwtMechanism.authenticate(context, identityProviderManager));
    }

    @Override
    public Uni<ChallengeData> getChallenge(RoutingContext context) {
        return apiKeyMechanism.getChallenge(context)
                .onItem().ifNull().switchTo(() -> jwtMechanism.getChallenge(context));
    }

    @Override
    public Set<Class<? extends AuthenticationRequest>> getCredentialTypes() {
        return Set.of(TokenAuthenticationRequest.class, ApiKeyAuthenticationRequest.class);
    }
}
