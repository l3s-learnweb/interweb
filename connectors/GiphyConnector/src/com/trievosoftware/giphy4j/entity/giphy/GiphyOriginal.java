/*
 * The MIT License
 *
 * Copyright (c) 2019 Trievo, LLC. https://trievosoftware.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 *
 */

package com.trievosoftware.giphy4j.entity.giphy;

import com.google.gson.annotations.SerializedName;

/**
 * This class represents the image file.
 *
 * @author Mark Tripoli
 */
public class GiphyOriginal {

    @SerializedName("url")
    private String url;

    @SerializedName("width")
    private String width;

    @SerializedName("height")
    private String height;

    @SerializedName("size")
    private String size;

    @SerializedName("frames")
    private String frames;

    @SerializedName("mp4")
    private String mp4;

    @SerializedName("mp4_size")
    private String mp4Size;

    @SerializedName("webp")
    private String webp;

    @SerializedName("webp_size")
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
    public String getWidth() {
        return width;
    }

    /**
     * Sets the width.
     *
     * @param width the width
     */
    public void setWidth(String width) {
        this.width = width;
    }

    /**
     * Returns the height.
     *
     * @return the height of the image
     */
    public String getHeight() {
        return height;
    }

    /**
     * Sets the height.
     *
     * @param height the height
     */
    public void setHeight(String height) {
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
     * Returns the frame count.
     *
     * @return the frame count
     */
    public String getFrames() {
        return frames;
    }

    /**
     * Sets the frame count.
     *
     * @param frames the frame count
     */
    public void setFrames(String frames) {
        this.frames = frames;
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
        outputString += "\n      frames = " + frames;
        outputString += "\n      mp4 = " + mp4;
        outputString += "\n      mp4Size = " + mp4Size;
        outputString += "\n      webp = " + webp;
        outputString += "\n      webpSize = " + webpSize;
        return outputString;
    }

}
