package de.l3s.interweb.connector.openai.entity;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.chat.CompletionsQuery;
import de.l3s.interweb.core.chat.ResponseFormat;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    /**
     * How many completions to generate for each prompt. Minimum of 1 (default) and maximum of 128 allowed.
     * Note: Because this parameter generates many completions, it can quickly consume your token quota.
     */
    private Integer n;

    /**
     * If specified, our system will make the best effort to sample deterministically,
     * such that repeated requests with the same seed and parameters should return the same result.
     * Determinism isn't guaranteed, and you should refer to the system_fingerprint response parameter to monitor changes in the backend.
     */
    private Integer seed;

    @JsonProperty("response_format")
    private ResponseFormat responseFormat;

    private String[] stop;

    public CompletionBody(CompletionsQuery query) {
        this.messages = query.getMessages().stream().map(CompletionMessage::new).toList();
        this.temperature = query.getTemperature();
        this.topP = query.getTopP();
        this.frequencyPenalty = query.getPresencePenalty();
        this.presencePenalty = query.getPresencePenalty();
        this.maxTokens = query.getMaxTokens();
        this.n = query.getN();
        this.seed = query.getSeed();
        this.responseFormat = query.getResponseFormat();
        this.stop = query.getStop();
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

    public Integer getN() {
        return n;
    }

    public Integer getSeed() {
        return seed;
    }

    public ResponseFormat getResponseFormat() {
        return responseFormat;
    }

    public String[] getStop() {
        return stop;
    }
}
