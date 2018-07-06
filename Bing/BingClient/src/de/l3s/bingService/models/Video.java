package de.l3s.bingService.models;

import java.util.List;

public class Video
{

    private Media media;

    private String description;

    private List<Publisher> publisher;

    private String duration;

    private String motionThumbnailUrl;

    private String embedHtml;

    private Boolean allowHttpsEmbed;

    private String viewCount;

    public String getDescription()
    {
	return description;
    }

    public void setDescription(String description)
    {
	this.description = description;
    }

    public String getDuration()
    {
	return duration;
    }

    public void setDuration(String duration)
    {
	this.duration = duration;
    }

    public String getMotionThumbnailUrl()
    {
	return motionThumbnailUrl;
    }

    public void setMotionThumbnailUrl(String motionThumbnailUrl)
    {
	this.motionThumbnailUrl = motionThumbnailUrl;
    }

    public String getEmbedHtml()
    {
	return embedHtml;
    }

    public void setEmbedHtml(String embedHtml)
    {
	this.embedHtml = embedHtml;
    }

    public Boolean getAllowHttpsEmbed()
    {
	return allowHttpsEmbed;
    }

    public void setAllowHttpsEmbed(Boolean allowHttpsEmbed)
    {
	this.allowHttpsEmbed = allowHttpsEmbed;
    }

    public String getViewCount()
    {
	return viewCount;
    }

    public void setViewCount(String viewCount)
    {
	this.viewCount = viewCount;
    }

    public List<Publisher> getPublisher()
    {
	return publisher;
    }

    public void setPublisher(List<Publisher> publisher)
    {
	this.publisher = publisher;
    }

    public Media getMedia()
    {
	return media;
    }

    public void setMedia(Media media)
    {
	this.media = media;
    }

}
