package de.l3s.interweb.connector.flickr.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetInfoResponse {

    @JsonProperty("stat")
    private String stat;

    @JsonProperty("photo")
    private InfoPhoto photo;

    public void setStat(String stat) {
        this.stat = stat;
    }

    public String getStat() {
        return stat;
    }

    public void setPhoto(InfoPhoto photo) {
        this.photo = photo;
    }

    public InfoPhoto getPhoto() {
        return photo;
    }
}