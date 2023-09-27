package de.l3s.interweb.connector.ipernity.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public record Doc(
    @JsonProperty("doc_id")
    String docId,

    @JsonProperty(value = "owner", required = true)
    Owner owner,

    @JsonProperty("license")
    String license,

    @JsonProperty("thumb")
    Thumb thumb,

    @JsonProperty("thumbs")
    Thumbs thumbs,

    @JsonProperty("rotation")
    String rotation,

    @JsonProperty("link")
    String link,

    @JsonProperty("count")
    Count count,

    @JsonProperty("description")
    String description,

    @JsonProperty("dates")
    Dates dates,

    @JsonProperty("media")
    String media,

    @JsonProperty("title")
    String title,

    @JsonProperty("tags")
    Tags tags
) {
}