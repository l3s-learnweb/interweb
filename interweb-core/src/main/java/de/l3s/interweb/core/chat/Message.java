package de.l3s.interweb.core.chat;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 7110951353515625780L;

    public enum Role {
        system,
        user,
        assistant
    }

    private Long id;
    @NotNull
    private Role role;
    private String name;
    @NotEmpty
    private String content;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
