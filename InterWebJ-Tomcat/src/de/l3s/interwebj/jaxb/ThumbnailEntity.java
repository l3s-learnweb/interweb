package de.l3s.interwebj.jaxb;


import javax.xml.bind.annotation.*;

import de.l3s.interwebj.query.*;


@XmlRootElement(name = "thumbnail")
@XmlAccessorType(XmlAccessType.FIELD)
public class ThumbnailEntity
{
	
	@XmlAttribute(name = "width")
	protected int width;
	@XmlAttribute(name = "height")
	protected int height;
	@XmlValue
	protected String url;
	

	public ThumbnailEntity()
	{
	}
	

	public ThumbnailEntity(String url, int width, int height)
	{
		this.url = url;
		this.width = width;
		this.height = height;
	}
	

	public ThumbnailEntity(Thumbnail thumbnail)
	{
		url = thumbnail.getUrl();
		width = thumbnail.getWidth();
		height = thumbnail.getHeight();
	}
	

	public int getHeight()
	{
		return height;
	}
	

	public String getUrl()
	{
		return url;
	}
	

	public int getWidth()
	{
		return width;
	}
	

	public void setHeight(int height)
	{
		this.height = height;
	}
	

	public void setUrl(String url)
	{
		this.url = url;
	}
	

	public void setWidth(int width)
	{
		this.width = width;
	}
	
}
