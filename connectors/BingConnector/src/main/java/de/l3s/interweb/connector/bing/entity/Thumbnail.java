package de.l3s.interweb.connector.bing.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Thumbnail {

    private Integer width;

    private Integer height;

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
        return "Thumbnail [width=" + width + ", height=" + height + "]";
    }

}
