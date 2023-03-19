package de.l3s.interweb.core.search;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.l3s.interweb.core.query.Query;

@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -2762679444319967129L;

    @XmlElement(name = "query")
    private Query query;

    @JsonbProperty("total_results")
    @XmlElement(name = "total_results")
    private long totalResults = 0;
    @JsonbProperty("results_per_service")
    @XmlElementWrapper(name = "results_per_service")
    @XmlElement(name = "count")
    private Map<String, Long> resultsPerService;

    @JsonbProperty("results")
    @XmlElementWrapper(name = "results")
    @XmlElement(name = "result")
    private List<SearchItem> results;

    @JsonbProperty("created_time")
    @XmlElement(name = "created_time")
    private long createdTime;
    @JsonbProperty("elapsed_time")
    @XmlElement(name = "elapsed_time")
    private long elapsedTime;

    public SearchResponse() {
    }

    public SearchResponse(Query query) {
        this.query = query;
        this.results = new LinkedList<>();
        this.resultsPerService = new HashMap<>();
    }

    public void addConnectorResults(de.l3s.interweb.core.search.SearchResults queryResult) {
        results.addAll(queryResult.getItems());
        totalResults += queryResult.getTotalResults();
        resultsPerService.put(queryResult.getSource(), queryResult.getTotalResults());
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(final Query query) {
        this.query = query;
    }

    public long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(final long totalResults) {
        this.totalResults = totalResults;
    }

    public Map<String, Long> getResultsPerService() {
        return resultsPerService;
    }

    public void setResultsPerService(final Map<String, Long> resultsPerService) {
        this.resultsPerService = resultsPerService;
    }

    public List<SearchItem> getResults() {
        return results;
    }

    public void setResults(final List<SearchItem> results) {
        this.results = results;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public int size() {
        return results.size();
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
