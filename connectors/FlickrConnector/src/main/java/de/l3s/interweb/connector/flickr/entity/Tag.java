package de.l3s.interweb.connector.flickr.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Tag {

    @JsonProperty("author")
    private String author;

    @JsonProperty("authorname")
    private String authorname;

    @JsonProperty("raw")
    private String raw;

    @JsonProperty("id")
    private String id;

    @JsonProperty("_content")
    private String content;

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthorname(String authorname) {
        this.authorname = authorname;
    }

    public String getAuthorname() {
        return authorname;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public String getRaw() {
        return raw;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}