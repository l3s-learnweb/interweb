package de.l3s.interwebj.query;


import java.io.*;
import java.util.*;

import de.l3s.interwebj.jaxb.*;


public class QueryResult
    implements Serializable
{
	
	private static final long serialVersionUID = -2762679444319967129L;
	
	private Query query;
	private List<ResultItem> resultItems;
	private long elapsedTime;
	

	public QueryResult(Query query)
	{
		this.query = query;
		resultItems = new LinkedList<ResultItem>();
	}
	

	public void addQueryResult(QueryResult queryResult)
	{
		for (ResultItem resultItem : queryResult.resultItems)
		{
			resultItems.add(resultItem);
		}
	}
	

	public void addResultItem(ResultItem resultItem)
	{
		resultItems.add(resultItem);
	}
	

	public IWSearchResponse createIWSearchResponse()
	{
		IWSearchQuery iwSearchQuery = query.createIWSearchQuery();
		iwSearchQuery.setElapsedTime(String.valueOf(elapsedTime));
		for (ResultItem resultItem : resultItems)
		{
			IWSearchResult iwSearchResult = resultItem.createIWSearchResult();
			iwSearchQuery.addResult(iwSearchResult);
		}
		return new IWSearchResponse(iwSearchQuery);
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
	

	public void setElapsedTime(long elapsedTime)
	{
		this.elapsedTime = elapsedTime;
	}
	

	public int size()
	{
		return resultItems.size();
	}
}
