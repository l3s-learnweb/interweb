package de.l3s.interweb.connector.youtube.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Snippet(

    @JsonProperty("publishTime")
    String publishTime,

    @JsonProperty("publishedAt")
    String publishedAt,

    @JsonProperty("defaultAudioLanguage")
    String defaultAudioLanguage,

    @JsonProperty("defaultLanguage")
    String defaultLanguage,

    @JsonProperty("localized")
    Localized localized,

    @JsonProperty("description")
    String description,

    @JsonProperty("title")
    String title,

    @JsonProperty("thumbnails")
    Thumbnails thumbnails,

    @JsonProperty("channelId")
    String channelId,

    @JsonProperty("categoryId")
    String categoryId,

    @JsonProperty("channelTitle")
    String channelTitle,

    @JsonProperty("tags")
    List<String> tags,

    @JsonProperty("liveBroadcastContent")
    String liveBroadcastContent
) {
}