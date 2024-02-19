package de.l3s.interweb.server.components.auth;

import java.util.Optional;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AsciiString;
import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.AuthenticationRequest;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
import io.quarkus.vertx.http.runtime.security.HttpSecurityUtils;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;

@ApplicationScoped
public class ApiKeyAuthMechanism implements HttpAuthenticationMechanism {
    public static final AsciiString API_KEY = AsciiString.cached("Api-Key");

    @Override
    public Uni<SecurityIdentity> authenticate(RoutingContext context, IdentityProviderManager identityProviderManager) {
        String authHeader = context.request().headers().get(API_KEY);
        if (authHeader != null) {
            context.put(HttpAuthenticationMechanism.class.getName(), this);
            return identityProviderManager.authenticate(HttpSecurityUtils.setRoutingContextAttribute(new ApiKeyAuthenticationRequest(authHeader), context));
        }
        return Uni.createFrom().optional(Optional.empty());
    }

    @Override
    public Uni<ChallengeData> getChallenge(RoutingContext context) {
        String authHeader = context.request().headers().get(API_KEY);
        if (authHeader == null) {
            return Uni.createFrom().optional(Optional.empty());
        }

        ChallengeData result = new ChallengeData(HttpResponseStatus.UNAUTHORIZED.code(), API_KEY, "");
        return Uni.createFrom().item(result);
    }

    @Override
    public Set<Class<? extends AuthenticationRequest>> getCredentialTypes() {
        return Set.of(ApiKeyAuthenticationRequest.class);
    }
}
