package de.l3s.interweb.connector.openai.entity;

import java.time.Instant;
import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.chat.*;

@RegisterForReflection
public class CompletionsResponse {
    private String id;
    private String object;
    private String model;
    private Usage usage;
    @JsonProperty("system_fingerprint")
    private String systemFingerprint;
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

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    public String getSystemFingerprint() {
        return systemFingerprint;
    }

    public void setSystemFingerprint(String systemFingerprint) {
        this.systemFingerprint = systemFingerprint;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public CompletionsResults toCompletionResults() {
        CompletionsResults results = new CompletionsResults();
        results.setModel(model);
        results.setCreated(created);
        results.setChoices(choices);
        results.setUsage(usage);
        results.setObject(object);
        results.setSystemFingerprint(systemFingerprint);
        return results;
    }
}
