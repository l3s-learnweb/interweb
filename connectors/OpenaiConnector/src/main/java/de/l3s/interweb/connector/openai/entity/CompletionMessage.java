package de.l3s.interweb.connector.openai.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.runtime.annotations.RegisterForReflection;

import de.l3s.interweb.core.chat.Message;

@RegisterForReflection
public final class CompletionMessage {
    private String role;
    @JsonIgnore
    private String name;
    private String content;

    public CompletionMessage(Message message) {
        this.role = message.getRole().name();
        this.name = message.getName();
        this.content = message.getContent();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
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

    public void setContent(String content) {
        this.content = content;
    }
}
