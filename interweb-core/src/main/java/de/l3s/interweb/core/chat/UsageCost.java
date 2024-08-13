package de.l3s.interweb.core.chat;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.models.UsagePrice;

@RegisterForReflection
public class UsageCost {
    private double prompt;
    private double completion;
    private double total;
    @JsonProperty("chat_total")
    private double chatTotal;

    public double getPrompt() {
        return prompt;
    }

    public void setPrompt(double prompt) {
        this.prompt = prompt;
    }

    public double getCompletion() {
        return completion;
    }

    public void setCompletion(double completion) {
        this.completion = completion;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getChatTotal() {
        return chatTotal;
    }

    public void setChatTotal(double chatTotal) {
        this.chatTotal = chatTotal;
    }

    public static UsageCost of(Usage usage, UsagePrice price) {
        UsageCost cost = new UsageCost();
        cost.prompt = (usage.getPromptTokens() / 1000d) * price.getInput();
        cost.completion = (usage.getCompletionTokens() / 1000d) * price.getOutput();
        cost.total = cost.prompt + cost.completion;
        return cost;
    }
}
