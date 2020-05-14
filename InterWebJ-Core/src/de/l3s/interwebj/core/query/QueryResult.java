package de.l3s.interwebj.core.query;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class QueryResult implements Serializable
{
    private static final long serialVersionUID = -2762679444319967129L;

    private final Query query;
    private final List<ResultItem> resultItems;
    private long elapsedTime;
    private long createdTime;
    private long totalResultCount = 0;

    public QueryResult(Query query)
    {
        this.query = query;
        this.resultItems = new LinkedList<>();
    }

    public void addQueryResult(QueryResult queryResult)
    {
        resultItems.addAll(queryResult.resultItems);
        addTotalResultCount(queryResult.getTotalResultCount());
    }

    public void addResultItem(ResultItem resultItem)
    {
	    resultItems.add(resultItem);
    }

    public long getCreatedTime()
    {
	    return createdTime;
    }

    public long getElapsedTime()
    {
	    return elapsedTime;
    }

    public Query getQuery()
    {
	    return query;
    }

    public List<ResultItem> getResultItems()
    {
	    return resultItems;
    }

    public long getTotalResultCount()
    {
	    return totalResultCount;
    }

    public void setCreatedTime(long createdTime)
    {
        this.createdTime = createdTime;

        if (query != null)
            query.setUpdated(createdTime);
    }

    public void setElapsedTime(long elapsedTime)
    {
	    this.elapsedTime = elapsedTime;
    }

    public void addTotalResultCount(long totalResultCount)
    {
	    this.totalResultCount += totalResultCount;
    }

    public void setTotalResultCount(long totalResultCount)
    {
	    this.totalResultCount = totalResultCount;
    }

    public int size()
    {
	    return resultItems.size();
    }
}
