package de.l3s.interwebj.jaxb;


import java.util.*;

import javax.xml.bind.annotation.*;

import de.l3s.interwebj.query.*;


@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchResultEntity
{
	
	@XmlElement(name = "service")
	protected String service;
	@XmlElement(name = "id_at_service")
	protected String idAtService;
	@XmlElement(name = "type")
	protected String type;
	@XmlElement(name = "title")
	protected String title;
	@XmlElement(name = "description")
	protected String description;
	@XmlElement(name = "url")
	protected String url;
	@XmlElementWrapper(name = "thumbnails")
	@XmlElement(name = "thumbnail")
	protected List<ThumbnailEntity> thumbnailEntities;
	@XmlElement(name = "date")
	protected String date;
	@XmlElement(name = "tags")
	protected String tags;
	@XmlElement(name = "rank_at_service")
	protected int rankAtService;
	@XmlElement(name = "total_results_at_service")
	protected int totalResultsAtService;
	@XmlElement(name = "views")
	protected int numberOfViews;
	@XmlElement(name = "number_of_comments")
	protected int numberOfComments;
	

	public SearchResultEntity()
	{
	}
	

	public SearchResultEntity(ResultItem resultItem)
	{
		this();
		setService(resultItem.getServiceName());
		setIdAtService(resultItem.getId());
		setType(resultItem.getType());
		setTitle(resultItem.getTitle());
		setDescription(resultItem.getDescription());
		setUrl(resultItem.getUrl());
		Set<Thumbnail> thumbnails = resultItem.getThumbnails();
		List<ThumbnailEntity> thumbnailEntities = new ArrayList<ThumbnailEntity>();
		for (Thumbnail thumbnail : thumbnails)
		{
			thumbnailEntities.add(new ThumbnailEntity(thumbnail));
		}
		setThumbnailEntities(thumbnailEntities);
		setDate(resultItem.getDate());
		setTags(resultItem.getTags());
		setRankAtService(resultItem.getRank());
		setTotalResultsAtService(resultItem.getTotalResultCount());
		setViews(resultItem.getViewCount());
		setNumberOfComments(resultItem.getCommentCount());
	}
	

	public String getDate()
	{
		return date;
	}
	

	public String getDescription()
	{
		return description;
	}
	

	public String getIdAtService()
	{
		return idAtService;
	}
	

	public int getNumberOfComments()
	{
		return numberOfComments;
	}
	

	public int getNumberOfViews()
	{
		return numberOfViews;
	}
	

	public int getRankAtService()
	{
		return rankAtService;
	}
	

	public String getService()
	{
		return service;
	}
	

	public String getTags()
	{
		return tags;
	}
	

	public List<ThumbnailEntity> getThumbnailEntities()
	{
		return thumbnailEntities;
	}
	

	public String getTitle()
	{
		return title;
	}
	

	public int getTotalResultsAtService()
	{
		return totalResultsAtService;
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
	

	public void setIdAtService(String idAtService)
	{
		this.idAtService = idAtService;
	}
	

	public void setNumberOfComments(int numberOfComments)
	{
		this.numberOfComments = numberOfComments;
	}
	

	public void setRankAtService(int rankAtService)
	{
		this.rankAtService = rankAtService;
	}
	

	public void setService(String service)
	{
		this.service = service;
	}
	

	public void setTags(String tags)
	{
		this.tags = tags;
	}
	

	public void setThumbnailEntities(List<ThumbnailEntity> thumbnailEntities)
	{
		this.thumbnailEntities = thumbnailEntities;
	}
	

	public void setTitle(String title)
	{
		this.title = title;
	}
	

	public void setTotalResultsAtService(int totalResultsAtService)
	{
		this.totalResultsAtService = totalResultsAtService;
	}
	

	public void setType(String type)
	{
		this.type = type;
	}
	

	public void setUrl(String url)
	{
		this.url = url;
	}
	

	public void setViews(int numberOfViews)
	{
		this.numberOfViews = numberOfViews;
	}
}
