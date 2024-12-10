package de.l3s.interweb.core.chat;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public class UsageCost {
    private double prompt;
    private double completion;
    @JsonInclude(JsonInclude.Include.ALWAYS)
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

    public void add(UsageCost other) {
        this.prompt += other.prompt;
        this.completion += other.completion;
        this.total += other.total;
        this.chatTotal += other.chatTotal;
    }
}
