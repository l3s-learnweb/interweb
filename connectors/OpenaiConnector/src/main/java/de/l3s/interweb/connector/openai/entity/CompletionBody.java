package de.l3s.interweb.connector.openai.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;

import de.l3s.interweb.core.completion.CompletionQuery;

@RegisterForReflection
public final class CompletionBody {

    private List<CompletionMessage> messages;

    private Double temperature;

    @JsonProperty("top_p")
    private Double topP;

    @JsonProperty("frequency_penalty")
    private Double frequencyPenalty;

    @JsonProperty("presence_penalty")
    private Double presencePenalty;

    @JsonProperty("max_tokens")
    private Integer maxTokens;

    public CompletionBody(CompletionQuery query) {
        this.messages = query.getMessages().stream().map(CompletionMessage::new).toList();
        this.temperature = query.getTemperature();
        this.topP = query.getTopP();
        this.frequencyPenalty = query.getPresencePenalty();
        this.presencePenalty = query.getPresencePenalty();
        this.maxTokens = query.getMaxTokens();
    }

    public List<CompletionMessage> getMessages() {
        return messages;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Double getTopP() {
        return topP;
    }

    public Double getFrequencyPenalty() {
        return frequencyPenalty;
    }

    public Double getPresencePenalty() {
        return presencePenalty;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }
}
