package de.l3s.interweb.connector.ipernity.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetResponse(

    @JsonProperty("doc")
    Doc doc,

    @JsonProperty("api")
    Api api
) {
}