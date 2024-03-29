package de.l3s.interweb.connector.bing.entity;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public abstract class BaseHolder<T> {

    @JsonProperty("id")
    String id;
    @JsonProperty("readLink")
    String readLink;
    @JsonProperty("webSearchUrl")
    String webSearchUrl;
    @JsonProperty("totalEstimatedMatches")
    Long totalEstimatedMatches;
    @JsonProperty("currentOffset")
    Integer currentOffset;
    @JsonProperty("nextOffset")
    Integer nextOffset;
    @JsonProperty("value")
    List<T> values;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReadLink() {
        return readLink;
    }

    public void setReadLink(String readLink) {
        this.readLink = readLink;
    }

    public String getWebSearchUrl() {
        return webSearchUrl;
    }

    public void setWebSearchUrl(String webSearchUrl) {
        this.webSearchUrl = webSearchUrl;
    }

    public Long getTotalEstimatedMatches() {
        return totalEstimatedMatches;
    }

    public void setTotalEstimatedMatches(final Long totalEstimatedMatches) {
        this.totalEstimatedMatches = totalEstimatedMatches;
    }

    public Integer getCurrentOffset() {
        return currentOffset;
    }

    public void setCurrentOffset(final Integer currentOffset) {
        this.currentOffset = currentOffset;
    }

    public Integer getNextOffset() {
        return nextOffset;
    }

    public void setNextOffset(final Integer nextOffset) {
        this.nextOffset = nextOffset;
    }

    public List<T> getValues() {
        return values;
    }

    public void setValues(List<T> values) {
        this.values = values;
    }
}
