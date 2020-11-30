package de.l3s.bingService.models;

import com.google.common.base.MoreObjects;

public class Image extends BaseMedia {

    private String contentSize;
    private String accentColor;

    public String getContentSize() {
        return contentSize;
    }

    public void setContentSize(final String contentSize) {
        this.contentSize = contentSize;
    }

    public String getAccentColor() {
        return accentColor;
    }

    public void setAccentColor(final String accentColor) {
        this.accentColor = accentColor;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("webSearchUrl", webSearchUrl)
            .add("name", name)
            .add("thumbnailUrl", thumbnailUrl)
            .add("datePublished", datePublished)
            .add("contentUrl", contentUrl)
            .add("hostPageUrl", hostPageUrl)
            .add("encodingFormat", encodingFormat)
            .add("hostPageDisplayUrl", hostPageDisplayUrl)
            .add("width", width)
            .add("height", height)
            .add("thumbnail", thumbnail)
            .add("contentSize", contentSize)
            .toString();
    }
}
