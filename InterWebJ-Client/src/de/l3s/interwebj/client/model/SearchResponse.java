package de.l3s.interwebj.client.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.gson.annotations.SerializedName;

public class SearchResponse implements Serializable {
    private static final long serialVersionUID = 3566212743897913566L;

    @SerializedName("query")
    private SearchQuery query;
    @SerializedName("total_results")
    private Long totalResults;
    @SerializedName("results_per_service")
    private Map<String, Long> resultsPerService;

    @SerializedName("results")
    private List<SearchResult> results = null;

    @SerializedName("created_time")
    private String createdTime;
    @SerializedName("elapsed_time")
    private String elapsedTime;

    public SearchQuery getQuery() {
        return query;
    }

    public void setQuery(final SearchQuery query) {
        this.query = query;
    }

    public Long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(final Long totalResults) {
        this.totalResults = totalResults;
    }

    public Map<String, Long> getResultsPerService() {
        return resultsPerService;
    }

    public void setResultsPerService(final Map<String, Long> resultsPerService) {
        this.resultsPerService = resultsPerService;
    }

    public List<SearchResult> getResults() {
        return results;
    }

    public void setResults(final List<SearchResult> results) {
        this.results = results;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(final String createdTime) {
        this.createdTime = createdTime;
    }

    public String getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(final String elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("query", query)
            .append("totalResults", totalResults)
            .append("resultsPerService", resultsPerService)
            .append("results", results)
            .append("createdTime", createdTime)
            .append("elapsedTime", elapsedTime)
            .toString();
    }
}
