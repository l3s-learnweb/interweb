package de.l3s.interweb.server.principal;

import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.security.credential.Credential;
import io.smallrye.mutiny.Uni;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.l3s.interweb.core.util.StringUtils;

@Entity
@Cacheable
@Table(name = "principal_consumer")
public class Consumer extends PanacheEntityBase implements Credential {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    public User principal;

    @NotEmpty
    @NotNull
    public String name;

    @Size(max = 512)
    public String url;

    @Size(max = 1024)
    public String description;

    @NotEmpty
    @NotNull
    @Column(unique = true, length = 64)
    public String apikey;

    public Consumer() {
        // required for Panache
    }

    public static Consumer generate() {
        Consumer consumer = new Consumer();
        consumer.apikey = StringUtils.randomAlphanumeric(64);
        return consumer;
    }

    public static Uni<List<Consumer>> findByPrincipal(User user) {
        return list("principal.id", user.id);
    }

    public static Uni<Consumer> findById(Object id, User user) {
        return find("id = ?1 and principal.id = ?2", id, user.id).firstResult();
    }

    public static Uni<Consumer> findByApiKey(String apikey) {
        return find("apikey", apikey).firstResult();
    }
}
