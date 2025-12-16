package de.l3s.interweb.core.responses;

import java.util.Map;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public class ResponseOutputFileSearchResult {
    @JsonProperty("attributes")
    private Map<String, Object> attributes;

    @JsonProperty("file_id")
    private String fileId;

    @JsonProperty("filename")
    private String filename;

    @JsonProperty("score")
    private Double score;

    @JsonProperty("text")
    private String text;

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

