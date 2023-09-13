package de.l3s.interweb.connector.youtube.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.search.Thumbnail;

public record VideoThumbnail(
    @JsonProperty("width")
    Integer width,

    @JsonProperty("url")
    String url,

    @JsonProperty("height")
    Integer height
) {
    public Thumbnail toThumbnail() {
        return new Thumbnail(url, width, height);
    }
}