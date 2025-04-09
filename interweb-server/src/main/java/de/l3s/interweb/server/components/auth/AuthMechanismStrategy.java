package de.l3s.interweb.server.components.auth;

import java.util.HashSet;
import java.util.Set;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;

import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.AuthenticationRequest;
import io.quarkus.smallrye.jwt.runtime.auth.JWTAuthMechanism;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
import io.quarkus.vertx.http.runtime.security.HttpCredentialTransport;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;

@Alternative
@Priority(1)
@ApplicationScoped
public class AuthMechanismStrategy implements HttpAuthenticationMechanism {

    @Inject
    JWTAuthMechanism jwtMechanism;

    @Inject
    ApiKeyAuthMechanism apikeyMechanism;

    @Override
    public Uni<SecurityIdentity> authenticate(RoutingContext context, IdentityProviderManager identityProviderManager) {
        return selectBetweenJwtAndApikey(context).authenticate(context, identityProviderManager);
    }

    @Override
    public Uni<ChallengeData> getChallenge(RoutingContext context) {
        return selectBetweenJwtAndApikey(context).getChallenge(context);
    }

    @Override
    public Set<Class<? extends AuthenticationRequest>> getCredentialTypes() {
        Set<Class<? extends AuthenticationRequest>> credentialTypes = new HashSet<>();
        credentialTypes.addAll(jwtMechanism.getCredentialTypes());
        credentialTypes.addAll(apikeyMechanism.getCredentialTypes());
        return credentialTypes;
    }

    @Override
    public Uni<HttpCredentialTransport> getCredentialTransport(RoutingContext context) {
        return selectBetweenJwtAndApikey(context).getCredentialTransport(context);
    }

    private HttpAuthenticationMechanism selectBetweenJwtAndApikey(RoutingContext context) {
        String path = context.normalizedPath(); // there are some paths for which we prefer JWT over API-Key
        String apikeyHeader = context.request().headers().get(ApiKeyAuthMechanism.APIKEY_HEADER);
        String authHeader = context.request().headers().get(ApiKeyAuthMechanism.AUTHORIZATION_HEADER);

        // when provided both, prefer Api-Key, except for /api_keys routes (required for Swagger UI)
        if (apikeyHeader != null && (authHeader == null || !path.equals("/api_keys"))) {
            return apikeyMechanism;
        }
        if (authHeader != null && authHeader.length() == ApiKeyAuthMechanism.AUTHORIZATION_HEADER_LENGTH) {
            return apikeyMechanism;
        }
        return jwtMechanism;
    }
}
