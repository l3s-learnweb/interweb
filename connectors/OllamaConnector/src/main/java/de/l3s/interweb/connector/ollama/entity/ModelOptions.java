package de.l3s.interweb.connector.ollama.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.chat.CompletionsQuery;
import de.l3s.interweb.core.embeddings.EmbeddingsQuery;
import de.l3s.interweb.core.responses.ResponsesQuery;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModelOptions {
    @JsonProperty("num_ctx")
    private Integer numCtx;
    @JsonProperty("num_predict")
    private Integer numPredict;
    @JsonProperty("num_keep")
    private Integer numKeep;
    @JsonProperty("repeat_last_n")
    private Double repeatLastN;
    // @JsonProperty("num_batch")
    // private Integer numBatch;
    // @JsonProperty("num_gpu")
    // private Integer numGpu;
    // @JsonProperty("main_gpu")
    // private Integer mainGpu;
    // @JsonProperty("use_mmap")
    // private Boolean useMmap;
    // @JsonProperty("num_thread")
    // private Integer numThread;
    // private Boolean numa;
    @JsonProperty("repeat_penalty")
    private Double repeatPenalty;
    @JsonProperty("presence_penalty")
    private Double presencePenalty;
    @JsonProperty("frequency_penalty")
    private Double frequencyPenalty;
    @JsonProperty("penalize_newline")
    private Boolean penalizeNewline;
    private Double temperature;
    private Integer seed;
    private String[] stop;
    @JsonProperty("top_p")
    private Double topP;
    @JsonProperty("min_p")
    private Double minP;
    @JsonProperty("typical_p")
    private Double typicalP;
    @JsonProperty("top_k")
    private Integer topK;

    public Integer getNumCtx() {
        return numCtx;
    }

    public Integer getNumPredict() {
        return numPredict;
    }

    public Integer getNumKeep() {
        return numKeep;
    }

    public Double getRepeatLastN() {
        return repeatLastN;
    }

    public Double getRepeatPenalty() {
        return repeatPenalty;
    }

    public Double getPresencePenalty() {
        return presencePenalty;
    }

    public Double getFrequencyPenalty() {
        return frequencyPenalty;
    }

    public Boolean getPenalizeNewline() {
        return penalizeNewline;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Integer getSeed() {
        return seed;
    }

    public String[] getStop() {
        return stop;
    }

    public Double getTopP() {
        return topP;
    }

    public Double getMinP() {
        return minP;
    }

    public Double getTypicalP() {
        return typicalP;
    }

    public Integer getTopK() {
        return topK;
    }

    public static ModelOptions of(CompletionsQuery query) {
        ModelOptions options = new ModelOptions();
        options.seed = query.getSeed();
        if (query.getStop() != null && query.getStop().length > 0) {
            options.stop = query.getStop();
        }
        options.presencePenalty = query.getPresencePenalty();
        options.frequencyPenalty = query.getFrequencyPenalty();
        if (options.numCtx == null && query.getMaxTokens() != null && query.getMaxTokens() >= 2048) {
            options.numCtx = query.getMaxTokens();
        } else {
            options.numCtx = query.getNumCtx();
        }
        options.temperature = query.getTemperature();
        options.topP = query.getTopP();
        options.minP = query.getMinP();
        options.topK = query.getTopK();
        return options;
    }

    public static ModelOptions of(EmbeddingsQuery query) {
        return new ModelOptions();
    }

    public static ModelOptions of(ResponsesQuery query) {
        ModelOptions options = new ModelOptions();
        options.temperature = query.getTemperature();
        options.topP = query.getTopP();
        options.numPredict = query.getMaxOutputTokens();
        return options;
    }
}
