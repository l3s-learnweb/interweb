package de.l3s.interweb.connector.ipernity.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public record Count(
    @JsonProperty("visits")
    Long visits,

    @JsonProperty("faves")
    Long faves,

    @JsonProperty("comments")
    Long comments,

    @JsonProperty("notes")
    Long notes,

    @JsonProperty("groups")
    Long groups,

    @JsonProperty("tags")
    Long tags,

    @JsonProperty("albums")
    Long albums
) {
}