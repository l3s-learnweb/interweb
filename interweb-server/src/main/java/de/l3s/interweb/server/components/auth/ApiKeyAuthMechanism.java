package de.l3s.interweb.server.components.auth;

import java.util.Optional;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AsciiString;
import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.AuthenticationRequest;
import io.quarkus.smallrye.jwt.runtime.auth.JWTAuthMechanism;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
import io.quarkus.vertx.http.runtime.security.HttpSecurityUtils;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;

import de.l3s.interweb.server.features.user.Token;

@ApplicationScoped
public class ApiKeyAuthMechanism implements HttpAuthenticationMechanism {
    public static final AsciiString APIKEY_HEADER = AsciiString.cached("Api-Key");
    public static final AsciiString AUTHORIZATION_HEADER = AsciiString.cached("Authorization");
    public static final int AUTHORIZATION_HEADER_PREFIX_LENGTH = JWTAuthMechanism.BEARER.length() + 1;
    public static final int AUTHORIZATION_HEADER_LENGTH = AUTHORIZATION_HEADER_PREFIX_LENGTH + Token.TOKEN_LENGTH;

    @Override
    public Uni<SecurityIdentity> authenticate(RoutingContext context, IdentityProviderManager identityProviderManager) {
        String authHeader = context.request().headers().get(APIKEY_HEADER);
        if (authHeader == null) {
            authHeader = context.request().headers().get(AUTHORIZATION_HEADER);
            if (authHeader != null && authHeader.length() == AUTHORIZATION_HEADER_LENGTH) {
                authHeader = authHeader.substring(AUTHORIZATION_HEADER_PREFIX_LENGTH);
            }
        }
        if (authHeader != null) {
            context.put(HttpAuthenticationMechanism.class.getName(), this);
            return identityProviderManager.authenticate(HttpSecurityUtils.setRoutingContextAttribute(new ApiKeyAuthenticationRequest(authHeader), context));
        }
        return Uni.createFrom().optional(Optional.empty());
    }

    @Override
    public Uni<ChallengeData> getChallenge(RoutingContext context) {
        String authHeader = context.request().headers().get(APIKEY_HEADER);
        if (authHeader == null) {
            return Uni.createFrom().optional(Optional.empty());
        }

        ChallengeData result = new ChallengeData(HttpResponseStatus.UNAUTHORIZED.code(), APIKEY_HEADER, "");
        return Uni.createFrom().item(result);
    }

    @Override
    public Set<Class<? extends AuthenticationRequest>> getCredentialTypes() {
        return Set.of(ApiKeyAuthenticationRequest.class);
    }
}
