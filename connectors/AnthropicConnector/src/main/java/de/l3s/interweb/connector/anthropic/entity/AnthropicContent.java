package de.l3s.interweb.connector.anthropic.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;


@RegisterForReflection
public class AnthropicContent {
    private String type;
    private String text;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
