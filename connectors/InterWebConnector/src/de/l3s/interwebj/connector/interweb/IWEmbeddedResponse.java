package de.l3s.interwebj.connector.interweb;


import javax.xml.bind.annotation.*;


@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class IWEmbeddedResponse
    extends IWXMLResponse
{
	
	@XmlElement(name = "embedded")
	protected String embedded;
	@XmlElement(name = "content_type")
	protected String contentType;
	@XmlElement(name = "media")
	protected String media;
	@XmlElement(name = "media_url")
	protected String mediaUrl;
	

	public IWEmbeddedResponse()
	{
	}
	

	public String getContentType()
	{
		return contentType;
	}
	

	public String getEmbedded()
	{
		return embedded;
	}
	

	public String getMedia()
	{
		return media;
	}
	

	public String getMediaUrl()
	{
		return mediaUrl;
	}
	

	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}
	

	public void setEmbedded(String embedded)
	{
		this.embedded = embedded;
	}
	

	public void setMedia(String media)
	{
		this.media = media;
	}
	

	public void setMediaUrl(String mediaUrl)
	{
		this.mediaUrl = mediaUrl;
	}
}
