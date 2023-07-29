package de.l3s.interweb.connector.ipernity.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Doc {
    @JsonProperty("doc_id")
    public Integer docId;
    public String media;
    public String license;
    public String title;
    @JsonProperty(required = true)
    public Owner owner;
    public String rotation;
    @JsonProperty(required = true)
    public Thumb thumb;
    public Dates dates;
    public Count count;
    public String lng;
    public String lat;

    public static class Dates {
        @JsonProperty("posted_at")
        public String postedAt;
        public String created;
        @JsonProperty("last_comment_at")
        public String lastCommentAt;
        @JsonProperty("last_update_at")
        public String lastUpdateAt;
    }

    public static class Count {
        public Long visits;
        public Long faves;
        public Long comments;
        public Long notes;
    }
}
