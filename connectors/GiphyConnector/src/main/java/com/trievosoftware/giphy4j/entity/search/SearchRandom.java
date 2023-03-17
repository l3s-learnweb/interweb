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

import com.google.gson.annotations.SerializedName;
import com.trievosoftware.giphy4j.entity.common.Meta;
import com.trievosoftware.giphy4j.entity.giphy.GiphyRandom;

/**
 * This class represents a single search response.
 *
 * @author Mark Tripoli
 */
public class SearchRandom {
    @SerializedName("data")
    private GiphyRandom data;

    @SerializedName("meta")
    private Meta meta;

    /**
     * Returns the data.
     *
     * <p>
     * "data": { ... }
     *
     * @return The data.
     */
    public GiphyRandom getData() {
        return data;
    }

    /**
     * Sets the data.
     *
     * @param data the data
     */
    public void setData(GiphyRandom data) {
        this.data = data;
    }

    /**
     * Returns the meta data.
     *
     * <p>
     * "meta": { ... }
     *
     * @return the meta data.
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
        String outputString = "SearchRandom [";
        outputString += "\n  " + data;
        outputString += "\n  " + meta + "\n]";
        return outputString;
    }
}
