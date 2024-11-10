package de.l3s.interweb.core.models;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonInclude;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsagePrice {
    public static final UsagePrice FREE = new UsagePrice(0, 0);

    private double input;
    private double output;

    public UsagePrice(double input, double output) {
        this.input = input;
        this.output = output;
    }

    public double getInput() {
        return input;
    }

    public void setInput(double input) {
        this.input = input;
    }

    public double getOutput() {
        return output;
    }

    public void setOutput(double output) {
        this.output = output;
    }
}
