package de.l3s.interweb.connector.slideshare.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Slideshow {

    @JsonProperty("ID")
    private int id;
    @JsonProperty("Title")
    private String title;
    @JsonProperty("Description")
    private String description;
    @JsonProperty("Status")
    private int status;
    @JsonProperty("Username")
    private String userName;
    @JsonProperty("URL")
    private String url;
    @JsonProperty("ThumbnailURL")
    private String thumbnailURL;
    @JsonProperty("ThumbnailSize")
    private String thumbnailSize;
    @JsonProperty("ThumbnailSmallURL")
    private String thumbnailSmallURL;
    @JsonProperty("ThumbnailXLargeURL")
    private String thumbnailXLargeURL;
    @JsonProperty("ThumbnailXXLargeURL")
    private String thumbnailXXLargeURL;
    @JsonProperty("Embed")
    private String embed;
    @JsonProperty("SlideshowEmbedUrl")
    private String slideshowEmbedUrl;
    @JsonProperty("Language")
    private String language;
    @JsonProperty("Format")
    private String format;
    @JsonProperty("Download")
    private int download;
    @JsonProperty("DownloadUrl")
    private String downloadUrl;
    @JsonProperty("SlideshowType")
    private int slideshowType;
    @JsonProperty("InContest")
    private int inContest;
    @JsonProperty("SecretKey")
    private String secretKey;
    @JsonProperty("Updated")
    private String updated;
    @JsonProperty("Created")
    private String created;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String getThumbnailSize() {
        return thumbnailSize;
    }

    public void setThumbnailSize(String thumbnailSize) {
        this.thumbnailSize = thumbnailSize;
    }

    public String getThumbnailSmallURL() {
        return thumbnailSmallURL;
    }

    public void setThumbnailSmallURL(String thumbnailSmallURL) {
        this.thumbnailSmallURL = thumbnailSmallURL;
    }

    public String getThumbnailXLargeURL() {
        return thumbnailXLargeURL;
    }

    public void setThumbnailXLargeURL(String thumbnailXLargeURL) {
        this.thumbnailXLargeURL = thumbnailXLargeURL;
    }

    public String getThumbnailXXLargeURL() {
        return thumbnailXXLargeURL;
    }

    public void setThumbnailXXLargeURL(String thumbnailXXLargeURL) {
        this.thumbnailXXLargeURL = thumbnailXXLargeURL;
    }

    public String getEmbed() {
        return embed;
    }

    public void setEmbed(String embed) {
        this.embed = embed;
    }

    public String getSlideshowEmbedUrl() {
        return slideshowEmbedUrl;
    }

    public void setSlideshowEmbedUrl(String slideshowEmbedUrl) {
        this.slideshowEmbedUrl = slideshowEmbedUrl;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getDownload() {
        return download;
    }

    public void setDownload(int download) {
        this.download = download;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getSlideshowType() {
        return slideshowType;
    }

    public void setSlideshowType(int slideshowType) {
        this.slideshowType = slideshowType;
    }

    public int getInContest() {
        return inContest;
    }

    public void setInContest(int inContest) {
        this.inContest = inContest;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
