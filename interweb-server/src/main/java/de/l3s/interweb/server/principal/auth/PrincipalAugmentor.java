package de.l3s.interweb.server.principal.auth;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;

import io.quarkus.hibernate.reactive.panache.common.WithSessionOnDemand;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;

import de.l3s.interweb.server.principal.User;

@ApplicationScoped
public class PrincipalAugmentor implements SecurityIdentityAugmentor {
    @Override
    @ActivateRequestContext
    @WithSessionOnDemand
    public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {
        if (identity.isAnonymous() || identity.getPrincipal() instanceof User) {
            return Uni.createFrom().item(identity);
        }

        QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder(identity);
        return User.findByName(identity.getPrincipal().getName()).map(principal -> {
            builder.setPrincipal(principal);
            return builder.build();
        });
    }
}
