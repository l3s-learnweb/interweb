package de.l3s.interweb.connector.ipernity.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Count(
    @JsonProperty("visits")
    Long visits,

    @JsonProperty("faves")
    Long faves,

    @JsonProperty("comments")
    Long comments,

    @JsonProperty("notes")
    Long notes
) {
}