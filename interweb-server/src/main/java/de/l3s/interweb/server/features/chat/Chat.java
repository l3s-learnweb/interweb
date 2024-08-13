package de.l3s.interweb.server.features.chat;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.l3s.interweb.core.chat.Usage;

import de.l3s.interweb.core.chat.UsageCost;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.l3s.interweb.server.PanacheUtils;
import de.l3s.interweb.server.features.user.ApiKey;

@Entity
@Cacheable
@Table(name = "chat", indexes = {
    @Index(name = "user_index", columnList = "user"),
})
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    @Column(name = "used_tokens")
    public Integer usedTokens = 0;

    @NotNull
    @Column(name = "est_cost")
    public Double estimatedCost = 0d;

    @UpdateTimestamp
    public Instant updated;

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

    public void addCosts(Usage usage, UsageCost cost) {
        this.usedTokens += usage.getTotalTokens();
        this.estimatedCost += cost.getResponse();
    }

    public Uni<Integer> updateTitleAndUsage() {
        return update("title = ?1, usedTokens = ?2, estimatedCost = ?3 where id = ?4", title, usedTokens, estimatedCost, id);
    }

    public static Uni<Chat> findById(ApiKey apikey, UUID id) {
        return find("apikey.id = ?1 AND id = ?2", apikey.id, id).firstResult();
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
}
