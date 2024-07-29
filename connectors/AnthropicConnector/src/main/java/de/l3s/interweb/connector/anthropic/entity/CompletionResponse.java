package de.l3s.interweb.connector.anthropic.entity;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public class CompletionResponse {
    private String id;
    private String type;
    private String role;
    private String model;
    private List<AnthropicContent> content;
    @JsonProperty("stop_reason")
    private String stopReason;
    @JsonProperty("stop_sequence")
    private Integer stopSequence;
    private AnthropicUsage usage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<AnthropicContent> getContent() {
        return content;
    }

    public void setContent(List<AnthropicContent> content) {
        this.content = content;
    }

    public String getStopReason() {
        return stopReason;
    }

    public void setStopReason(String stopReason) {
        this.stopReason = stopReason;
    }

    public Integer getStopSequence() {
        return stopSequence;
    }

    public void setStopSequence(Integer stopSequence) {
        this.stopSequence = stopSequence;
    }

    public AnthropicUsage getUsage() {
        return usage;
    }

    public void setUsage(AnthropicUsage usage) {
        this.usage = usage;
    }
}
