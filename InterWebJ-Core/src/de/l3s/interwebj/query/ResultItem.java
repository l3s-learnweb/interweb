package de.l3s.interwebj.query;


import java.io.*;

import org.apache.commons.lang.*;

import de.l3s.interwebj.jaxb.*;


public abstract class ResultItem
    implements Serializable
{
	
	private static final int MAX_TITLE_LENGTH = 256;
	private static final int MAX_DESCRIPTION_LENGTH = 1024;
	
	private static final long serialVersionUID = 9111067008513145675L;
	
	private String id;
	private String title;
	private String type;
	private String description;
	private String connectorName;
	private String serviceName;
	private String tags;
	private String url;
	private String imageUrl;
	private String date;
	private int rank;
	private int totalResultCount;
	private int viewCount;
	private int commentCount;
	

	public ResultItem(String connectorName)
	{
		this.connectorName = connectorName;
	}
	

	abstract String asHtml();
	

	public IWSearchResult createIWSearchResult()
	{
		IWSearchResult iwSearchResult = new IWSearchResult();
		iwSearchResult.setService(serviceName);
		iwSearchResult.setIdAtService(id);
		iwSearchResult.setType(type);
		iwSearchResult.setTitle(title);
		iwSearchResult.setDescription(description);
		iwSearchResult.setUrl(url);
		iwSearchResult.setImage(imageUrl);
		iwSearchResult.setDate(date);
		iwSearchResult.setTags(tags);
		iwSearchResult.setRankAtService(rank);
		iwSearchResult.setTotalResultsAtService(totalResultCount);
		iwSearchResult.setViews(viewCount);
		iwSearchResult.setNumberOfComments(commentCount);
		return iwSearchResult;
	}
	

	public int getCommentCount()
	{
		return commentCount;
	}
	

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
	

	public String getId()
	{
		return id;
	}
	

	public String getImageUrl()
	{
		return imageUrl;
	}
	

	public int getRank()
	{
		return rank;
	}
	

	public String getServiceName()
	{
		return serviceName;
	}
	

	public String getTags()
	{
		return tags;
	}
	

	public String getTitle()
	{
		return title;
	}
	

	public int getTotalResultCount()
	{
		return totalResultCount;
	}
	

	public String getType()
	{
		return type;
	}
	

	public String getUrl()
	{
		return url;
	}
	

	public int getViewCount()
	{
		return viewCount;
	}
	

	public void setCommentCount(int commentCount)
	{
		this.commentCount = commentCount;
	}
	

	public void setDate(String date)
	{
		this.date = date;
	}
	

	public void setDescription(String description)
	{
		if (description == null)
		{
			return;
		}
		description = description.trim();
		description = unescape(description);
		if (description.length() > MAX_DESCRIPTION_LENGTH)
		{
			int cutIndex = description.lastIndexOf(' ', MAX_DESCRIPTION_LENGTH);
			if (cutIndex == -1)
			{
				cutIndex = MAX_DESCRIPTION_LENGTH;
			}
			description = description.substring(0, cutIndex) + "...";
		}
		this.description = description;
	}
	

	public void setId(String id)
	{
		this.id = id;
	}
	

	public void setImageUrl(String imageUrl)
	{
		this.imageUrl = imageUrl;
	}
	

	public void setRank(int rank)
	{
		this.rank = rank;
	}
	

	public void setServiceName(String serviceName)
	{
		this.serviceName = serviceName;
	}
	

	public void setTags(String tags)
	{
		this.tags = tags;
	}
	

	public void setTitle(String title)
	{
		if (title == null)
		{
			return;
		}
		title = title.trim();
		description = unescape(description);
		if (title.length() > MAX_TITLE_LENGTH)
		{
			int cutIndex = title.lastIndexOf(' ', MAX_TITLE_LENGTH);
			if (cutIndex == -1)
			{
				cutIndex = MAX_TITLE_LENGTH;
			}
			title = title.substring(0, cutIndex) + "...";
		}
		this.title = title;
	}
	

	public void setTotalResultCount(int totalResultCount)
	{
		this.totalResultCount = totalResultCount;
	}
	

	public void setType(String type)
	{
		this.type = type;
	}
	

	public void setUrl(String url)
	{
		this.url = url;
	}
	

	public void setViewCount(int viewCount)
	{
		this.viewCount = viewCount;
	}
	

	private String unescape(String encoded)
	{
		if (encoded == null)
		{
			return null;
		}
		String decoded;
		while (!(decoded = StringEscapeUtils.unescapeHtml(encoded)).equals(encoded))
		{
			encoded = decoded;
		}
		return decoded;
	}
	
}
