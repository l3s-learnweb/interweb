package de.l3s.interweb.connector.youtube.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.l3s.interweb.connector.youtube.adapters.ItemIdAdapter;

public record ListItem(

    @JsonProperty("snippet")
    Snippet snippet,

    @JsonProperty("kind")
    String kind,

    @JsonProperty("etag")
    String etag,

    @JsonDeserialize(using = ItemIdAdapter.class)
    @JsonProperty("id")
    String id,

    @JsonProperty("contentDetails")
    ContentDetails contentDetails,

    @JsonProperty("statistics")
    Statistics statistics
) {
}