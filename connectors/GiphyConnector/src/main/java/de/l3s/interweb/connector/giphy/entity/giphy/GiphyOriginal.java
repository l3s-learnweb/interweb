package de.l3s.interweb.connector.giphy.entity.giphy;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents the image file.
 *
 * @author Mark Tripoli
 */
public class GiphyOriginal extends GiphyImage {

    @JsonProperty("frames")
    private String frames;

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

    @Override
    public String toString() {
        String outputString = super.toString();
        outputString += "\n      frames = " + frames;
        return outputString;
    }

}
