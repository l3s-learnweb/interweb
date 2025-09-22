package de.l3s.interweb.connector.anthropic.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;

import de.l3s.interweb.core.chat.Message;

@RegisterForReflection
public final class CompletionMessage {
    private String role;
    private String content;

    public CompletionMessage(Message message) {
        this.role = message.getRole().name();
        this.content = message.getContent();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
