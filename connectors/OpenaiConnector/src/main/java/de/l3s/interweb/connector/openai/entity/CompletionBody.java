package de.l3s.interweb.connector.openai.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;

import de.l3s.interweb.core.completion.CompletionQuery;
import de.l3s.interweb.core.completion.ResponseFormat;

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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer n;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer seed;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("response_format")
    private ResponseFormat responseFormat;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String[] stop;

    public CompletionBody(CompletionQuery query) {
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
