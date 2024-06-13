package de.l3s.interweb.core.completion;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.ConnectorResults;

@RegisterForReflection
@JsonIgnoreProperties({"elapsed_time", "created"})
public class Choice extends ConnectorResults {
    private int index;
    @JsonProperty("finish_reason")
    private String finishReason;
    private Message message;

    public Choice() {
    }

    public Choice(int index, String finishReason, Message message) {
        this.index = index;
        this.finishReason = finishReason;
        this.message = message;
    }

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

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
