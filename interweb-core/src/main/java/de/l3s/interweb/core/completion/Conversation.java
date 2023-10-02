package de.l3s.interweb.core.completion;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "title", "model", "messages", "used_tokens", "estimated_cost", "created"})
public class Conversation extends CompletionQuery {
    @JsonProperty("title")
    private String title;

    @JsonProperty("used_tokens")
    private Integer usedTokens;

    @JsonProperty("estimated_cost")
    private Double estimatedCost;

    @JsonProperty("created")
    private Instant created;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getUsedTokens() {
        return usedTokens;
    }

    public void setUsedTokens(Integer usedTokens) {
        this.usedTokens = usedTokens;
    }

    public Double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }
}
