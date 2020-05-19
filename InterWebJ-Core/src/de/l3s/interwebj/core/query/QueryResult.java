package de.l3s.interwebj.core.query;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class QueryResult implements Serializable {
    private static final long serialVersionUID = -2762679444319967129L;

    private final Query query;
    private final List<ResultItem> resultItems;
    private final Map<String, Long> facetResults;

    private long elapsedTime;
    private long createdTime;
    private long totalResultCount = 0;

    public QueryResult(Query query) {
        this.query = query;
        this.resultItems = new LinkedList<>();
        this.facetResults = new HashMap<>();
    }

    public void addQueryResult(ConnectorResults queryResult) {
        resultItems.addAll(queryResult.getResultItems());
        totalResultCount += queryResult.getTotalResultCount();
        facetResults.put(queryResult.getConnectorName(), queryResult.getTotalResultCount());
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;

        if (query != null) {
            query.setUpdated(createdTime);
        }
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public Query getQuery() {
        return query;
    }

    public List<ResultItem> getResultItems() {
        return resultItems;
    }

    public Map<String, Long> getFacetResults() {
        return facetResults;
    }

    public long getTotalResultCount() {
        return totalResultCount;
    }

    public int size() {
        return resultItems.size();
    }
}
