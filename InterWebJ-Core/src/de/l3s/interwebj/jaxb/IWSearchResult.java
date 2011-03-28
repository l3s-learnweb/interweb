package de.l3s.interwebj.jaxb;


import javax.xml.bind.annotation.*;


@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.FIELD)
public class IWSearchResult
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
	@XmlElement(name = "image")
	protected String image;
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
	

	public String getImage()
	{
		return image;
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
	

	public void setImage(String image)
	{
		this.image = image;
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
	

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("IWSearchResult [");
		if (service != null)
		{
			builder.append("service=");
			builder.append(service);
			builder.append(", ");
		}
		if (idAtService != null)
		{
			builder.append("idAtService=");
			builder.append(idAtService);
			builder.append(", ");
		}
		if (type != null)
		{
			builder.append("type=");
			builder.append(type);
			builder.append(", ");
		}
		if (title != null)
		{
			builder.append("title=");
			builder.append(title);
			builder.append(", ");
		}
		if (description != null)
		{
			builder.append("description=");
			builder.append(description);
			builder.append(", ");
		}
		if (url != null)
		{
			builder.append("url=");
			builder.append(url);
			builder.append(", ");
		}
		if (image != null)
		{
			builder.append("image=");
			builder.append(image);
			builder.append(", ");
		}
		if (date != null)
		{
			builder.append("date=");
			builder.append(date);
			builder.append(", ");
		}
		if (tags != null)
		{
			builder.append("tags=");
			builder.append(tags);
			builder.append(", ");
		}
		builder.append("rankAtService=");
		builder.append(rankAtService);
		builder.append(", totalResultsAtService=");
		builder.append(totalResultsAtService);
		builder.append(", numberOfViews=");
		builder.append(numberOfViews);
		builder.append(", numberOfComments=");
		builder.append(numberOfComments);
		builder.append("]");
		return builder.toString();
	}
}
