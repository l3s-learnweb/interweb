package de.l3s.interweb.server.chat;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.l3s.interweb.server.principal.Consumer;

@Entity
@Cacheable
@Table(name = "chat")
public class Chat extends PanacheEntityBase {
    @Id
    @UuidGenerator
    @GeneratedValue
    public UUID id;

    @NotNull
    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    public Consumer consumer;

    @NotNull
    @Size(max = 32)
    public String model;

    @NotNull
    @ColumnDefault("0")
    public Integer used_tokens = 0;

    @NotNull
    @ColumnDefault("0")
    public Double estimated_cost = 0d;

    @CreationTimestamp
    public Instant created;

    @JsonIgnore
    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();

    public Chat() {
    }

    public void addMessage(final ChatMessage message) {
        messages.add(message);
        message.chat = this;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void addCosts(int tokens, double cost) {
        this.used_tokens += tokens;
        this.estimated_cost += cost;
    }

    public static Uni<Chat> findById(UUID id) {
        return find("id", id).firstResult();
    }

    public static Uni<Chat> findByIdWithMessages(UUID id) {
        return find("from Chat p left join fetch p.messages WHERE p.id = ?1", id).firstResult();
    }

    public static Uni<List<Chat>> findByConsumer(Consumer consumer) {
        return list("consumer.id", consumer.id);
    }
}
