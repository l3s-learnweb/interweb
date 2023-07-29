package de.l3s.interweb.connector.giphy.client.entity.search;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.connector.giphy.client.entity.common.Meta;
import de.l3s.interweb.connector.giphy.client.entity.common.Pagination;
import de.l3s.interweb.connector.giphy.client.entity.giphy.GiphyData;

/**
 * This class represents a search feed response.
 *
 * @author Mark Tripoli
 */
public class SearchFeed {

    @JsonProperty("data")
    private List<GiphyData> dataList;

    @JsonProperty("meta")
    private Meta meta;

    @JsonProperty("pagination")
    private Pagination pagination;

    /**
     * Returns the data list.
     *
     * <p>
     * "data": [ ... ],
     *
     * @return the data list
     */
    public List<GiphyData> getDataList() {
        return dataList;
    }

    /**
     * Sets the data list.
     *
     * @param dataList the data list
     */
    public void setDataList(List<GiphyData> dataList) {
        this.dataList = dataList;
    }

    /**
     * Returns the meta information object.
     *
     * <p>
     * "meta": { ... },
     *
     * @return the meta information object
     */
    public Meta getMeta() {
        return meta;
    }

    /**
     * Sets the meta object.
     *
     * @param meta the meta object
     */
    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    /**
     * Returns the pagination object.
     *
     * <p>
     * "pagination": { ... }
     *
     * @return the pagination object
     */
    public Pagination getPagination() {
        return pagination;
    }

    /**
     * Sets the pagination object.
     *
     * @param pagination the pagination object
     */
    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    @Override
    public String toString() {
        StringBuilder outputString = new StringBuilder("SearchFeed [");
        for (GiphyData data : dataList) {
            outputString.append("\n  ").append(data);
        }
        outputString.append("\n  ").append(meta).append("\n  ").append(pagination).append("\n]");
        return outputString.toString();
    }
}
