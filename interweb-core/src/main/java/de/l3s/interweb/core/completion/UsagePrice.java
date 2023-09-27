package de.l3s.interweb.core.completion;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class UsagePrice {
    private double prompt;
    private double completion;

    public UsagePrice(double prompt, double completion) {
        this.prompt = prompt;
        this.completion = completion;
    }

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
}
