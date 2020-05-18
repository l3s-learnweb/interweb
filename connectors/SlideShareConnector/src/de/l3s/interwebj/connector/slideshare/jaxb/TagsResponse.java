package de.l3s.interwebj.connector.slideshare.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
