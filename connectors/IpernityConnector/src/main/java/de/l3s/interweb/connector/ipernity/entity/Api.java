package de.l3s.interweb.connector.ipernity.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Api(
    @JsonProperty("at")
    String at,

    @JsonProperty("status")
    String status
) {
}