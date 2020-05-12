package de.l3s.interwebj.connector.slideshare.jaxb;

import java.util.*;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "Tags")
@XmlAccessorType(XmlAccessType.FIELD)
public class TagsResponse
{

    @XmlElement(name = "Tag")
    protected List<TagEntity> tags;

    public List<TagEntity> getTags()
    {
	return tags;
    }

    public void setTags(List<TagEntity> tags)
    {
	this.tags = tags;
    }

}
