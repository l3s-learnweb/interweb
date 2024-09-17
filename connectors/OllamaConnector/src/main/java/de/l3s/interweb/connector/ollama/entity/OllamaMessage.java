package de.l3s.interweb.connector.ollama.entity;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.chat.Message;
import de.l3s.interweb.core.chat.CallTool;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class OllamaMessage {
    private String role;
    private String content;
    private List<String> images;
    @JsonProperty("tool_calls")
    private List<CallTool> toolCalls;

    public OllamaMessage() {
    }

    public OllamaMessage(Message message) {
        this.role = message.getRole().name();
        this.content = message.getContent();
        this.toolCalls = message.getToolCalls();
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

    public List<CallTool> getToolCalls() {
        return toolCalls;
    }
}
