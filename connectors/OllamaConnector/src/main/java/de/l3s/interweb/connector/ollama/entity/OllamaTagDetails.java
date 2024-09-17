package de.l3s.interweb.connector.ollama.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@RegisterForReflection
public class OllamaTagDetails {
    private String format;
    private String family;
    private List<String> families;
    @JsonProperty("parameter_size")
    private String parameterSize;
    @JsonProperty("quantization_level")
    private String quantizationLevel;

    public String getFormat() {
        return format;
    }

    public String getFamily() {
        return family;
    }

    public List<String> getFamilies() {
        return families;
    }

    public String getParameterSize() {
        return parameterSize;
    }

    public String getQuantizationLevel() {
        return quantizationLevel;
    }
}
