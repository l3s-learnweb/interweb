package de.l3s.interwebj.clientjson.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class SearchThumbnail implements Serializable {
    private static final long serialVersionUID = 4849378168629011137L;

    @SerializedName("value")
    private String url;
    @SerializedName("width")
    private Integer width;
    @SerializedName("height")
    private Integer height;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

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

}
