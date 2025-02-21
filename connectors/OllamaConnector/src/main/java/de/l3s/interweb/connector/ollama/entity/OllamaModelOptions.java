package de.l3s.interweb.connector.ollama.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.chat.CompletionsQuery;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OllamaModelOptions {
    @JsonProperty("num_ctx")
    private Integer numCtx;
    @JsonProperty("repeat_penalty")
    private Double repeatPenalty;
    private Double temperature;
    private Integer seed;
    private String stop;
    @JsonProperty("num_predict")
    private Integer numPredict;
    @JsonProperty("top_p")
    private Double topP;
    @JsonProperty("min_p")
    private Double minP;
    @JsonProperty("top_k")
    private Integer topK;

    public OllamaModelOptions(CompletionsQuery query) {
        this.seed = query.getSeed();
        if (query.getStop() != null && query.getStop().length > 0) {
            this.stop = query.getStop()[0];
        }
        this.repeatPenalty = query.getFrequencyPenalty();
        this.numPredict = query.getMaxTokens();
        this.numCtx = query.getNumCtx();
        this.temperature = query.getTemperature();
        this.topP = query.getTopP();
        this.minP = query.getMinP();
        this.topK = query.getTopK();
    }

    public Integer getNumCtx() {
        return numCtx;
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

    public Double getMinP() {
        return minP;
    }

    public Integer getTopK() {
        return topK;
    }
}
