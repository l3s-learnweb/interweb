package de.l3s.interwebj.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchResponse extends XMLResponse
{

    @XmlElement(name = "query")
    protected SearchQueryEntity query;

    public SearchResponse()
    {
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
