package de.l3s.interwebj.jaxb;


import java.util.*;

import javax.xml.bind.annotation.*;

import de.l3s.interwebj.query.*;
import de.l3s.interwebj.util.*;


@XmlRootElement(name = "query")
@XmlAccessorType(XmlAccessType.FIELD)
public class IWSearchQuery
{
	
	@XmlAttribute(name = "id")
	protected String id;
	@XmlAttribute(name = "link")
	protected String link;
	@XmlElement(name = "user")
	protected String user;
	@XmlElement(name = "query_string")
	protected String queryString;
	@XmlElement(name = "search_in")
	protected String searchIn;
	@XmlElement(name = "media_types")
	protected String mediaTypes;
	@XmlElement(name = "date_from")
	protected String dateFrom;
	@XmlElement(name = "date_till")
	protected String dateTill;
	@XmlElement(name = "ranking")
	protected String ranking;
	@XmlElement(name = "number_of_results")
	protected int numberOfResults;
	@XmlElement(name = "updated")
	protected String updated;
	@XmlElement(name = "elapsed_time")
	protected String elapsedTime;
	
	@XmlElementWrapper(name = "results")
	@XmlElement(name = "result")
	protected List<IWSearchResult> results;
	

	public IWSearchQuery()
	{
		results = new ArrayList<IWSearchResult>();
	}
	

	public IWSearchQuery(Query query)
	{
		this();
		setId(query.getId());
		setLink(query.getLink());
		setQueryString(query.getQuery());
		setSearchIn(CoreUtils.convertToString(query.getSearchScopes()).toLowerCase());
		setMediaTypes(CoreUtils.convertToString(query.getContentTypes()));
		setDateFrom(query.getParam("date_from"));
		setDateTill(query.getParam("date_till"));
		setRanking(query.getSortOrder().getName());
		setNumberOfResults(query.getResultCount());
		setUpdated(CoreUtils.formatDate(query.getUpdated()));
	}
	

	public void addResult(IWSearchResult result)
	{
		results.add(result);
	}
	

	public String getDateFrom()
	{
		return dateFrom;
	}
	

	public String getDateTill()
	{
		return dateTill;
	}
	

	public String getElapsedTime()
	{
		return elapsedTime;
	}
	

	public String getId()
	{
		return id;
	}
	

	public String getLink()
	{
		return link;
	}
	

	public String getMediaTypes()
	{
		return mediaTypes;
	}
	

	public int getNumberOfResults()
	{
		return numberOfResults;
	}
	

	public String getQueryString()
	{
		return queryString;
	}
	

	public String getRanking()
	{
		return ranking;
	}
	

	public List<IWSearchResult> getResults()
	{
		return results;
	}
	

	public String getSearchIn()
	{
		return searchIn;
	}
	

	public String getUpdated()
	{
		return updated;
	}
	

	public String getUser()
	{
		return user;
	}
	

	public void setDateFrom(String dateFrom)
	{
		this.dateFrom = dateFrom;
	}
	

	public void setDateTill(String dateTill)
	{
		this.dateTill = dateTill;
	}
	

	public void setElapsedTime(String elapsedTime)
	{
		this.elapsedTime = elapsedTime;
	}
	

	public void setId(String id)
	{
		this.id = id;
	}
	

	public void setLink(String link)
	{
		this.link = link;
	}
	

	public void setMediaTypes(String mediaTypes)
	{
		this.mediaTypes = mediaTypes;
	}
	

	public void setNumberOfResults(int numberOfResults)
	{
		this.numberOfResults = numberOfResults;
	}
	

	public void setQueryString(String queryString)
	{
		this.queryString = queryString;
	}
	

	public void setRanking(String ranking)
	{
		this.ranking = ranking;
	}
	

	public void setResults(List<IWSearchResult> results)
	{
		this.results = results;
	}
	

	public void setSearchIn(String searchIn)
	{
		this.searchIn = searchIn;
	}
	

	public void setUpdated(String updated)
	{
		this.updated = updated;
	}
	

	public void setUser(String user)
	{
		this.user = user;
	}
	

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("IWSearchQuery [");
		if (id != null)
		{
			builder.append("id=");
			builder.append(id);
			builder.append(", ");
		}
		if (link != null)
		{
			builder.append("link=");
			builder.append(link);
			builder.append(", ");
		}
		if (user != null)
		{
			builder.append("user=");
			builder.append(user);
			builder.append(", ");
		}
		if (queryString != null)
		{
			builder.append("queryString=");
			builder.append(queryString);
			builder.append(", ");
		}
		if (searchIn != null)
		{
			builder.append("searchIn=");
			builder.append(searchIn);
			builder.append(", ");
		}
		if (mediaTypes != null)
		{
			builder.append("mediaTypes=");
			builder.append(mediaTypes);
			builder.append(", ");
		}
		if (dateFrom != null)
		{
			builder.append("dateFrom=");
			builder.append(dateFrom);
			builder.append(", ");
		}
		if (dateTill != null)
		{
			builder.append("dateTill=");
			builder.append(dateTill);
			builder.append(", ");
		}
		if (ranking != null)
		{
			builder.append("ranking=");
			builder.append(ranking);
			builder.append(", ");
		}
		builder.append("numberOfResults=");
		builder.append(numberOfResults);
		builder.append(", ");
		if (updated != null)
		{
			builder.append("updated=");
			builder.append(updated);
			builder.append(", ");
		}
		if (elapsedTime != null)
		{
			builder.append("elapsedTime=");
			builder.append(elapsedTime);
			builder.append(", ");
		}
		if (results != null)
		{
			builder.append("results=");
			builder.append(results);
		}
		builder.append("]");
		return builder.toString();
	}
}
