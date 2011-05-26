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
		SearchQueryEntity searchQueryEntity = new SearchQueryEntity(queryResult.getQuery());
		searchQueryEntity.setElapsedTime(String.valueOf(queryResult.getElapsedTime()));
		for (ResultItem resultItem : queryResult.getResultItems())
		{
			SearchResultEntity iwSearchResult = new SearchResultEntity(resultItem);
			searchQueryEntity.addResult(iwSearchResult);
		}
		query = searchQueryEntity;
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
