package de.l3s.interweb.server.principal;

import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Cacheable
@Table(name = "principal_secrets")
public class Secrets extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    public Principal principal;

    @NotEmpty
    @NotNull
    @Length(min = 3, max = 255)
    public String name;

    @NotEmpty
    @NotNull
    @Column(name = "secret1")
    public String secret1;

    @NotEmpty
    @Column(name = "secret2")
    public String secret2;

    public Secrets() {
    }

    public static Uni<List<Secrets>> listByPrincipal(Principal principal) {
        return list("principal.id", principal.id);
    }

    public static Uni<Secrets> findByPrincipalAndName(Principal principal, String name) {
        return find("principal.id = ?1 and name = ?2", principal.id, name).firstResult();
    }
}
