package de.l3s.interweb.connector.youtube.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Thumbnails(

    @JsonProperty("default")
    VideoThumbnail def,

    @JsonProperty("medium")
    VideoThumbnail medium,

    @JsonProperty("high")
    VideoThumbnail high,

    @JsonProperty("standard")
    VideoThumbnail standard,

    @JsonProperty("maxres")
    VideoThumbnail maxres
) {
}