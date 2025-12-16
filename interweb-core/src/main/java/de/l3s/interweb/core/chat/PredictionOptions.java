package de.l3s.interweb.core.chat;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public class PredictionOptions {

    /**
     * The type of the predicted output. In order to pass the content, set this to "content".
     */
    @JsonProperty("type")
    private String type;

    /**
     * The content that the model should use for prediction.
     */
    @JsonProperty("content")
    private Object content;

    public PredictionOptions() {
    }

    public PredictionOptions(String type, Object content) {
        this.type = type;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}

