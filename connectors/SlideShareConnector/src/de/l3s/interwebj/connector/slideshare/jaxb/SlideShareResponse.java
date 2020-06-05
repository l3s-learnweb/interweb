package de.l3s.interwebj.connector.slideshare.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Slideshows")
@XmlAccessorType(XmlAccessType.FIELD)
public class SlideShareResponse {

    @XmlElement(name = "Meta")
    protected MetaEntity meta;
    @XmlElement(name = "Slideshow")
    protected List<SearchResultEntity> searchResults;

    public MetaEntity getMeta() {
        return meta;
    }

    public void setMeta(MetaEntity meta) {
        this.meta = meta;
    }

    public List<SearchResultEntity> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<SearchResultEntity> searchResults) {
        this.searchResults = searchResults;
    }

}
