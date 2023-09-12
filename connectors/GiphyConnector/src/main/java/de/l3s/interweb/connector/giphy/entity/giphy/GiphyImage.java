package de.l3s.interweb.connector.giphy.entity.giphy;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents the image file.
 *
 * @author Mark Tripoli
 */
public class GiphyImage {

    @JsonProperty("url")
    private String url;

    @JsonProperty("width")
    private Integer width;

    @JsonProperty("height")
    private Integer height;

    @JsonProperty("size")
    private String size;

    @JsonProperty("mp4")
    private String mp4;

    @JsonProperty("mp4_size")
    private String mp4Size;

    @JsonProperty("webp")
    private String webp;

    @JsonProperty("webp_size")
    private String webpSize;

    /**
     * Returns the URL.
     *
     * @return the URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL.
     *
     * @param url the URL
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Returns the width.
     *
     * @return the width of the image
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * Sets the width.
     *
     * @param width the width
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * Returns the height.
     *
     * @return the height of the image
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * Sets the height.
     *
     * @param height the height
     */
    public void setHeight(Integer height) {
        this.height = height;
    }

    /**
     * Returns the size.
     *
     * @return the size
     */
    public String getSize() {
        return size;
    }

    /**
     * Sets the size.
     *
     * @param size the size
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * Returns the mp4 URL.
     *
     * @return the mp4 URL
     */
    public String getMp4() {
        return mp4;
    }

    /**
     * Sets the mp4 URL.
     *
     * @param mp4 the mp4 URL
     */
    public void setMp4(String mp4) {
        this.mp4 = mp4;
    }

    /**
     * Returns the mp4 size.
     *
     * @return the mp4 size
     */
    public String getMp4Size() {
        return mp4Size;
    }

    /**
     * Sets the mp4 size.
     *
     * @param mp4Size the mp4 size
     */
    public void setMp4Size(String mp4Size) {
        this.mp4Size = mp4Size;
    }

    public String getWebp() {
        return webp;
    }

    /**
     * Sets the webp URL.
     *
     * @param webp the webp URL
     */
    public void setWebp(String webp) {
        this.webp = webp;
    }

    public String getWebpSize() {
        return webpSize;
    }

    /**
     * Sets the webp size.
     *
     * @param webpSize the webp size
     */
    public void setWebpSize(String webpSize) {
        this.webpSize = webpSize;
    }

    @Override
    public String toString() {
        String outputString = "";
        outputString += "\n      url = " + url;
        outputString += "\n      width = " + width;
        outputString += "\n      height = " + height;
        outputString += "\n      size = " + size;
        outputString += "\n      mp4 = " + mp4;
        outputString += "\n      mp4Size = " + mp4Size;
        outputString += "\n      webp = " + webp;
        outputString += "\n      webpSize = " + webpSize;
        return outputString;
    }

}
