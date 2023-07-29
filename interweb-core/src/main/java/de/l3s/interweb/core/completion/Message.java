package de.l3s.interweb.core.completion;

import java.time.Instant;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {
    public enum Role {
        system,
        user,
        assistant
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;
    @NotNull
    private Role role;
    @NotEmpty
    private String content;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Instant created;

    public Message() {
    }

    public Message(final Role role) {
        this.role = role;
    }

    public Message(Role role, String content) {
        this.role = role;
        this.content = content;
        this.created = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(final Role role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(final Instant created) {
        this.created = created;
    }

    public static Message system(String content) {
        return new Message(Role.system, content);
    }

    public static Message user(String content) {
        return new Message(Role.user, content);
    }

    public static Message assistant(String content) {
        return new Message(Role.assistant, content);
    }
}
