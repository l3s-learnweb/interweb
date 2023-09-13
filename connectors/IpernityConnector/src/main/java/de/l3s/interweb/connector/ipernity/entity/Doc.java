package de.l3s.interweb.connector.ipernity.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Doc(
    @JsonProperty("doc_id")
    String docId,

    @JsonProperty(value = "owner", required = true)
    Owner owner,

    @JsonProperty("license")
    String license,

    @JsonProperty(value = "thumb", required = true)
    Thumb thumb,

    @JsonProperty("rotation")
    String rotation,

    @JsonProperty("count")
    Count count,

    @JsonProperty("dates")
    Dates dates,

    @JsonProperty("media")
    String media,

    @JsonProperty("title")
    String title
) {
}