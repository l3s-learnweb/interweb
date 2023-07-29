package de.l3s.interweb.connector.vimeo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Connections {

    @JsonProperty("comments")
    private Comments comments;
    @JsonProperty("credits")
    private Credits credits;
    @JsonProperty("likes")
    private Likes likes;
    @JsonProperty("texttracks")
    private Texttracks texttracks;
    @JsonProperty("related")
    private Related related;
    @JsonProperty("albums")
    private Albums albums;
    @JsonProperty("available_albums")
    private AvailableAlbums availableAlbums;
    @JsonProperty("available_channels")
    private AvailableChannels availableChannels;

    public Comments getComments() {
        return comments;
    }

    public void setComments(Comments comments) {
        this.comments = comments;
    }

    public Credits getCredits() {
        return credits;
    }

    public void setCredits(Credits credits) {
        this.credits = credits;
    }

    public Likes getLikes() {
        return likes;
    }

    public void setLikes(Likes likes) {
        this.likes = likes;
    }

    public Texttracks getTexttracks() {
        return texttracks;
    }

    public void setTexttracks(Texttracks texttracks) {
        this.texttracks = texttracks;
    }

    public Related getRelated() {
        return related;
    }

    public void setRelated(Related related) {
        this.related = related;
    }

    public Albums getAlbums() {
        return albums;
    }

    public void setAlbums(Albums albums) {
        this.albums = albums;
    }

    public AvailableAlbums getAvailableAlbums() {
        return availableAlbums;
    }

    public void setAvailableAlbums(AvailableAlbums availableAlbums) {
        this.availableAlbums = availableAlbums;
    }

    public AvailableChannels getAvailableChannels() {
        return availableChannels;
    }

    public void setAvailableChannels(AvailableChannels availableChannels) {
        this.availableChannels = availableChannels;
    }

}
