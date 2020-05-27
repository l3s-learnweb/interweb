package de.l3s.interwebj.clientjson.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class SearchResponse implements Serializable {
    private static final long serialVersionUID = 3566212743897913566L;

    @SerializedName("stat")
    private String stat;
    @SerializedName("error")
    private SearchError error;
    @SerializedName("query")
    private SearchQuery query;

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public SearchError getError() {
        return error;
    }

    public void setError(SearchError error) {
        this.error = error;
    }

    public SearchQuery getQuery() {
        return query;
    }

    public void setQuery(SearchQuery query) {
        this.query = query;
    }

}
