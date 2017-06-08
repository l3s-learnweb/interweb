package de.l3s.interwebj.jaxb;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.lucene.document.Document;

import de.l3s.interwebj.core.Environment;
import de.l3s.interwebj.query.ResultItem;
import de.l3s.interwebj.query.Thumbnail;
import de.l3s.interwebj.socialsearch.SocialSearchResultItem;

@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.FIELD)
public class SocialSearchResultEntity
{
    /*
    private String username;
    private String userid;
    private String imageurl;
    private ArrayList<Document> resultitems;
    private String story;
    private String reason;
    
    private ArrayList<String> embedhtmlofphotos;
    private ArrayList<String> embedhtmlofvideos;*/

    @XmlElement(name = "username")
    protected String username;
    @XmlElement(name = "id")
    protected String userid;
    @XmlElement(name = "profilepicurl")
    protected String imageurl;
    @XmlElement(name = "story")
    protected String story;
    @XmlElement(name = "reason")
    protected String reason;

    @XmlElementWrapper(name = "resultdocs")
    @XmlElement(name = "doc")
    protected List<Document> resultItems;
    @XmlElementWrapper(name = "embedhtmlphotos")
    @XmlElement(name = "photo")
    protected List<String> photos;
    @XmlElementWrapper(name = "embedhtmlvideos")
    @XmlElement(name = "video")
    protected List<String> videos;

    public SocialSearchResultEntity()
    {
    }

    public SocialSearchResultEntity(SocialSearchResultItem resultItem)
    {
	setUsername(resultItem.getUsername());
	setImageurl(resultItem.getImageurl());
	setPhotos(resultItem.getEmbedhtmlofphotos());
	setReason(resultItem.getReason());
	setResultItems(resultItem.getResultitems());
	setStory(resultItem.getStory());
	setUserid(resultItem.getUserid());
	setVideos(resultItem.getEmbedhtmlofvideos());
    }

    public String getUsername()
    {
	return username;
    }

    public void setUsername(String username)
    {
	this.username = username;
    }

    public String getUserid()
    {
	return userid;
    }

    public void setUserid(String userid)
    {
	this.userid = userid;
    }

    public String getImageurl()
    {
	return imageurl;
    }

    public void setImageurl(String imageurl)
    {
	this.imageurl = imageurl;
    }

    public String getStory()
    {
	return story;
    }

    public void setStory(String story)
    {
	this.story = story;
    }

    public String getReason()
    {
	return reason;
    }

    public void setReason(String reason)
    {
	this.reason = reason;
    }

    public List<Document> getResultItems()
    {
	return resultItems;
    }

    public void setResultItems(List<Document> resultItems)
    {
	this.resultItems = resultItems;
    }

    public List<String> getPhotos()
    {
	return photos;
    }

    public void setPhotos(List<String> photos)
    {
	this.photos = photos;
    }

    public List<String> getVideos()
    {
	return videos;
    }

    public void setVideos(List<String> videos)
    {
	this.videos = videos;
    }

}
