package de.l3s.interweb.server.features.user;

import java.security.Principal;
import java.time.Instant;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @NotNull
    @Schema(readOnly = true)
    public String role = Roles.USER;

    @NotNull
    public boolean approved = false;

    @UpdateTimestamp
    public Instant updated;

    @CreationTimestamp
    public Instant created;

    protected User() {
    }

    public static Uni<User> findByEmail(String name) {
        return find("email", name).singleResult();
    }

    @Override
    @JsonIgnore
    public String getName() {
        return email;
    }
}
