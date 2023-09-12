package de.l3s.interweb.connector.flickr.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PhotoItem{
    @JsonProperty("id")
    private String id;

    @JsonProperty("media")
    private String media;

    @JsonProperty("title")
    private String title;

    @JsonProperty("tags")
    private String tags;

    @JsonProperty("description")
    private Description description;

    @JsonProperty("views")
    private Long views;

    @JsonProperty("owner")
    private String owner;

    @JsonProperty("ownername")
    private String ownerName;

    @JsonProperty("pathalias")
    private String pathAlias;

    @JsonProperty("o_width")
    private Integer originalWidth;

    @JsonProperty("o_height")
    private Integer originalHeight;

    @JsonProperty("url_s")
    private String mediaSmallUrl;

    @JsonProperty("width_s")
    private Integer mediaSmallWidth;

    @JsonProperty("height_s")
    private Integer mediaSmallHeight;

    @JsonProperty("url_m")
    private String mediaMediumUrl;

    @JsonProperty("width_m")
    private Integer mediaMediumWidth;

    @JsonProperty("height_m")
    private Integer mediaMediumHeight;

    @JsonProperty("url_l")
    private String mediaLargeUrl;

    @JsonProperty("width_l")
    private Integer mediaLargeWidth;

    @JsonProperty("height_l")
    private Integer mediaLargeHeight;

    @JsonProperty("url_o")
    private String mediaOriginalUrl;

    @JsonProperty("width_o")
    private Integer mediaOriginalWidth;

    @JsonProperty("height_o")
    private Integer mediaOriginalHeight;

    @JsonProperty("dateupload")
    private Integer dateUpload;

    @JsonProperty("lastupdate")
    private String lastUpdate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPathAlias() {
        if (pathAlias == null) {
            return owner;
        }
        return pathAlias;
    }

    public void setPathAlias(String pathAlias) {
        this.pathAlias = pathAlias;
    }

    public Integer getOriginalWidth() {
        return originalWidth;
    }

    public void setOriginalWidth(Integer originalWidth) {
        this.originalWidth = originalWidth;
    }

    public Integer getOriginalHeight() {
        return originalHeight;
    }

    public void setOriginalHeight(Integer originalHeight) {
        this.originalHeight = originalHeight;
    }

    public String getMediaSmallUrl() {
        return mediaSmallUrl;
    }

    public void setMediaSmallUrl(String mediaSmallUrl) {
        this.mediaSmallUrl = mediaSmallUrl;
    }

    public Integer getMediaSmallWidth() {
        return mediaSmallWidth;
    }

    public void setMediaSmallWidth(Integer mediaSmallWidth) {
        this.mediaSmallWidth = mediaSmallWidth;
    }

    public Integer getMediaSmallHeight() {
        return mediaSmallHeight;
    }

    public void setMediaSmallHeight(Integer mediaSmallHeight) {
        this.mediaSmallHeight = mediaSmallHeight;
    }

    public String getMediaMediumUrl() {
        return mediaMediumUrl;
    }

    public void setMediaMediumUrl(String mediaMediumUrl) {
        this.mediaMediumUrl = mediaMediumUrl;
    }

    public Integer getMediaMediumWidth() {
        return mediaMediumWidth;
    }

    public void setMediaMediumWidth(Integer mediaMediumWidth) {
        this.mediaMediumWidth = mediaMediumWidth;
    }

    public Integer getMediaMediumHeight() {
        return mediaMediumHeight;
    }

    public void setMediaMediumHeight(Integer mediaMediumHeight) {
        this.mediaMediumHeight = mediaMediumHeight;
    }

    public String getMediaLargeUrl() {
        return mediaLargeUrl;
    }

    public void setMediaLargeUrl(String mediaLargeUrl) {
        this.mediaLargeUrl = mediaLargeUrl;
    }

    public Integer getMediaLargeWidth() {
        return mediaLargeWidth;
    }

    public void setMediaLargeWidth(Integer mediaLargeWidth) {
        this.mediaLargeWidth = mediaLargeWidth;
    }

    public Integer getMediaLargeHeight() {
        return mediaLargeHeight;
    }

    public void setMediaLargeHeight(Integer mediaLargeHeight) {
        this.mediaLargeHeight = mediaLargeHeight;
    }

    public String getMediaOriginalUrl() {
        return mediaOriginalUrl;
    }

    public void setMediaOriginalUrl(String mediaOriginalUrl) {
        this.mediaOriginalUrl = mediaOriginalUrl;
    }

    public Integer getMediaOriginalWidth() {
        return mediaOriginalWidth;
    }

    public void setMediaOriginalWidth(Integer mediaOriginalWidth) {
        this.mediaOriginalWidth = mediaOriginalWidth;
    }

    public Integer getMediaOriginalHeight() {
        return mediaOriginalHeight;
    }

    public void setMediaOriginalHeight(Integer mediaOriginalHeight) {
        this.mediaOriginalHeight = mediaOriginalHeight;
    }

    public Integer getDateUpload() {
        return dateUpload;
    }

    public void setDateUpload(Integer dateUpload) {
        this.dateUpload = dateUpload;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}