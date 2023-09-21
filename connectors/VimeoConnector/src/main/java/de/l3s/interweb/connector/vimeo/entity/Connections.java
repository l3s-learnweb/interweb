package de.l3s.interweb.connector.vimeo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Connections {

    @JsonProperty("comments")
    private Connection comments;

    @JsonProperty("likes")
    private Connection likes;

    public void setComments(Connection comments) {
        this.comments = comments;
    }

    public Connection getComments() {
        return comments;
    }

    public void setLikes(Connection likes) {
        this.likes = likes;
    }

    public Connection getLikes() {
        return likes;
    }
}