package de.l3s.interweb.connector.ollama.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.completion.CompletionQuery;


public class CompletionOptions {
    private Integer seed;
    private String stop;
    @JsonProperty("num_predict")
    private Integer numPredict;
    private Double temperature;
    @JsonProperty("top_p")
    private Double topP;

    public CompletionOptions() {
    }

    public CompletionOptions(CompletionQuery query) {
        this.seed = query.getSeed();
        if (query.getStop() != null && query.getStop().length > 0) {
            this.stop = query.getStop()[0];
        }
        this.numPredict = query.getMaxTokens();
        this.temperature = query.getTemperature();
        this.topP = query.getTopP();
    }

    public Integer getSeed() {
        return seed;
    }

    public void setSeed(Integer seed) {
        this.seed = seed;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public Integer getNumPredict() {
        return numPredict;
    }

    public void setNumPredict(Integer numPredict) {
        this.numPredict = numPredict;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getTopP() {
        return topP;
    }

    public void setTopP(Double topP) {
        this.topP = topP;
    }
}
