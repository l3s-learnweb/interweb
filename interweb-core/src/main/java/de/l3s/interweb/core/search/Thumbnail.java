package de.l3s.interweb.core.search;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Thumbnail implements Serializable {
    @Serial
    private static final long serialVersionUID = -792701713759619246L;

    private String url;
    private Integer width;
    private Integer height;

    public Thumbnail() {
    }

    public Thumbnail(String url) {
        this.url = url;
    }

    public Thumbnail(String url, Integer width, Integer height) {
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

    public Integer getWidth() {
        return width;
    }

    public void setWidth(final Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(final Integer height) {
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
        return Objects.equals(width, thumbnail.width) && Objects.equals(height, thumbnail.height) && Objects.equals(url, thumbnail.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, width, height);
    }
}
