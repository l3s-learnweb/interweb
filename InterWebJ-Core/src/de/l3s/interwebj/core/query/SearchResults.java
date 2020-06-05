package de.l3s.interwebj.core.query;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interwebj.core.connector.ConnectorSearchResults;

@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchResults implements Serializable {
    private static final long serialVersionUID = -2762679444319967129L;

    @XmlElement(name = "query")
    private Query query;

    @XmlElement(name = "total_results")
    private long totalResults = 0;
    @JsonProperty("results_per_service")
    @XmlElementWrapper(name = "results_per_service")
    @XmlElement(name = "count")
    private Map<String, Long> resultsPerService;

    @JsonProperty("results")
    @XmlElementWrapper(name = "results")
    @XmlElement(name = "result")
    private List<ResultItem> resultItems;

    @XmlElement(name = "created_time")
    private long createdTime;
    @XmlElement(name = "elapsed_time")
    private long elapsedTime;

    public SearchResults() {
    }

    public SearchResults(Query query) {
        this.query = query;
        this.resultItems = new LinkedList<>();
        this.resultsPerService = new HashMap<>();
    }

    public void addConnectorResults(ConnectorSearchResults queryResult) {
        resultItems.addAll(queryResult.getResultItems());
        totalResults += queryResult.getTotalResultCount();
        resultsPerService.put(queryResult.getConnectorName(), queryResult.getTotalResultCount());
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

    public List<ResultItem> getResultItems() {
        return resultItems;
    }

    public void setResultItems(final List<ResultItem> resultItems) {
        this.resultItems = resultItems;
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
        return resultItems.size();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("query", query)
            .append("totalResults", totalResults)
            .append("resultsPerService", resultsPerService)
            .append("resultItems", resultItems)
            .append("createdTime", createdTime)
            .append("elapsedTime", elapsedTime)
            .toString();
    }
}
