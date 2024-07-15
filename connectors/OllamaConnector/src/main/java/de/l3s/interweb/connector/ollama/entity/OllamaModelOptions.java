package de.l3s.interweb.connector.ollama.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.completion.CompletionQuery;

public class OllamaModelOptions {
    @JsonProperty("repeat_penalty")
    private Double repeatPenalty;
    private Double temperature;
    private Integer seed;
    private String stop;
    @JsonProperty("num_predict")
    private Integer numPredict;
    @JsonProperty("top_p")
    private Double topP;
    @JsonProperty("top_k")
    private Integer topK;

    public OllamaModelOptions(CompletionQuery query) {
        this.seed = query.getSeed();
        if (query.getStop() != null && query.getStop().length > 0) {
            this.stop = query.getStop()[0];
        }
        this.repeatPenalty = query.getFrequencyPenalty();
        this.numPredict = query.getMaxTokens();
        this.temperature = query.getTemperature();
        this.topP = query.getTopP();
    }

    public Double getRepeatPenalty() {
        return repeatPenalty;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Integer getSeed() {
        return seed;
    }

    public String getStop() {
        return stop;
    }

    public Integer getNumPredict() {
        return numPredict;
    }

    public Double getTopP() {
        return topP;
    }

    public Integer getTopK() {
        return topK;
    }
}
