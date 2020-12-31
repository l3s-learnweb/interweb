package de.l3s.interwebj.connector.slideshare.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Meta")
@XmlAccessorType(XmlAccessType.FIELD)
public class MetaEntity {

    @XmlElement(name = "Query")
    protected String query;
    @XmlElement(name = "ResultOffset")
    protected int resultOffset;
    @XmlElement(name = "NumResults")
    protected int numResults;
    @XmlElement(name = "TotalResults")
    protected int totalResults;

    public int getNumResults() {
        return numResults;
    }

    public void setNumResults(int numResults) {
        this.numResults = numResults;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getResultOffset() {
        return resultOffset;
    }

    public void setResultOffset(int resultOffset) {
        this.resultOffset = resultOffset;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

}
