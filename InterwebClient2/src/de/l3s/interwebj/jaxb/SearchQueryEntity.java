package de.l3s.interwebj.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "query")
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchQueryEntity
{

    @XmlAttribute(name = "id")
    protected String id;
    @XmlAttribute(name = "link")
    protected String link;
    @XmlElement(name = "user")
    protected String user;
    @XmlElement(name = "query_string")
    protected String queryString;
    @XmlElement(name = "search_in")
    protected String searchIn;
    @XmlElement(name = "media_types")
    protected String mediaTypes;
    @XmlElement(name = "date_from")
    protected String dateFrom;
    @XmlElement(name = "date_till")
    protected String dateTill;
    @XmlElement(name = "ranking")
    protected String ranking;
    @XmlElement(name = "number_of_results")
    protected int numberOfResults;
    @XmlElement(name = "updated")
    protected String updated;
    @XmlElement(name = "elapsed_time")
    protected String elapsedTime;
    @XmlElementWrapper(name = "results")
    @XmlElement(name = "result")
    protected List<SearchResultEntity> results;

    public SearchQueryEntity()
    {
        results = new ArrayList<SearchResultEntity>();
    }

    public void addResult(SearchResultEntity result)
    {
        results.add(result);
    }

    public String getDateFrom()
    {
        return dateFrom;
    }

    public String getDateTill()
    {
        return dateTill;
    }

    public String getElapsedTime()
    {
        return elapsedTime;
    }

    public String getId()
    {
        return id;
    }

    public String getLink()
    {
        return link;
    }

    public String getMediaTypes()
    {
        return mediaTypes;
    }

    public int getNumberOfResults()
    {
        return numberOfResults;
    }

    public String getQueryString()
    {
        return queryString;
    }

    public String getRanking()
    {
        return ranking;
    }

    public List<SearchResultEntity> getResults()
    {
        return results;
    }

    public String getSearchIn()
    {
        return searchIn;
    }

    public String getUpdated()
    {
        return updated;
    }

    public String getUser()
    {
        return user;
    }

    public void setDateFrom(String dateFrom)
    {
        this.dateFrom = dateFrom;
    }

    public void setDateTill(String dateTill)
    {
        this.dateTill = dateTill;
    }

    public void setElapsedTime(String elapsedTime)
    {
        this.elapsedTime = elapsedTime;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setLink(String link)
    {
        this.link = link;
    }

    public void setMediaTypes(String mediaTypes)
    {
        this.mediaTypes = mediaTypes;
    }

    public void setNumberOfResults(int numberOfResults)
    {
        this.numberOfResults = numberOfResults;
    }

    public void setQueryString(String queryString)
    {
        this.queryString = queryString;
    }

    public void setRanking(String ranking)
    {
        this.ranking = ranking;
    }

    public void setResults(List<SearchResultEntity> results)
    {
        this.results = results;
    }

    public void setSearchIn(String searchIn)
    {
        this.searchIn = searchIn;
    }

    public void setUpdated(String updated)
    {
        this.updated = updated;
    }

    public void setUser(String user)
    {
        this.user = user;
    }
}
