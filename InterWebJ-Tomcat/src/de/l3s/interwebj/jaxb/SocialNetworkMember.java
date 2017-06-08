package de.l3s.interwebj.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.l3s.interwebj.core.Environment;
import de.l3s.interwebj.query.ContactFromSocialNetwork;

@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.FIELD)
public class SocialNetworkMember
{

    @XmlElement(name = "service")
    protected String service;
    @XmlElement(name = "id_at_service")
    protected String idAtService;
    @XmlElement(name = "type")
    protected String type;
    @XmlElement(name = "name")
    protected String name;
    @XmlElement(name = "description")
    protected String description;
    @XmlElement(name = "url")
    protected String url;
    // TODO: Remove image element. Used only for the InterWeb compatibility
    @XmlElement(name = "image")
    protected String image;

    @XmlElement(name = "date")
    protected String date;
    @XmlElement(name = "tags")
    protected String tags;

    @XmlElement(name = "privacy_confidence")
    protected double privacyConfidence;

    public SocialNetworkMember()
    {
    }

    public SocialNetworkMember(ContactFromSocialNetwork contact)
    {
	this();

	if(contact == null)
	{
	    Environment.logger.severe("Result is null ");
	    return;
	}
	setService(contact.getSource());
	setIdAtService(contact.getUserId());
	setType("friend");
	setName(contact.getUsername());
	setDescription("test run");

	setUrl("test");

	setDate(null);
	setTags(null);

	this.privacyConfidence = contact.getHop();
    }

    public String getDate()
    {
	return date;
    }

    public String getDescription()
    {
	return description;
    }

    //	public String getEmbedded()
    //	{
    //		return embedded;
    //	}

    public String getIdAtService()
    {
	return idAtService;
    }

    public String getImage()
    {
	return image;
    }

    public String getService()
    {
	return service;
    }

    public String getTags()
    {
	return tags;
    }

    public String getName()
    {
	return name;
    }

    public String getType()
    {
	return type;
    }

    public String getUrl()
    {
	return url;
    }

    public void setDate(String date)
    {
	this.date = date;
    }

    public void setDescription(String description)
    {
	this.description = description;
    }

    //	public void setEmbedded(String embedded)
    //	{
    //		this.embedded = embedded;
    //	}

    public void setIdAtService(String idAtService)
    {
	this.idAtService = idAtService;
    }

    public void setImage(String image)
    {
	this.image = image;
    }

    public void setService(String service)
    {
	this.service = service;
    }

    public void setTags(String tags)
    {
	this.tags = tags;
    }

    public void setName(String title)
    {
	this.name = title;
    }

    public void setType(String type)
    {
	this.type = type;
    }

    public void setUrl(String url)
    {
	this.url = url;
    }

}
