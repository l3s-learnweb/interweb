package de.l3s.interweb.core.chat;

import java.util.List;
import java.util.Map;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Parameters {

    private String type = "object";
    private Map<String, Property> properties;
    private List<String> required;
    @JsonProperty("additionalProperties")
    private Boolean additionalProperties;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setProperties(Map<String, Property> properties) {
        this.properties = properties;
    }

    public Map<String, Property> getProperties() {
        return properties;
    }

    public void setRequired(List<String> required) {
        this.required = required;
    }

    public List<String> getRequired() {
        return required;
    }

    public void setAdditionalProperties(Boolean additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public Boolean getAdditionalProperties() {
        return additionalProperties;
    }
}
