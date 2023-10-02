package de.l3s.interweb.server.principal;

import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Cacheable
@Table(name = "principal")
public class Principal extends PanacheEntityBase implements java.security.Principal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(readOnly = true)
    public Long id;

    @Email
    @NotEmpty
    @NotNull
    public String email;

    @NotEmpty
    @NotNull
    @Schema(writeOnly = true)
    @JsonIgnore
    public String password;

    @NotNull
    @Schema(readOnly = true)
    public String role = Roles.USER;

    @JsonIgnore
    @OneToMany(mappedBy = "principal", fetch = FetchType.LAZY, orphanRemoval = true)
    public Set<Consumer> tokens;

    protected Principal() {
    }

    /**
     * Adds a new user to the database
     */
    public static Uni<Principal> add(String email, String password) {
        Principal user = new Principal();
        user.email = email;
        user.password = BcryptUtil.bcryptHash(password);
        return user.persist();
    }

    public static Uni<Principal> findByName(String name) {
        return find("email", name).firstResult();
    }

    public static Uni<Principal> findByNameAndPassword(String name, String password) {
        return findByName(name).onItem().ifNotNull().transform(principal -> {
            if (BcryptUtil.matches(password, principal.password)) {
                return principal;
            }

            return null;
        });
    }

    @Override
    @JsonIgnore
    public String getName() {
        return email;
    }
}
