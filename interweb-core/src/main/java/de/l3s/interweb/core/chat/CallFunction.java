package de.l3s.interweb.core.chat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

import jakarta.validation.constraints.NotEmpty;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class CallFunction implements Serializable {
    @Serial
    private static final long serialVersionUID = -2780720621585498099L;

    /**
     * The name of the function to call.
     */
    @NotEmpty
    private String name;

    /**
     * The arguments to call the function with, as generated by the model in JSON format. Note that the model does not always generate valid JSON,
     * and may hallucinate parameters not defined by your function schema. Validate the arguments in your code before calling your function.
     */
    private Map<String, String> arguments;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setArguments(Map<String, String> arguments) {
        this.arguments = arguments;
    }

    public Map<String, String> getArguments() {
        return arguments;
    }
}
