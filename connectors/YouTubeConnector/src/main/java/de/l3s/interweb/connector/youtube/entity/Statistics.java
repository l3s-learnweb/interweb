package de.l3s.interweb.connector.youtube.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Statistics(

    @JsonProperty("likeCount")
    Long likeCount,

    @JsonProperty("viewCount")
    Long viewCount,

    @JsonProperty("favoriteCount")
    Long favoriteCount,

    @JsonProperty("commentCount")
    Long commentCount
) {
}