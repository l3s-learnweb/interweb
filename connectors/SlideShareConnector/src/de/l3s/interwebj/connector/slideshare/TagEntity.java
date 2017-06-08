package de.l3s.interwebj.connector.slideshare;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "Tag")
@XmlAccessorType(XmlAccessType.FIELD)
public class TagEntity
{

    @XmlAttribute(name = "Count")
    protected int count;
    @XmlValue
    protected String tag;

    public int getCount()
    {
	return count;
    }

    public String getTag()
    {
	return tag;
    }

    public void setCount(int count)
    {
	this.count = count;
    }

    public void setTag(String tag)
    {
	this.tag = tag;
    }
}
