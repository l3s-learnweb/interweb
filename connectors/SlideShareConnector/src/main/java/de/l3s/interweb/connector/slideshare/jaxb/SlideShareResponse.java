package de.l3s.interweb.connector.slideshare.jaxb;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

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
