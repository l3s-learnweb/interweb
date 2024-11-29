package de.l3s.interweb.core.embeddings;

import java.util.ArrayList;
import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import de.l3s.interweb.core.chat.Usage;
import de.l3s.interweb.core.chat.UsageCost;

@RegisterForReflection
@JsonPropertyOrder({"object", "model", "data", "usage", "estimated_cost", "elapsed_time"})
public class EmbeddingsResults {
    private final String object = "list";
    private String model;
    private Usage usage;
    private List<Embedding> data;
    @JsonProperty(value = "estimated_cost")
    private UsageCost cost;
    @JsonProperty("elapsed_time")
    private Long elapsedTime;

    public String getObject() {
        return this.object;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Embedding> getData() {
        return data;
    }

    public void setData(List<Embedding> data) {
        this.data = data;
    }

    public void addData(Embedding embedding) {
        if (this.data == null) {
            this.data = new ArrayList<>();
        }

        this.data.add(embedding);
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    public UsageCost getCost() {
        return cost;
    }

    public void setCost(UsageCost cost) {
        this.cost = cost;
    }

    public Long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(Long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
}
