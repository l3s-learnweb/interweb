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
 * This class is used to hold the image data of the response.
 *
 * @author Mark Tripoli
 */
public class GiphyData {

    @SerializedName("type")
    private String type;

    @SerializedName("id")
    private String id;

    @SerializedName("slug")
    private String slug;

    @SerializedName("url")
    private String url;

    @SerializedName("bitly_gif_url")
    private String bitlyGifUrl;

    @SerializedName("bitly_url")
    private String bitlyUrl;

    @SerializedName("embed_url")
    private String embedUrl;

    @SerializedName("username")
    private String username;

    @SerializedName("source")
    private String source;

    @SerializedName("title")
    private String title;

    @SerializedName("rating")
    private String rating;

    @SerializedName("caption")
    private String caption;

    @SerializedName("content_url")
    private String contentUrl;

    @SerializedName("source_tld")
    private String sourceTld;

    @SerializedName("source_post_url")
    private String sourcePostUrl;

    @SerializedName("import_datetime")
    private String importDatetime;

    @SerializedName("trending_datetime")
    private String trendingDatetime;

    @SerializedName("user")
    private GiphyUser user;

    @SerializedName("images")
    private GiphyContainer images;

    /**
     * Returns the type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the slug.
     *
     * @return the slug
     */
    public String getSlug() {
        return slug;
    }

    /**
     * Sets the slug.
     *
     * @param slug the slug
     */
    public void setSlug(String slug) {
        this.slug = slug;
    }

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
     * Returns the bitly GIF URL.
     *
     * @return the bitly GIF URL
     */
    public String getBitlyGifUrl() {
        return bitlyGifUrl;
    }

    /**
     * Sets the bitly GIF URL.
     *
     * @param bitlyGifUrl the bitly GIF URL
     */
    public void setBitlyGifUrl(String bitlyGifUrl) {
        this.bitlyGifUrl = bitlyGifUrl;
    }

    /**
     * Returns the bitly URL.
     *
     * @return the bitly URL
     */
    public String getBitlyUrl() {
        return bitlyUrl;
    }

    /**
     * Sets the bitly URL.
     *
     * @param bitlyUrl the bitly URL
     */
    public void setBitlyUrl(String bitlyUrl) {
        this.bitlyUrl = bitlyUrl;
    }

    /**
     * Returns the embed URL.
     *
     * @return the embed URL
     */
    public String getEmbedUrl() {
        return embedUrl;
    }

    /**
     * Sets the embed URL.
     *
     * @param embedUrl the embed URL
     */
    public void setEmbedUrl(String embedUrl) {
        this.embedUrl = embedUrl;
    }

    /**
     * Returns the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the source.
     *
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the source.
     *
     * @param source the source
     */
    public void setSource(String source) {
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the rating.
     *
     * @return the rating
     */
    public String getRating() {
        return rating;
    }

    /**
     * Sets the rating.
     *
     * @param rating the rating
     */
    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Returns the contentUrl.
     *
     * @return the contentUrl
     */
    public String getContentUrl() {
        return contentUrl;
    }

    /**
     * Sets the contentUrl.
     *
     * @param contentUrl the contentUrl
     */
    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    /**
     * Returns the sourceTld.
     *
     * @return the sourceTld
     */
    public String getSourceTld() {
        return sourceTld;
    }

    /**
     * Sets the sourceTld.
     *
     * @param sourceTld the sourceTld
     */
    public void setSourceTld(String sourceTld) {
        this.sourceTld = sourceTld;
    }

    /**
     * Returns the sourcePostUrl.
     *
     * @return the sourcePostUrl
     */
    public String getSourcePostUrl() {
        return sourcePostUrl;
    }

    /**
     * Sets the sourcePostUrl.
     *
     * @param sourcePostUrl the sourcePostUrl
     */
    public void setSourcePostUrl(String sourcePostUrl) {
        this.sourcePostUrl = sourcePostUrl;
    }

    /**
     * Returns the importDatetime.
     *
     * @return the importDatetime
     */
    public String getImportDatetime() {
        return importDatetime;
    }

    /**
     * Sets the importDatetime.
     *
     * @param importDatetime the importDatetime
     */
    public void setImportDatetime(String importDatetime) {
        this.importDatetime = importDatetime;
    }

    /**
     * Returns the trendingDatetime.
     *
     * @return the trendingDatetime
     */
    public String getTrendingDatetime() {
        return trendingDatetime;
    }

    /**
     * Sets the trendingDatetime.
     *
     * @param trendingDatetime the trendingDatetime
     */
    public void setTrendingDatetime(String trendingDatetime) {
        this.trendingDatetime = trendingDatetime;
    }

    public GiphyUser getUser() {
        return user;
    }

    public void setUser(final GiphyUser user) {
        this.user = user;
    }

    /**
     * Returns the image.
     *
     * @return the image
     */
    public GiphyContainer getImages() {
        return images;
    }

    /**
     * Sets the image.
     *
     * @param images the image
     */
    public void setImages(GiphyContainer images) {
        this.images = images;
    }

    @Override
    public String toString() {
        String outputString = "GifData [";
        outputString += "\n    id = " + id;
        outputString += "\n    slug = " + slug;
        outputString += "\n    url = " + url;
        outputString += "\n    user = " + user;
        outputString += "\n    " + images;
        outputString += "\n  ]";
        return outputString;
    }
}
