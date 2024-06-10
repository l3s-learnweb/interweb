package de.l3s.interweb.server.features.user;

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
@Table(name = "user_token")
public class Token extends PanacheEntityBase implements Credential {
    public static final int TOKEN_LENGTH = 64;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    public User user;

    @NotEmpty
    @NotNull
    public String name;

    @Size(max = 512)
    public String url;

    @Size(max = 1024)
    public String description;

    @NotEmpty
    @NotNull
    @Column(unique = true, length = TOKEN_LENGTH)
    public String apikey;

    public Token() {
        // required for Panache
    }

    public static Token generate() {
        Token token = new Token();
        token.apikey = StringUtils.randomAlphanumeric(TOKEN_LENGTH);
        return token;
    }

    public static Uni<List<Token>> findByUser(User user) {
        return list("user.id", user.id);
    }

    public static Uni<Token> findById(Object id, User user) {
        return find("id = ?1 and user.id = ?2", id, user.id).firstResult();
    }

    public static Uni<Token> findByApiKey(String apikey) {
        return find("apikey", apikey).firstResult();
    }
}
