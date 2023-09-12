package de.l3s.interweb.connector.flickr.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Description{

    @JsonProperty("_content")
    private String content;

    public void setContent(String content){
        this.content = content;
    }

    public String getContent(){
        return content;
    }
}