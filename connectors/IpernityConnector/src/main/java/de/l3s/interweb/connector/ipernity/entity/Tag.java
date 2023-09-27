package de.l3s.interweb.connector.ipernity.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public record Tag(

    @JsonProperty("added_at")
    String addedAt,

    @JsonProperty("user_id")
    String userId,

    @JsonProperty("id")
    String id,

    @JsonProperty("tags")
    String tag,

    @JsonProperty("type")
    String type
) {
}