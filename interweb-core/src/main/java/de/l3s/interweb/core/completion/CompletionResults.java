package de.l3s.interweb.core.completion;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.Results;

public class CompletionResults extends Results<Choice> {
    @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
    private UUID chatId;
    private String model;
    private Usage usage;
    private UsageCost cost;
    private Instant created;

    public UUID getChatId() {
        return chatId;
    }

    public void setChatId(UUID chatId) {
        this.chatId = chatId;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    @JsonIgnore
    public List<Choice> getResults() {
        return super.getResults();
    }

    public List<Choice> getChoices() {
        return getResults();
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

    public void updateCosts(UsagePrice price) {
        double promptCost = (usage.getPromptTokens() / 1000d) * price.getPrompt();
        double completionCost = (usage.getCompletionTokens() / 1000d) * price.getCompletion();

        cost = new UsageCost();
        cost.setResponse(promptCost + completionCost);
    }
}
