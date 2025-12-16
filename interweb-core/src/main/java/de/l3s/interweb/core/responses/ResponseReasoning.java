package de.l3s.interweb.core.responses;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.chat.ReasoningEffort;

@RegisterForReflection
public class ResponseReasoning {
    @JsonProperty("effort")
    private ReasoningEffort effort;

    @JsonProperty("generate_summary")
    private String generateSummary;

    @JsonProperty("summary")
    private String summary;

    public ReasoningEffort getEffort() {
        return effort;
    }

    public void setEffort(ReasoningEffort effort) {
        this.effort = effort;
    }

    public String getGenerateSummary() {
        return generateSummary;
    }

    public void setGenerateSummary(String generateSummary) {
        this.generateSummary = generateSummary;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}

