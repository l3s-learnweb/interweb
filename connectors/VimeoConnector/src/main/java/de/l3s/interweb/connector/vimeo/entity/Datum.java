package de.l3s.interweb.connector.vimeo.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Datum {

    @JsonProperty("uri")
    private String uri;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("type")
    private String type;
    @JsonProperty("link")
    private String link;
    @JsonProperty("duration")
    private Long duration;
    @JsonProperty("width")
    private Integer width;
    @JsonProperty("language")
    private Object language;
    @JsonProperty("height")
    private Integer height;
    @JsonProperty("player_embed_url")
    private String playerEmbedUrl;
    @JsonProperty("created_time")
    private String createdTime;
    @JsonProperty("modified_time")
    private String modifiedTime;
    @JsonProperty("release_time")
    private String releaseTime;
    @JsonProperty("content_rating")
    private List<String> contentRating;
    @JsonProperty("license")
    private Object license;
    @JsonProperty("pictures")
    private Pictures pictures;
    @JsonProperty("tags")
    private List<Tag> tags;
    @JsonProperty("stats")
    private Stats stats;
    @JsonProperty("metadata")
    private Metadata metadata;
    @JsonProperty("user")
    private User user;
    @JsonProperty("status")
    private String status;
    @JsonProperty("resource_key")
    private String resourceKey;
    @JsonProperty("review_link")
    private String reviewLink;
    @JsonProperty("embed_presets")
    private Object embedPresets;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Object getLanguage() {
        return language;
    }

    public void setLanguage(Object language) {
        this.language = language;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getPlayerEmbedUrl() {
        return playerEmbedUrl;
    }

    public void setPlayerEmbedUrl(String playerEmbedUrl) {
        this.playerEmbedUrl = playerEmbedUrl;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(String modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public List<String> getContentRating() {
        return contentRating;
    }

    public void setContentRating(List<String> contentRating) {
        this.contentRating = contentRating;
    }

    public Object getLicense() {
        return license;
    }

    public void setLicense(Object license) {
        this.license = license;
    }

    public Pictures getPictures() {
        return pictures;
    }

    public void setPictures(Pictures pictures) {
        this.pictures = pictures;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public void setResourceKey(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public String getReviewLink() {
        return reviewLink;
    }

    public void setReviewLink(String reviewLink) {
        this.reviewLink = reviewLink;
    }

    public Object getEmbedPresets() {
        return embedPresets;
    }

    public void setEmbedPresets(Object embedPresets) {
        this.embedPresets = embedPresets;
    }

}
