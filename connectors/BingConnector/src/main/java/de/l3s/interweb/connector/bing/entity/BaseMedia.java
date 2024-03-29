package de.l3s.interweb.connector.bing.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public abstract class BaseMedia {

    @JsonProperty("webSearchUrl")
    String webSearchUrl;
    @JsonProperty("name")
    String name;
    @JsonProperty("thumbnailUrl")
    String thumbnailUrl;
    @JsonProperty("datePublished")
    String datePublished;
    @JsonProperty("contentUrl")
    String contentUrl;
    @JsonProperty("hostPageUrl")
    String hostPageUrl;
    @JsonProperty("encodingFormat")
    String encodingFormat;
    @JsonProperty("hostPageDisplayUrl")
    String hostPageDisplayUrl;
    @JsonProperty("width")
    Integer width;
    @JsonProperty("height")
    Integer height;
    @JsonProperty("thumbnail")
    Thumbnail thumbnail;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebSearchUrl() {
        return webSearchUrl;
    }

    public void setWebSearchUrl(String webSearchUrl) {
        this.webSearchUrl = webSearchUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(String datePublished) {
        this.datePublished = datePublished;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getHostPageUrl() {
        return hostPageUrl;
    }

    public void setHostPageUrl(String hostPageUrl) {
        this.hostPageUrl = hostPageUrl;
    }

    public String getEncodingFormat() {
        return encodingFormat;
    }

    public void setEncodingFormat(String encodingFormat) {
        this.encodingFormat = encodingFormat;
    }

    public String getHostPageDisplayUrl() {
        return hostPageDisplayUrl;
    }

    public void setHostPageDisplayUrl(String hostPageDisplayUrl) {
        this.hostPageDisplayUrl = hostPageDisplayUrl;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }
}
