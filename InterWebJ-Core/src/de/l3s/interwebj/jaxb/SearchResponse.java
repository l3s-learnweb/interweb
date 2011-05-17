package de.l3s.interwebj.jaxb;


import javax.xml.bind.annotation.*;

import de.l3s.interwebj.query.*;


@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchResponse
    extends XMLResponse
{
	
	@XmlElement(name = "query")
	protected SearchQueryEntity query;
	

	public SearchResponse()
	{
	}
	

	public SearchResponse(QueryResult queryResult)
	{
		SearchQueryEntity iwSearchQuery = new SearchQueryEntity(queryResult.getQuery());
		iwSearchQuery.setElapsedTime(String.valueOf(queryResult.getElapsedTime()));
		for (ResultItem resultItem : queryResult.getResultItems())
		{
			SearchResultEntity iwSearchResult = new SearchResultEntity(resultItem);
			iwSearchQuery.addResult(iwSearchResult);
		}
		query = iwSearchQuery;
	}
	

	public SearchQueryEntity getQuery()
	{
		return query;
	}
	

	public void setQuery(SearchQueryEntity query)
	{
		this.query = query;
	}
}
