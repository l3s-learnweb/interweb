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

package com.trievosoftware.giphy4j.entity.search;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.trievosoftware.giphy4j.entity.common.Meta;
import com.trievosoftware.giphy4j.entity.common.Pagination;
import com.trievosoftware.giphy4j.entity.giphy.GiphyData;

/**
 * This class represents a search feed response.
 *
 * @author Mark Tripoli
 */
public class SearchFeed {

    @SerializedName("data")
    private List<GiphyData> dataList;

    @SerializedName("meta")
    private Meta meta;

    @SerializedName("pagination")
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
