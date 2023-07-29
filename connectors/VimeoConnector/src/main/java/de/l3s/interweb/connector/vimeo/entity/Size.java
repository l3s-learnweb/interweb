package de.l3s.interweb.connector.vimeo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Size {

    @JsonProperty("width")
    private Integer width;
    @JsonProperty("height")
    private Integer height;
    @JsonProperty("link")
    private String link;
    @JsonProperty("link_with_play_button")
    private String linkWithPlayButton;

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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLinkWithPlayButton() {
        return linkWithPlayButton;
    }

    public void setLinkWithPlayButton(String linkWithPlayButton) {
        this.linkWithPlayButton = linkWithPlayButton;
    }

}
