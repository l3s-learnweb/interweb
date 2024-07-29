package de.l3s.interweb.core.chat;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "title", "model", "messages", "used_tokens", "estimated_cost", "created"})
public class Conversation extends CompletionsQuery implements Serializable {
    @Serial
    private static final long serialVersionUID = 7841527600165525951L;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversation that = (Conversation) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
