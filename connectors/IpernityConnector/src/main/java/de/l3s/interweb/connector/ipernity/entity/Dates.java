package de.l3s.interweb.connector.ipernity.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Dates(
    @JsonProperty("created")
    String created,

    @JsonProperty("last_comment_at")
    String lastCommentAt,

    @JsonProperty("last_update_at")
    String lastUpdateAt,

    @JsonProperty("posted_at")
    String postedAt
) {
}