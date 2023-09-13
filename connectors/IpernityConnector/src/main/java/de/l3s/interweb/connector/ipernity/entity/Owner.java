package de.l3s.interweb.connector.ipernity.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Owner(
    @JsonProperty("user_id")
    String userId,

    @JsonProperty("username")
    String username,

    @JsonProperty("alias")
    String alias,

    @JsonProperty("is_pro")
    Integer isPro,

    @JsonProperty("icon")
    String icon
) {
}