package de.l3s.interweb.connector.ipernity.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public record GetResponse(

    @JsonProperty("doc")
    Doc doc,

    @JsonProperty("api")
    Api api
) {
}