package de.l3s.interwebj.core.connector;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.ResultItem;

public class ConnectorSearchResults implements Serializable {
    private static final long serialVersionUID = -2762679444319967129L;

    private final Query query;
    private final String connectorName;
    private final List<ResultItem> resultItems;

    private long totalResultCount = 0;

    public ConnectorSearchResults(Query query, String connectorName) {
        this.query = query;
        this.connectorName = connectorName;
        this.resultItems = new LinkedList<>();
    }

    public void addResultItem(ResultItem resultItem) {
        resultItems.add(resultItem);
    }

    public Query getQuery() {
        return query;
    }

    public String getConnectorName() {
        return connectorName;
    }

    public List<ResultItem> getResultItems() {
        return resultItems;
    }

    public long getTotalResultCount() {
        return totalResultCount;
    }

    public void setTotalResultCount(long totalResultCount) {
        this.totalResultCount = totalResultCount;
    }

    public void addTotalResultCount(long totalResultCount) {
        this.totalResultCount += totalResultCount;
    }

    public int size() {
        return resultItems.size();
    }
}
