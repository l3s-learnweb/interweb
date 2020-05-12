package de.l3s.interwebj.connector.slideshare;

import java.util.*;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "Slideshows")
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchResponse
{

    @XmlElement(name = "Meta")
    protected MetaEntity meta;
    @XmlElement(name = "Slideshow")
    protected List<SearchResultEntity> searchResults;

    public MetaEntity getMeta()
    {
	return meta;
    }

    public List<SearchResultEntity> getSearchResults()
    {
	return searchResults;
    }

    public void setMeta(MetaEntity meta)
    {
	this.meta = meta;
    }

    public void setSearchResults(List<SearchResultEntity> searchResults)
    {
	this.searchResults = searchResults;
    }

}
