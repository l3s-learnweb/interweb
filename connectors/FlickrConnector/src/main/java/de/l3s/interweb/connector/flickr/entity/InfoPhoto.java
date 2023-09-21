package de.l3s.interweb.connector.flickr.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.l3s.interweb.connector.flickr.adapters.ContentAdapter;

public class InfoPhoto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("media")
    private String media;

    @JsonProperty("secret")
    private String secret;

    @JsonProperty("server")
    private String server;

    @JsonDeserialize(using = ContentAdapter.class)
    @JsonProperty("title")
    private String title;

    @JsonProperty("tags")
    private Tags tags;

    @JsonProperty("owner")
    private Owner owner;

    @JsonProperty("dateuploaded")
    private Integer dateUploaded;

    @JsonDeserialize(using = ContentAdapter.class)
    @JsonProperty("comments")
    private String comments;

    @JsonProperty("safety_level")
    private String safetyLevel;

    @JsonProperty("rotation")
    private int rotation;

    @JsonDeserialize(using = ContentAdapter.class)
    @JsonProperty("description")
    private String description;

    @JsonProperty("views")
    private Long views;

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setDateUploaded(Integer dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    public Integer getDateUploaded() {
        return dateUploaded;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getComments() {
        return comments;
    }

    public void setSafetyLevel(String safetyLevel) {
        this.safetyLevel = safetyLevel;
    }

    public String getSafetyLevel() {
        return safetyLevel;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public int getRotation() {
        return rotation;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getMedia() {
        return media;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTags(Tags tags) {
        this.tags = tags;
    }

    public Tags getTags() {
        return tags;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public Long getViews() {
        return views;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }
}