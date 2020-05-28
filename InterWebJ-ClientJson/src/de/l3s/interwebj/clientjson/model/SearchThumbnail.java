package de.l3s.interwebj.clientjson.model;

import java.io.Serializable;
import java.util.StringJoiner;

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

    @Override
    public String toString() {
        return new StringJoiner(", ", SearchThumbnail.class.getSimpleName() + "[", "]")
            .add("url='" + url + "'")
            .add("width=" + width)
            .add("height=" + height)
            .toString();
    }
}
