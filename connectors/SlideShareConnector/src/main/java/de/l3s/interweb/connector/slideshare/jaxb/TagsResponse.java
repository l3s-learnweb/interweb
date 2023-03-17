package de.l3s.interweb.connector.slideshare.jaxb;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Tags")
@XmlAccessorType(XmlAccessType.FIELD)
public class TagsResponse {

    @XmlElement(name = "Tag")
    protected List<TagEntity> tags;

    public List<TagEntity> getTags() {
        return tags;
    }

    public void setTags(List<TagEntity> tags) {
        this.tags = tags;
    }

}
