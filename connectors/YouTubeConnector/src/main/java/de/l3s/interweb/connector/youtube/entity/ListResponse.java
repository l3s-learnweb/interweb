package de.l3s.interweb.connector.youtube.entity;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ListResponse(

    @JsonProperty("regionCode")
    String regionCode,

    @JsonProperty("kind")
    String kind,

    @JsonProperty("nextPageToken")
    String nextPageToken,

    @JsonProperty("pageInfo")
    PageInfo pageInfo,

    @JsonProperty("etag")
    String etag,

    @JsonProperty("items")
    List<ListItem> items
) {
}