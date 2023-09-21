package de.l3s.interweb.connector.ipernity.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SearchResponse(
    @JsonProperty("docs")
    Docs docs,

    @JsonProperty("api")
    Api api
) {
}