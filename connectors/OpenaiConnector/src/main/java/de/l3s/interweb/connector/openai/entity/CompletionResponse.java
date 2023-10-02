package de.l3s.interweb.connector.openai.entity;

import java.time.Instant;
import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import de.l3s.interweb.core.completion.Choice;
import de.l3s.interweb.core.completion.Usage;

@RegisterForReflection
public class CompletionResponse {
    private String id;
    private String object;
    private String model;
    private Usage usage;
    private Instant created;
    private List<Choice> choices;

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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }
}
