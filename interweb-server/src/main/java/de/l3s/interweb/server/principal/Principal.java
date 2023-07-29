package de.l3s.interweb.server.principal;

import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

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

    @NotEmpty
    @NotNull
    public String username;

    @NotEmpty
    @NotNull
    @Schema(writeOnly = true)
    public String password;

    @NotEmpty
    @NotNull
    @Email
    public String email;

    @NotNull
    @Schema(readOnly = true)
    public String role = Roles.USER;

    @JsonIgnore
    @OneToMany(mappedBy = "principal", fetch = FetchType.LAZY, orphanRemoval = true)
    public Set<Consumer> tokens;

    public Principal() {
    }

    public static Uni<Principal> findByName(String name) {
        return find("username", name).firstResult();
    }

    @Override
    public String getName() {
        return username;
    }
}
