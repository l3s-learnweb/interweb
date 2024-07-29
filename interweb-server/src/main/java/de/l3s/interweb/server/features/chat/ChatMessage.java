package de.l3s.interweb.server.features.chat;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.l3s.interweb.core.chat.Message;
import de.l3s.interweb.core.chat.Role;

@Entity
@Cacheable
@Table(name = "chat_message")
public class ChatMessage extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    public Chat chat;

    @NotNull
    public Role role;

    @NotEmpty
    @NotNull
    @Column(columnDefinition = "TEXT")
    public String content;

    @CreationTimestamp
    public Instant created;

    public ChatMessage() {
    }

    public ChatMessage(final Message message) {
        this.id = message.getId();
        this.role = message.getRole();
        this.content = message.getContent();
        this.created = message.getCreated();
    }

    public Message toMessage() {
        Message message = new Message(role, content);
        message.setCreated(created);
        message.setId(id);
        return message;
    }

    public static Uni<List<ChatMessage>> listByChat(UUID id) {
        return list("chat.id", id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return role == that.role && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, content);
    }
}
