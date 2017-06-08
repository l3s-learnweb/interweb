package de.l3s.interwebj.connector.slideshare;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "Meta")
@XmlAccessorType(XmlAccessType.FIELD)
public class MetaEntity
{

    @XmlElement(name = "Query")
    protected String query;
    @XmlElement(name = "ResultOffset")
    protected int resultOffset;
    @XmlElement(name = "NumResults")
    protected int numResults;
    @XmlElement(name = "TotalResults")
    protected int totalResults;

    public int getNumResults()
    {
	return numResults;
    }

    public String getQuery()
    {
	return query;
    }

    public int getResultOffset()
    {
	return resultOffset;
    }

    public int getTotalResults()
    {
	return totalResults;
    }

    public void setNumResults(int numResults)
    {
	this.numResults = numResults;
    }

    public void setQuery(String query)
    {
	this.query = query;
    }

    public void setResultOffset(int resultOffset)
    {
	this.resultOffset = resultOffset;
    }

    public void setTotalResults(int totalResults)
    {
	this.totalResults = totalResults;
    }

}
