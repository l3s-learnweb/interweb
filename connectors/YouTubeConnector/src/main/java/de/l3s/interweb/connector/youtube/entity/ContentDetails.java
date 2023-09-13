package de.l3s.interweb.connector.youtube.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ContentDetails(

    @JsonProperty("duration")
    String duration,

    @JsonProperty("licensedContent")
    Boolean licensedContent,

    @JsonProperty("caption")
    String caption,

    @JsonProperty("definition")
    String definition,

    @JsonProperty("projection")
    String projection,

    @JsonProperty("dimension")
    String dimension
) {
}