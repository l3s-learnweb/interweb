package de.l3s.interweb.core.search;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Thumbnail implements Serializable {
    @Serial
    private static final long serialVersionUID = -792701713759619246L;

    @JsonProperty("value")
    private String url;
    private int width;
    private int height;

    public Thumbnail() {
    }

    public Thumbnail(String url, int width, int height) {
        this.url = url;
        this.width = width;
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(final int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(final int height) {
        this.height = height;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Thumbnail thumbnail = (Thumbnail) o;
        return width == thumbnail.width && height == thumbnail.height && Objects.equals(url, thumbnail.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, width, height);
    }
}
