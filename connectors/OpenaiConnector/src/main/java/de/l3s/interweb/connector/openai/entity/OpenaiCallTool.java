package de.l3s.interweb.connector.openai.entity;

import java.io.Serial;
import java.io.Serializable;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.chat.CallTool;

@RegisterForReflection
public class OpenaiCallTool implements Serializable {
    @Serial
    private static final long serialVersionUID = -1359851236515659714L;

    private String id;
    private String type;
    @JsonProperty("function")
    private OpenaiCallFunction function;

    public OpenaiCallTool() {
    }

    public OpenaiCallTool(CallTool tool) {
        this.id = tool.getId();
        this.type = tool.getType();
        this.function = new OpenaiCallFunction(tool.getFunction());
    }

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

    public void setFunction(OpenaiCallFunction function) {
        this.function = function;
    }

    public OpenaiCallFunction getFunction() {
        return function;
    }
}
