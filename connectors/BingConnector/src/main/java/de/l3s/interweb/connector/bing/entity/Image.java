package de.l3s.interweb.connector.bing.entity;

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
}
