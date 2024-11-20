package de.l3s.interweb.core.models;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.l3s.interweb.core.chat.Usage;
import de.l3s.interweb.core.chat.UsageCost;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsagePrice {
    public static final double TOKENS_PER_PRICE = 1_000_000d;
    public static final UsagePrice FREE = new UsagePrice(0, 0);

    private final double input;
    private final double output;

    public UsagePrice(double input, double output) {
        this.input = input;
        this.output = output;
    }

    public double getInput() {
        return input;
    }

    public double getOutput() {
        return output;
    }

    public UsageCost calc(Usage usage) {
        UsageCost cost = new UsageCost();
        cost.setPrompt(usage.getPromptTokens() * (input / TOKENS_PER_PRICE));
        cost.setCompletion(usage.getCompletionTokens() * (output / TOKENS_PER_PRICE));
        cost.setTotal(cost.getPrompt() + cost.getCompletion());
        return cost;
    }
}
