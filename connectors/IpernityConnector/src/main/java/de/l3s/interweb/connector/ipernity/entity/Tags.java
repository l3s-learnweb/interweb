package de.l3s.interweb.connector.ipernity.entity;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public record Tags(

    @JsonProperty("total")
    String total,

    @JsonProperty("tags")
    List<Tag> tags
) {
}