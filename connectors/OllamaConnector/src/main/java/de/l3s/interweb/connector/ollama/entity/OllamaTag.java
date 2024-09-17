package de.l3s.interweb.connector.ollama.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

@RegisterForReflection
public class OllamaTag {
    private String name;
    private String model;
    private long size;
    private String digest;
    private OllamaTagDetails details;
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

    public OllamaTagDetails getDetails() {
        return details;
    }

    public void setDetails(OllamaTagDetails details) {
        this.details = details;
    }

    public Instant getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Instant modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
