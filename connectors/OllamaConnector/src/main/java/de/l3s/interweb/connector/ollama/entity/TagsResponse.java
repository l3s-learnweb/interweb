package de.l3s.interweb.connector.ollama.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@RegisterForReflection
public class TagsResponse {
    private List<TagsModel> models;

    public List<TagsModel> getModels() {
        return models;
    }

    public void setModels(List<TagsModel> models) {
        this.models = models;
    }
}
