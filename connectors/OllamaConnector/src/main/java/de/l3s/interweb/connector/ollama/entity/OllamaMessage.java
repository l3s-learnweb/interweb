package de.l3s.interweb.connector.ollama.entity;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import de.l3s.interweb.core.completion.Message;

@RegisterForReflection
public final class OllamaMessage {
    private String role;
    private String content;
    private List<String> images;

    public OllamaMessage() {
    }

    public OllamaMessage(Message message) {
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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
