package de.l3s.interweb.connector.ipernity.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IpernityResponse(
    @JsonProperty("docs")
    Docs docs,

    @JsonProperty("api")
    Api api
) {
}