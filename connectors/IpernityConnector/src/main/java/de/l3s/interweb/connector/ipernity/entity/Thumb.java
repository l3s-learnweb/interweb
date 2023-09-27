package de.l3s.interweb.connector.ipernity.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public record Thumb(
    @JsonProperty("ext")
    String ext,

    @JsonProperty("path")
    String path,

    @JsonProperty("icon")
    String icon,

    @JsonProperty("w")
    Integer width,

    @JsonProperty("h")
    Integer height,

    @JsonProperty("farm")
    String farm,

    @JsonProperty("label")
    String label,

    @JsonProperty("secret")
    String secret,

    @JsonProperty("url")
    String url
) {
}