package de.l3s.interweb.connector.slideshare.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("Meta")
public class Meta {

    @JsonProperty("Query")
    private String query;
    @JsonProperty("ResultOffset")
    private int resultOffset;
    @JsonProperty("NumResults")
    private int numResults;
    @JsonProperty("TotalResults")
    private int totalResults;

    public int getNumResults() {
        return numResults;
    }

    public void setNumResults(int numResults) {
        this.numResults = numResults;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getResultOffset() {
        return resultOffset;
    }

    public void setResultOffset(int resultOffset) {
        this.resultOffset = resultOffset;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

}
