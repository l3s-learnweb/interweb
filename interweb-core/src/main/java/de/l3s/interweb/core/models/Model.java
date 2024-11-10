package de.l3s.interweb.core.models;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"id", "object", "owned_by", "provided_by", "family", "parameter_size", "quantization_level", "created"})
public class Model {
    /**
     * The model identifier, which can be referenced in the API endpoints.
     */
    private String id;
    /**
     * The object type, which is always "model".
     */
    private String object = "model";
    /**
     * The organization that owns the model.
     */
    @JsonProperty("owned_by")
    private String ownedBy;
    @JsonProperty("provided_by")
    private String providedBy;
    @JsonProperty("family")
    private String family;
    @JsonProperty("parameter_size")
    private String parameterSize;
    @JsonProperty("quantization_level")
    private String quantizationLevel;
    /**
     * The price of the model per 1k tokens in USD.
     */
    private UsagePrice price;
    /**
     * The model provider, e.g. OpenAI, Anthropic or Ollama.
     */
    @JsonIgnore
    private String provider;
    /**
     * The Unix timestamp (in seconds) when the model was created.
     */
    private Instant created;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getOwnedBy() {
        return ownedBy;
    }

    public void setOwnedBy(String ownedBy) {
        this.ownedBy = ownedBy;
    }

    public String getProvidedBy() {
        return providedBy;
    }

    public void setProvidedBy(String providedBy) {
        this.providedBy = providedBy;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getParameterSize() {
        return parameterSize;
    }

    public void setParameterSize(String parameterSize) {
        this.parameterSize = parameterSize;
    }

    public String getQuantizationLevel() {
        return quantizationLevel;
    }

    public void setQuantizationLevel(String quantizationLevel) {
        this.quantizationLevel = quantizationLevel;
    }

    public UsagePrice getPrice() {
        return price;
    }

    public void setPrice(UsagePrice price) {
        this.price = price;
    }

    public boolean isFree() {
        return UsagePrice.FREE.equals(price);
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public static Model of(String id, String ownedBy, UsagePrice price, LocalDate created) {
        Model model = new Model();
        model.setId(id);
        model.setOwnedBy(ownedBy);
        model.setProvidedBy(ownedBy);
        model.setPrice(price);
        if (created != null) {
            model.setCreated(created.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        return model;
    }
}
