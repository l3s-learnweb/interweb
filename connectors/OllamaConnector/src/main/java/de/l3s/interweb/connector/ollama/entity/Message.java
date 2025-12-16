package de.l3s.interweb.connector.ollama.entity;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.chat.CallTool;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class Message {
    private Role role;
    private String content;
    private String thinking;
    private List<String> images;
    @JsonProperty("tool_calls")
    private List<CallTool> toolCalls;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getThinking() {
        return thinking;
    }

    public void setThinking(String thinking) {
        this.thinking = thinking;
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

    public void setToolCalls(List<CallTool> toolCalls) {
        this.toolCalls = toolCalls;
    }

    public static Message of(de.l3s.interweb.core.chat.Message message) {
        Message result = new Message();
        result.setRole(Role.of(message.getRole()));
        result.setContent(message.getContent());
        result.setToolCalls(message.getToolCalls());
        return result;
    }

    public de.l3s.interweb.core.chat.Message toMessage() {
        de.l3s.interweb.core.chat.Message result = new de.l3s.interweb.core.chat.Message();
        result.setRole(this.getRole().toRole());
        result.setContent(this.getContent());
        result.setToolCalls(this.getToolCalls());
        return result;
    }
}
