package de.l3s.interwebj.socialsearch;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class SocialSearchResult implements Serializable
{

    private static final long serialVersionUID = -2762679444319967129L;

    private SocialSearchQuery query;
    private List<SocialSearchResultItem> resultItems;
    private long elapsedTime;
    private long createdTime;
    private long totalResultCount;

    public SocialSearchResult(SocialSearchQuery query2)
    {
	this.query = query2;
	resultItems = new LinkedList<SocialSearchResultItem>();
    }

    public void addResult(SocialSearchResult result)
    {
	for(SocialSearchResultItem resultItem : result.resultItems)
	{
	    resultItems.add(resultItem);
	}
	totalResultCount = totalResultCount + result.getTotalResultCount();
    }

    public long getCreatedTime()
    {
	return createdTime;
    }

    public long getElapsedTime()
    {
	return elapsedTime;
    }

    public SocialSearchQuery getQuery()
    {
	return query;
    }

    public List<SocialSearchResultItem> getResultItems()
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

    }

    public void setElapsedTime(long elapsedTime)
    {
	this.elapsedTime = elapsedTime;
    }

    public void setTotalResultCount(long totalResultCount)
    {
	this.totalResultCount = totalResultCount;
    }

    public int size()
    {
	return resultItems.size();
    }

    public void addResultItem(SocialSearchResultItem item)
    {
	resultItems.add(item);

    }

    public void addSocialSearchResult()
    {

    }
}
