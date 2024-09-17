package de.l3s.interweb.connector.openai.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public class OpenaiChoice {
    private int index;
    @JsonProperty("finish_reason")
    private String finishReason;
    private OpenaiMessage message;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getFinishReason() {
        return finishReason;
    }

    public void setFinishReason(String finishReason) {
        this.finishReason = finishReason;
    }

    public OpenaiMessage getMessage() {
        return message;
    }

    public void setMessage(OpenaiMessage message) {
        this.message = message;
    }
}
