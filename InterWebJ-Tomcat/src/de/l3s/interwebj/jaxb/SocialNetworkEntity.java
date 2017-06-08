package de.l3s.interwebj.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "usersocialnetwork")
@XmlAccessorType(XmlAccessType.FIELD)
public class SocialNetworkEntity
{

    @XmlAttribute(name = "id")
    protected String id;

    @XmlElement(name = "user")
    protected String user;

    @XmlElement(name = "number_of_contacts")
    protected int numberOfontacts;
    @XmlElement(name = "updated")
    protected String updated;
    @XmlElement(name = "elapsed_time")
    protected String elapsedTime;
    @XmlElementWrapper(name = "contacts")
    @XmlElement(name = "contact")
    protected List<SocialNetworkMember> results;

    public SocialNetworkEntity()
    {
	results = new ArrayList<SocialNetworkMember>();
    }

    public SocialNetworkEntity(String userid)
    {
	this();
	this.user = userid;

    }

    public String getElapsedTime()
    {
	return elapsedTime;
    }

    public String getId()
    {
	return id;
    }

    public int getNumberOfResults()
    {
	return numberOfontacts;
    }

    public List<SocialNetworkMember> getResults()
    {
	return results;
    }

    public String getUpdated()
    {
	return updated;
    }

    public String getUser()
    {
	return user;
    }

    public void setElapsedTime(String elapsedTime)
    {
	this.elapsedTime = elapsedTime;
    }

    public void setId(String id)
    {
	this.id = id;
    }

    public void setNumberOfResults(int numberOfResults)
    {
	this.numberOfontacts = numberOfResults;
    }

    public void setUpdated(String updated)
    {
	this.updated = updated;
    }

    public void setUser(String user)
    {
	this.user = user;
    }
}
