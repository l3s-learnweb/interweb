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
 * This class is used to hold the image information of the response.
 *
 * @author Mark Tripoli
 */
public class GiphyContainer {

    @SerializedName("fixed_height")
    private GiphyImage fixedHeight;

    @SerializedName("fixed_height_still")
    private GiphyImage fixedHeightStill;

    @SerializedName("fixed_height_downsampled")
    private GiphyImage fixedHeightDownsampled;

    @SerializedName("fixed_width")
    private GiphyImage fixedWidth;

    @SerializedName("fixed_width_still")
    private GiphyImage fixedWidthStill;

    @SerializedName("fixed_width_downsampled")
    private GiphyImage fixedWidthDownsampled;

    @SerializedName("fixed_height_small")
    private GiphyImage fixedHeightSmall;

    @SerializedName("fixed_height_small_still")
    private GiphyImage fixedHeightSmallStill;

    @SerializedName("fixed_width_small")
    private GiphyImage fixedWidthSmall;

    @SerializedName("fixed_width_small_still")
    private GiphyImage fixedWidthSmallStill;

    @SerializedName("downsized")
    private GiphyImage downsized;

    @SerializedName("downsized_still")
    private GiphyImage downsizedStill;

    @SerializedName("downsized_small")
    private GiphyImage downsizedSmall;

    @SerializedName("downsized_medium")
    private GiphyImage downsizedMedium;

    @SerializedName("downsized_large")
    private GiphyImage downsizedLarge;

    @SerializedName("original")
    private GiphyOriginal original;

    @SerializedName("original_still")
    private GiphyImage originalStill;

    @SerializedName("looping")
    private GiphyImage looping;

    /**
     * Returns the image with fixed height. Height is set to 200px. Good for
     * mobile use.
     *
     * @return the image with fixed height
     */
    public GiphyImage getFixedHeight() {
        return fixedHeight;
    }

    /**
     * Sets the image with fixed height.
     *
     * @param fixedHeight the image with fixed height
     */
    public void setFixedHeight(GiphyImage fixedHeight) {
        this.fixedHeight = fixedHeight;
    }

    /**
     * Returns a static preview image for fixed_height.
     *
     * @return the static preview image
     */
    public GiphyImage getFixedHeightStill() {
        return fixedHeightStill;
    }

    /**
     * Sets the static preview image for fixed_height.
     *
     * @param fixedHeightStill the static preview image
     */
    public void setFixedHeightStill(GiphyImage fixedHeightStill) {
        this.fixedHeightStill = fixedHeightStill;
    }

    /**
     * Height is set to 200px. Reduced to 6 frames to minimize file size to the
     * lowest. Works well for unlimited scroll on mobile and as animated
     * previews. See Giphy.com on mobile web as an example.
     *
     * @return the reduced image
     */
    public GiphyImage getFixedHeightDownsampled() {
        return fixedHeightDownsampled;
    }

    /**
     * Sets the the reduced image.
     *
     * @param fixedHeightDownsampled the reduced image
     */
    public void setFixedHeightDownsampled(GiphyImage fixedHeightDownsampled) {
        this.fixedHeightDownsampled = fixedHeightDownsampled;
    }

    /**
     * Returns the image with fixed width . Width is set to 200px. Good for
     * mobile use.
     *
     * @return the image with fixed height
     */
    public GiphyImage getFixedWidth() {
        return fixedWidth;
    }

    /**
     * Sets the image with fixed width.
     *
     * @param fixedWidth the image with fixed width
     */
    public void setFixedWidth(GiphyImage fixedWidth) {
        this.fixedWidth = fixedWidth;
    }

    /**
     * Returns a static preview image for fixed_width.
     *
     * @return the static preview image
     */
    public GiphyImage getFixedWidthStill() {
        return fixedWidthStill;
    }

    /**
     * Sets the static preview image for fixed_width.
     *
     * @param fixedWidthStill the static preview image
     */
    public void setFixedWidthStill(GiphyImage fixedWidthStill) {
        this.fixedWidthStill = fixedWidthStill;
    }

    /**
     * Width set to 200px. Reduced to 6 frames. Works well for unlimited scroll
     * on mobile and as animated previews.
     *
     * @return the the reduced image
     */
    public GiphyImage getFixedWidthDownsampled() {
        return fixedWidthDownsampled;
    }

    /**
     * Sets the the reduced image.
     *
     * @param fixedWidthDownsampled the reduced image
     */
    public void setFixedWidthDownsampled(GiphyImage fixedWidthDownsampled) {
        this.fixedWidthDownsampled = fixedWidthDownsampled;
    }

    /**
     * Returns the image with fixed height . Height set to 100px. Good for
     * mobile keyboards.
     *
     * @return the image with fixed height
     */
    public GiphyImage getFixedHeightSmall() {
        return fixedHeightSmall;
    }

    /**
     * Sets the image with fixed height.
     *
     * @param fixedHeightSmall the image with fixed height
     */
    public void setFixedHeightSmall(GiphyImage fixedHeightSmall) {
        this.fixedHeightSmall = fixedHeightSmall;
    }

    /**
     * Returns a static preview image for fixed_height_small.
     *
     * @return the static preview image
     */
    public GiphyImage getFixedHeightSmallStill() {
        return fixedHeightSmallStill;
    }

    /**
     * Sets the static preview image for fixed_height_small.
     *
     * @param fixedHeightSmallStill the static preview image
     */
    public void setFixedHeightSmallStill(GiphyImage fixedHeightSmallStill) {
        this.fixedHeightSmallStill = fixedHeightSmallStill;
    }

    /**
     * Returns the image with fixed width. Width set to 100px. Good for mobile
     * keyboards.
     *
     * @return the image with fixed width
     */
    public GiphyImage getFixedWidthSmall() {
        return fixedWidthSmall;
    }

    /**
     * Sets the image with fixed width.
     *
     * @param fixedWidthSmall the image with fixed width
     */
    public void setFixedWidthSmall(GiphyImage fixedWidthSmall) {
        this.fixedWidthSmall = fixedWidthSmall;
    }

    /**
     * Returns a static preview image for fixed_width_small.
     *
     * @return the static preview image
     */
    public GiphyImage getFixedWidthSmallStill() {
        return fixedWidthSmallStill;
    }

    /**
     * Sets the static preview image for fixed_width_small.
     *
     * @param fixedWidthSmallStill the static preview image
     */
    public void setFixedWidthSmallStill(GiphyImage fixedWidthSmallStill) {
        this.fixedWidthSmallStill = fixedWidthSmallStill;
    }

    /**
     * File size is under 1.5mb.
     *
     * @return a file with under 1.5mb
     */
    public GiphyImage getDownsized() {
        return downsized;
    }

    /**
     * Sets the downsized image.
     *
     * @param downsized the downsized image.
     */
    public void setDownsized(GiphyImage downsized) {
        this.downsized = downsized;
    }

    /**
     * Returns a static preview image for the downsized image.
     *
     * @return the static preview image
     */
    public GiphyImage getDownsizedStill() {
        return downsizedStill;
    }

    /**
     * Sets the static preview image for the downsized image.
     *
     * @param downsizedStill the static preview image
     */
    public void setDownsizedStill(GiphyImage downsizedStill) {
        this.downsizedStill = downsizedStill;
    }

    public GiphyImage getDownsizedSmall() {
        return downsizedSmall;
    }

    public void setDownsizedSmall(GiphyImage downsizedSmall) {
        this.downsizedSmall = downsizedSmall;
    }

    /**
     * File size is under 5mb.
     *
     * @return a file with under 5mb
     */
    public GiphyImage getDownsizedMedium() {
        return downsizedMedium;
    }

    /**
     * Sets the downsized image.
     *
     * @param downsizedMedium the downsized image.
     */
    public void setDownsizedMedium(GiphyImage downsizedMedium) {
        this.downsizedMedium = downsizedMedium;
    }

    /**
     * File size is under 8mb.
     *
     * @return a file with under 8mb
     */
    public GiphyImage getDownsizedLarge() {
        return downsizedLarge;
    }

    /**
     * Sets the downsized image.
     *
     * @param downsizedLarge the downsized image.
     */
    public void setDownsizedLarge(GiphyImage downsizedLarge) {
        this.downsizedLarge = downsizedLarge;
    }

    /**
     * Returns the original image. Original file size and file dimensions. Good
     * for desktop use.
     *
     * @return the original image
     */
    public GiphyOriginal getOriginal() {
        return original;
    }

    /**
     * Sets the original image.
     *
     * @param original the original image
     */
    public void setOriginal(GiphyOriginal original) {
        this.original = original;
    }

    /**
     * Returns a static preview image for the original image.
     *
     * @return the static preview image
     */
    public GiphyImage getOriginalStill() {
        return originalStill;
    }

    /**
     * Sets the static preview image.
     *
     * @param originalStill the static preview image
     */
    public void setOriginalStill(GiphyImage originalStill) {
        this.originalStill = originalStill;
    }

    /**
     * Returns a looping image.
     *
     * @return the looping image.
     */
    public GiphyImage getLooping() {
        return looping;
    }

    /**
     * Sets the looping image.
     *
     * @param looping the looping image.
     */
    public void setLooping(GiphyImage looping) {
        this.looping = looping;
    }

    @Override
    public String toString() {
        String outputString = "GifImage (Original) [";
        outputString += original;
        outputString += "\n    ]";
        return outputString;
    }
}
