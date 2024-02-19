package de.l3s.interweb.server.features.user;

import java.security.Principal;
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

import de.l3s.interweb.server.Roles;

@Entity
@Cacheable
@Table(name = "user")
public class User extends PanacheEntityBase implements Principal {
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
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    public Set<Token> tokens;

    protected User() {
    }

    /**
     * Adds a new user to the database
     */
    public static Uni<User> add(String email, String password) {
        User user = new User();
        user.email = email;
        user.password = BcryptUtil.bcryptHash(password);
        return user.persist();
    }

    public static Uni<User> findByName(String name) {
        return find("email", name).firstResult();
    }

    public static Uni<User> findByNameAndPassword(String name, String password) {
        return findByName(name).onItem().ifNotNull().transform(user -> {
            if (BcryptUtil.matches(password, user.password)) {
                return user;
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
