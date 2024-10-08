package de.l3s.interweb.server.features.chat;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;

import de.l3s.interweb.core.chat.Message;
import de.l3s.interweb.core.chat.Role;
import de.l3s.interweb.core.util.JsonUtils;

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

    public String content;

    @Column(name = "tool_calls")
    public String toolCalls;

    @CreationTimestamp
    public Instant created;

    public ChatMessage() {
    }

    public ChatMessage(final Message message) {
        this.id = message.getId();
        this.role = message.getRole();
        this.content = message.getContent();
        this.toolCalls = JsonUtils.toJson(message.getToolCalls());
        this.created = message.getCreated();
    }

    public Message toMessage() {
        Message message = new Message(role, content);
        message.setCreated(created);
        message.setId(id);
        message.setToolCalls(JsonUtils.fromJson(toolCalls, new TypeReference<>() {}));
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
