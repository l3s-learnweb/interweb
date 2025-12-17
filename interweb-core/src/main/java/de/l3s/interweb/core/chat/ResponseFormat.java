package de.l3s.interweb.core.chat;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ResponseFormat {

    public enum ResponseType {
        json_object,
        json_schema,
        text
    }

    /**
     * The type of response format being defined.
     */
    private ResponseType type;

    /**
     * Structured Outputs configuration options, including a JSON Schema.
     * We keep it as object, we don't want to care about its internal structure here.
     */
    @JsonProperty("json_schema")
    private Object jsonSchema;

    public ResponseFormat() {
    }

    public ResponseFormat(ResponseType type) {
        this.type = type;
    }

    public ResponseType getType() {
        return type;
    }

    public void setType(ResponseType type) {
        this.type = type;
    }

    public Object getJsonSchema() {
        return jsonSchema;
    }

    public void setJsonSchema(Object jsonSchema) {
        this.jsonSchema = jsonSchema;
    }
}
