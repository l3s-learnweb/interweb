package de.l3s.interweb.core.chat;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Property {

    @NotEmpty
    private String type;
    private String description;
    @JsonProperty("enum")
    private List<String> enumValues;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setEnumValues(List<String> enumValues) {
        this.enumValues = enumValues;
    }

    public List<String> getEnumValues() {
        return enumValues;
    }
}
