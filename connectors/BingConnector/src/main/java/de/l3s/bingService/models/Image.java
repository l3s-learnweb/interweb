package de.l3s.bingService.models;

import org.apache.commons.lang3.builder.ToStringBuilder;

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
        return new ToStringBuilder(this)
            .append("webSearchUrl", webSearchUrl)
            .append("name", name)
            .append("thumbnailUrl", thumbnailUrl)
            .append("datePublished", datePublished)
            .append("contentUrl", contentUrl)
            .append("hostPageUrl", hostPageUrl)
            .append("encodingFormat", encodingFormat)
            .append("hostPageDisplayUrl", hostPageDisplayUrl)
            .append("width", width)
            .append("height", height)
            .append("thumbnail", thumbnail)
            .append("contentSize", contentSize)
            .toString();
    }
}
