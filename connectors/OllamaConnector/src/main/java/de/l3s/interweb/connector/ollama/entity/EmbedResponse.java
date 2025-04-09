package de.l3s.interweb.connector.ollama.entity;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.chat.Usage;
import de.l3s.interweb.core.embeddings.Embedding;
import de.l3s.interweb.core.embeddings.EmbeddingsResults;

@RegisterForReflection
public class EmbedResponse {
    private String model;
    private List<List<Double>> embeddings;
    @JsonProperty("total_duration")
    private Long totalDuration;
    @JsonProperty("load_duration")
    private Long loadDuration;
    @JsonProperty("prompt_eval_count")
    private Integer promptEvalCount;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<List<Double>> getEmbeddings() {
        return embeddings;
    }

    public void setEmbeddings(List<List<Double>> embeddings) {
        this.embeddings = embeddings;
    }

    public Long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(Long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public Long getLoadDuration() {
        return loadDuration;
    }

    public void setLoadDuration(Long loadDuration) {
        this.loadDuration = loadDuration;
    }

    public Integer getPromptEvalCount() {
        return promptEvalCount;
    }

    public void setPromptEvalCount(Integer promptEvalCount) {
        this.promptEvalCount = promptEvalCount;
    }

    public EmbeddingsResults toCompletionResults() {
        EmbeddingsResults results = new EmbeddingsResults();
        results.setModel(model);
        results.setElapsedTime(totalDuration);

        for (int i = 0; i < embeddings.size(); i++) {
            Embedding embedding = new Embedding();
            embedding.setEmbedding(embeddings.get(i));
            embedding.setIndex(i);
            results.addData(embedding);
        }

        Usage usage = new Usage();
        if (promptEvalCount != null) {
            usage.setTotalTokens(promptEvalCount);
        }
        results.setUsage(usage);
        return results;
    }
}
