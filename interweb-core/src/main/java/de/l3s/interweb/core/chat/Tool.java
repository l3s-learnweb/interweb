package de.l3s.interweb.core.chat;

import jakarta.validation.constraints.NotEmpty;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonInclude;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tool {

    /**
     * The type of the tool. Currently, only `function` is supported.
     */
    @NotEmpty
    private String type;

    @NotEmpty
    private Function function;

    public Tool() {
    }

    public Tool(Function function) {
        this.type = "function";
        this.function = function;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public Function getFunction() {
        return function;
    }

    public static FunctionToolBuilder functionBuilder() {
        return new FunctionToolBuilder();
    }
}
