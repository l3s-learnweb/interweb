package de.l3s.interweb.server.features.user;

import java.time.Duration;
import java.time.Instant;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.security.credential.Credential;
import io.smallrye.mutiny.Uni;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.l3s.interweb.core.util.StringUtils;

@Entity
@Cacheable
@Table(name = "user_token")
public class UserToken extends PanacheEntityBase implements Credential {

    public enum Type {
        login(Duration.ofHours(6), 32);

        private final Duration duration;
        private final int size;

        Type(Duration duration, int size) {
            this.duration = duration;
            this.size = size;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    public User user;

    @NotEmpty
    @NotNull
    public String type;

    @NotEmpty
    @NotNull
    public String token;

    @CreationTimestamp
    public Instant created;

    public UserToken() {
        // required for Panache
    }

    public static UserToken generate(Type type) {
        UserToken key = new UserToken();
        key.type = type.name();
        key.token = StringUtils.randomAlphanumeric(type.size);
        return key;
    }

    public static Uni<UserToken> findByToken(Type type, String token) {
        return find("type = ?1 and token = ?2 and created > ?3", type.name(), token, Instant.now().minus(type.duration)).singleResult();
    }
}
