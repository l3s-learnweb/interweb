package de.l3s.interweb.core.completion;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import de.l3s.interweb.core.Results;

@RegisterForReflection
@JsonIgnoreProperties("results")
@JsonPropertyOrder({"id", "object", "title", "model", "choices", "usage", "cost", "elapsed_time", "system_fingerprint", "created"})
public class CompletionResults extends Results<Choice> {
    @JsonProperty(value = "id")
    private UUID chatId;
    @JsonProperty(value = "title")
    private String chatTitle;
    private String object;
    private String model;
    private Usage usage;
    private UsageCost cost;
    @JsonProperty(value = "system_fingerprint")
    private String systemFingerprint;
    private Instant created;

    public UUID getChatId() {
        return chatId;
    }

    public void setChatId(UUID chatId) {
        this.chatId = chatId;
    }

    public String getChatTitle() {
        return chatTitle;
    }

    public void setChatTitle(String chatTitle) {
        this.chatTitle = chatTitle;
    }

    public String getObject() {
        return this.object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @JsonProperty
    public List<Choice> getChoices() {
        return getResults();
    }

    @JsonIgnore
    public Message getLastMessage() {
        if (getResults().isEmpty()) {
            return null;
        }

        return getResults().get(0).getMessage();
    }

    @JsonProperty
    public void setChoices(List<Choice> choices) {
        super.add(choices);
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

    public String getSystemFingerprint() {
        return systemFingerprint;
    }

    public void setSystemFingerprint(String systemFingerprint) {
        this.systemFingerprint = systemFingerprint;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public void updateCosts(UsagePrice price) {
        double promptCost = (usage.getPromptTokens() / 1000d) * price.getPrompt();
        double completionCost = (usage.getCompletionTokens() / 1000d) * price.getCompletion();

        cost = new UsageCost();
        cost.setResponse(promptCost + completionCost);
    }
}
