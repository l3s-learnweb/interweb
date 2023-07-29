package de.l3s.interweb.connector.vimeo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Embed {

    @JsonProperty("html")
    private String html;

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

}
