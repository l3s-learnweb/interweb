package de.l3s.interweb.connector.ipernity.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public record SearchResponse(
    @JsonProperty("docs")
    Docs docs,

    @JsonProperty("api")
    Api api
) {
}