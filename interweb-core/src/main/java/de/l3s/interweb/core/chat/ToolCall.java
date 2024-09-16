package de.l3s.interweb.core.chat;

import java.io.Serial;
import java.io.Serializable;

import jakarta.validation.constraints.NotEmpty;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ToolCall implements Serializable {
    @Serial
    private static final long serialVersionUID = -1359851236515659714L;

    /**
     * The id of the tool.
     */
    @NotEmpty
    private String id;
    /**
     * The type of the tool. Currently, only function is supported.
     */
    @NotEmpty
    private String type;
    /**
     * The function that the model called.
     */
    @NotEmpty
    @JsonProperty("function")
    private CallFunction function;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setFunction(CallFunction function) {
        this.function = function;
    }

    public CallFunction getFunction() {
        return function;
    }
}
