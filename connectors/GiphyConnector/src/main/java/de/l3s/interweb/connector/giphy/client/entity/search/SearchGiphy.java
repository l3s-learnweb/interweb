package de.l3s.interweb.connector.giphy.client.entity.search;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.connector.giphy.client.entity.common.Meta;
import de.l3s.interweb.connector.giphy.client.entity.giphy.GiphyData;

/**
 * This class represents a single search response.
 *
 * @author Mark Tripoli
 */
public class SearchGiphy {
    @JsonProperty("data")
    private GiphyData data;

    @JsonProperty("meta")
    private Meta meta;

    /**
     * Returns the data.
     *
     * <p>
     * "data": { ... },
     *
     * @return the data
     */
    public GiphyData getData() {
        return data;
    }

    /**
     * Sets the data.
     *
     * @param data the data
     */
    public void setData(GiphyData data) {
        this.data = data;
    }

    /**
     * Returns the meta data.
     *
     * <p>
     * "meta": { ... }
     *
     * @return the meta data
     */
    public Meta getMeta() {
        return meta;
    }

    /**
     * Sets the meta data.
     *
     * @param meta the meta data
     */
    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    @Override
    public String toString() {
        String outputString = "SearchGiphy [";
        outputString += "\n  " + data;
        outputString += "\n  " + meta + "\n]";
        return outputString;
    }
}
