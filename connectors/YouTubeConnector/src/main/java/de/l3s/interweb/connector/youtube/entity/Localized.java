package de.l3s.interweb.connector.youtube.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Localized(

    @JsonProperty("description")
    String description,

    @JsonProperty("title")
    String title
) {
}