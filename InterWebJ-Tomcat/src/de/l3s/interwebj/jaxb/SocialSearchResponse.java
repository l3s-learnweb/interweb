package de.l3s.interwebj.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.l3s.interwebj.socialsearch.SocialSearchResult;
import de.l3s.interwebj.socialsearch.SocialSearchResultItem;

@XmlRootElement(name = "socialsearchresponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class SocialSearchResponse extends XMLResponse
{

    @XmlElement(name = "query")
    protected SocialSearchQueryEntity query;

    public SocialSearchResponse()
    {
    }

    public SocialSearchResponse(SocialSearchResult queryResult)
    {
	SocialSearchQueryEntity searchQueryEntity = new SocialSearchQueryEntity(queryResult.getQuery());

	for(SocialSearchResultItem resultItem : queryResult.getResultItems())
	{
	    SocialSearchResultEntity iwSearchResult = new SocialSearchResultEntity(resultItem);

	    searchQueryEntity.addResult(iwSearchResult);
	}
	query = searchQueryEntity;
    }

    public SocialSearchQueryEntity getQuery()
    {
	return query;
    }

    public void setQuery(SocialSearchQueryEntity query)
    {
	this.query = query;
    }
}
