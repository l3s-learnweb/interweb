package de.l3s.interwebj.connector.vimeo.entity;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("uri")
    private String uri;
    @SerializedName("name")
    private String name;
    @SerializedName("description")
    private String description;
    @SerializedName("type")
    private String type;
    @SerializedName("link")
    private String link;
    @SerializedName("duration")
    private Integer duration;
    @SerializedName("width")
    private Integer width;
    @SerializedName("language")
    private Object language;
    @SerializedName("height")
    private Integer height;
    @SerializedName("embed")
    private Embed embed;
    @SerializedName("created_time")
    private String createdTime;
    @SerializedName("modified_time")
    private String modifiedTime;
    @SerializedName("release_time")
    private String releaseTime;
    @SerializedName("content_rating")
    private List<String> contentRating = null;
    @SerializedName("license")
    private Object license;
    @SerializedName("privacy")
    private Privacy privacy;
    @SerializedName("pictures")
    private Pictures pictures;
    @SerializedName("tags")
    private List<Tag> tags = null;
    @SerializedName("stats")
    private Stats stats;
    @SerializedName("metadata")
    private Metadata metadata;
    @SerializedName("user")
    private User user;
    @SerializedName("app")
    private Object app;
    @SerializedName("status")
    private String status;
    @SerializedName("resource_key")
    private String resourceKey;
    @SerializedName("review_link")
    private String reviewLink;
    @SerializedName("embed_presets")
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
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

    public Embed getEmbed() {
        return embed;
    }

    public void setEmbed(Embed embed) {
        this.embed = embed;
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

    public Privacy getPrivacy() {
        return privacy;
    }

    public void setPrivacy(Privacy privacy) {
        this.privacy = privacy;
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

    public Object getApp() {
        return app;
    }

    public void setApp(Object app) {
        this.app = app;
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
