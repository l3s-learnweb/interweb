package de.l3s.interweb.core.completion;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.ConnectorResults;

public class Choice extends ConnectorResults {
    private int index;
    @JsonProperty("finish_reason")
    private String finishReason;
    private Message message;

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

    @Override
    @JsonIgnore
    public long getElapsedTime() {
        return super.getElapsedTime();
    }

    @Override
    @JsonIgnore
    public Instant getCreated() {
        return super.getCreated();
    }
}
