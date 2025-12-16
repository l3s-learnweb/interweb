package de.l3s.interweb.connector.ollama.entity;

import java.time.Instant;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.models.Model;
import de.l3s.interweb.core.models.UsagePrice;

@RegisterForReflection
public class Tag {
    private String name;
    private String model;
    private long size;
    private String digest;
    private TagDetails details;
    @JsonProperty("modified_at")
    private Instant modifiedAt;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public TagDetails getDetails() {
        return details;
    }

    public void setDetails(TagDetails details) {
        this.details = details;
    }

    public Instant getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Instant modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public Model toModel() {
        Model model = new Model();
        model.setId(this.getName());
        model.setProvidedBy("l3s");
        model.setPrice(UsagePrice.FREE);
        model.setFamily(this.getDetails().getFamily());
        model.setParameterSize(this.getDetails().getParameterSize());
        model.setQuantizationLevel(this.getDetails().getQuantizationLevel());
        model.setCreated(this.getModifiedAt());
        return model;
    }
}
