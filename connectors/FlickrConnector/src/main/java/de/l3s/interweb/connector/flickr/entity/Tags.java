package de.l3s.interweb.connector.flickr.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Tags {

    @JsonProperty("tag")
    private List<Tag> tag;

    public void setTag(List<Tag> tag) {
        this.tag = tag;
    }

    public List<Tag> getTag() {
        return tag;
    }
}