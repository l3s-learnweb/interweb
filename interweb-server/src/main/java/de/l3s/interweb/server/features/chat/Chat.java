package de.l3s.interweb.server.features.chat;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.l3s.interweb.server.PanacheUtils;
import de.l3s.interweb.server.features.user.ApiKey;

@Entity
@Cacheable
@Table(name = "chat", indexes = {
    @Index(name = "user_index", columnList = "user"),
})
public class Chat extends PanacheEntityBase {
    @Id
    @UuidGenerator
    @GeneratedValue
    public UUID id;

    @NotNull
    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    public ApiKey apikey;

    @Size(max = 32)
    public String user;

    @NotNull
    @Size(max = 32)
    public String model;

    @Size(max = 512)
    public String title;

    @NotNull
    @ColumnDefault("0")
    public Integer usedTokens = 0;

    @NotNull
    @ColumnDefault("0")
    public Double estimatedCost = 0d;

    @CreationTimestamp
    public Instant created;

    @JsonIgnore
    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();

    public Chat() {
        // required by Panache
    }

    public void addMessage(final ChatMessage message) {
        messages.add(message);
        message.chat = this;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void addCosts(int tokens, double cost) {
        this.usedTokens += tokens;
        this.estimatedCost += cost;
    }

    public Uni<Integer> updateTitle() {
        return update("title = ?1 where id = ?2", title, id);
    }

    public Uni<Integer> updateTitleAndUsage() {
        return update("title = ?1, usedTokens = ?2, estimatedCost = ?3 where id = ?4", title, usedTokens, estimatedCost, id);
    }

    public static Uni<List<Chat>> listByUser(ApiKey apikey, String user, String order, int page, int perPage) {
        String query = "apikey.id = :id AND usedTokens != 0";
        Parameters params = Parameters.with("id", apikey.id);
        Sort sort = PanacheUtils.createSort(order);

        if (user != null) {
            params.and("user", user);
            query += " AND user = :user";
        }

        if (page <= 0) {
            page = 1; // foolproof
        }

        return find(query, sort, params).page(page - 1, perPage).list();
    }

    public static Uni<Chat> findById(ApiKey apikey, UUID id) {
        return find("apikey.id = ?1 AND id = ?2", apikey.id, id).firstResult();
    }
}
