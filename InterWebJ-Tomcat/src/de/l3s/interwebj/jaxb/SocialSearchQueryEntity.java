package de.l3s.interwebj.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.l3s.interwebj.socialsearch.SocialSearchQuery;

@XmlRootElement(name = "query")
@XmlAccessorType(XmlAccessType.FIELD)
public class SocialSearchQueryEntity
{

    @XmlAttribute(name = "id")
    protected String userid;

    public String getUserId()
    {
	return userid;
    }

    public void setUserId(String id)
    {
	this.userid = id;
    }

    public String getQuery()
    {
	return query;
    }

    public void setQuery(String query)
    {
	this.query = query;
    }

    public List<SocialSearchResultEntity> getResults()
    {
	return results;
    }

    public void setResults(List<SocialSearchResultEntity> results)
    {
	this.results = results;
    }

    @XmlAttribute(name = "query")
    protected String query;
    @XmlElementWrapper(name = "results")
    @XmlElement(name = "result")
    protected List<SocialSearchResultEntity> results;

    public SocialSearchQueryEntity()
    {
	results = new ArrayList<SocialSearchResultEntity>();
    }

    public SocialSearchQueryEntity(SocialSearchQuery q)
    {
	this();
	userid = "me";
	query = q.getQuery();

    }

    public void addResult(SocialSearchResultEntity result)
    {
	results.add(result);
    }

}
