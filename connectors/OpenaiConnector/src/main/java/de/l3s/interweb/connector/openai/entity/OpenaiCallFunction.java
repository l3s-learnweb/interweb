package de.l3s.interweb.connector.openai.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.chat.CallFunction;

@RegisterForReflection
public class OpenaiCallFunction implements Serializable {
    @Serial
    private static final long serialVersionUID = -2780720621585498099L;

    private String name;
    private String arguments;

    public OpenaiCallFunction() {
    }

    public OpenaiCallFunction(CallFunction function) {
        try {
            this.name = function.getName();
            this.arguments = new ObjectMapper().writeValueAsString(function.getArguments());
        } catch (JsonProcessingException e) {
            throw new ConnectorException("Error while converting arguments to JSON", e);
        }
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

    public CallFunction toCallFunction() {
        try {
            CallFunction function = new CallFunction();
            function.setName(name);

            if (arguments != null && !arguments.isBlank()) {
                TypeReference<HashMap<String, String>> typeRef = new TypeReference<>() {};
                function.setArguments(new ObjectMapper().readValue(arguments, typeRef));
            }

            return function;
        } catch (JsonProcessingException e) {
            throw new ConnectorException("Failed to parse function arguments", e);
        }
    }
}
