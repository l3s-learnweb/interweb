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
import de.l3s.interweb.server.features.user.User;

@ApplicationScoped
public class ApiKeyIdentityProvider implements IdentityProvider<ApiKeyAuthenticationRequest> {

    @Override
    public Class<ApiKeyAuthenticationRequest> getRequestType() {
        return ApiKeyAuthenticationRequest.class;
    }

    @Override
    @WithSession
    public Uni<SecurityIdentity> authenticate(ApiKeyAuthenticationRequest request, AuthenticationRequestContext authenticationRequestContext) {
        return ApiKey.findByApikey(request.getValue())
            .onItem().ifNotNull()
            .transform(key -> QuarkusSecurityIdentity.builder()
                .setPrincipal(key.user)
                .addCredential(key)
                .setAnonymous(false)
                .addRole(Roles.APPLICATION)
                .addPermissionChecker(permission -> {
                    try {
                        final User.Permission req = User.Permission.valueOf(permission.getName());
                        return Uni.createFrom().item(key.user.permissions.contains(req));
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .build());
    }
}
