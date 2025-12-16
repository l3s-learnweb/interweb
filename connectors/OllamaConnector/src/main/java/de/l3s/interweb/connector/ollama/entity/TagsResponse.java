package de.l3s.interweb.connector.ollama.entity;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class TagsResponse {
    private List<Tag> models;

    public List<Tag> getModels() {
        return models;
    }

    public void setModels(List<Tag> models) {
        this.models = models;
    }
}
