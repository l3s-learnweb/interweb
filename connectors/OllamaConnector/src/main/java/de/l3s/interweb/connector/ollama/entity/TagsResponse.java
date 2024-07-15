package de.l3s.interweb.connector.ollama.entity;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class TagsResponse {
    private List<OllamaTag> models;

    public List<OllamaTag> getModels() {
        return models;
    }

    public void setModels(List<OllamaTag> models) {
        this.models = models;
    }
}
