package de.l3s.interweb.connector.ollama.entity;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.l3s.interweb.core.embeddings.EmbeddingsQuery;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmbedBody {
    private String model;
    private List<String> input;
    private Boolean truncate;

    public EmbedBody(EmbeddingsQuery query) {
        this.model = query.getModel();
        this.input = query.getInput();
        this.truncate = query.getTruncate();
    }

    public String getModel() {
        return model;
    }

    public List<String> getInput() {
        return input;
    }

    public Boolean getTruncate() {
        return truncate;
    }
}
