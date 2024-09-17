package de.l3s.interweb.connector.openai.entity;

import de.l3s.interweb.core.chat.CallFunction;
import de.l3s.interweb.core.util.JsonUtils;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serial;
import java.io.Serializable;

@RegisterForReflection
public class OpenaiCallFunction implements Serializable {
    @Serial
    private static final long serialVersionUID = -2780720621585498099L;

    private String name;
    private String arguments;

    public OpenaiCallFunction() {
    }

    public OpenaiCallFunction(CallFunction function) {
        this.name = function.getName();
        this.arguments = JsonUtils.toJson(function.getArguments());
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    public String getArguments() {
        return arguments;
    }
}
