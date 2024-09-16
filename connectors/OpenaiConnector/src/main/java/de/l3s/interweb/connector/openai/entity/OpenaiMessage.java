package de.l3s.interweb.connector.openai.entity;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.chat.Message;
import de.l3s.interweb.core.chat.ToolCall;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class OpenaiMessage {
    private String role;
    @JsonIgnore
    private String name;
    private String content;
    private String refusal;
    @JsonProperty("tool_calls")
    private List<ToolCall> toolCalls;

    public OpenaiMessage(Message message) {
        this.role = message.getRole().name();
        this.name = message.getName();
        this.content = message.getContent();
        this.refusal = message.getRefusal();
        this.toolCalls = message.getToolCalls();
    }

    public String getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public String getRefusal() {
        return refusal;
    }

    public List<ToolCall> getToolCalls() {
        return toolCalls;
    }
}
