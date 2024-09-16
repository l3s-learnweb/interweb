package de.l3s.interweb.core.chat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Function {

    /**
     * The name of the function to be called. Must be a-z, A-Z, 0-9, or contain underscores and dashes, with a maximum length of 64.
     */
    @NotEmpty
    @Size(max = 64)
    @JsonProperty("name")
    private String name;

    /**
     * A description of what the function does, used by the model to choose when and how to call the function.
     */
    @JsonProperty("description")
    private String description;

    /**
     * The parameters the function accepts, described as a JSON Schema object.
     * Omitting parameters defines a function with an empty parameter list.
     * https://platform.openai.com/docs/guides/function-calling
     */
    @JsonProperty("parameters")
    private Parameters parameters;

    /**
     * Whether to enable strict schema adherence when generating the function call.
     * If set to true, the model will follow the exact schema defined in the `parameters` field.
     * Only a subset of JSON Schema is supported when strict is true. Learn more about Structured Outputs in the function calling guide.
     * https://platform.openai.com/docs/api-reference/chat/docs/guides/function-calling
     */
    @JsonProperty("strict")
    private Boolean strict;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public void setStrict(Boolean strict) {
        this.strict = strict;
    }

    public Boolean getStrict() {
        return strict;
    }
}
