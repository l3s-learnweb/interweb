package de.l3s.interweb.core.models;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonInclude;

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
}
