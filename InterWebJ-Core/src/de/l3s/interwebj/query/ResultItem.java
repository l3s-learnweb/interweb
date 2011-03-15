package de.l3s.interwebj.query;


import java.io.*;


public abstract class ResultItem
    implements Serializable
{
	
	private static final long serialVersionUID = 9111067008513145675L;
	
	private String title;
	private String type;
	private String description;
	private String connectorName;
	private String tags;
	private String url;
	private String previewUrl;
	private String date;
	

	public ResultItem(String connectorName)
	{
		this.connectorName = connectorName;
	}
	

	abstract String asHtml();
	

	public String getConnectorName()
	{
		return connectorName;
	}
	

	public String getDate()
	{
		return date;
	}
	

	public String getDescription()
	{
		return description;
	}
	

	public String getPreviewUrl()
	{
		return previewUrl;
	}
	

	public String getTags()
	{
		return tags;
	}
	

	public String getTitle()
	{
		return title;
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
	

	public void setPreviewUrl(String previewUrl)
	{
		this.previewUrl = previewUrl;
	}
	

	public void setTags(String tags)
	{
		this.tags = tags;
	}
	

	public void setTitle(String title)
	{
		this.title = title;
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
