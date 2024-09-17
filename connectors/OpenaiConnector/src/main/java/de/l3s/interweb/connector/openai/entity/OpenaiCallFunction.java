package de.l3s.interweb.connector.openai.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.chat.CallFunction;
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
}
