package de.l3s.interwebj.query;


import java.io.*;
import java.util.*;

import de.l3s.interwebj.connector.*;
import de.l3s.interwebj.jaxb.*;
import de.l3s.interwebj.util.*;


public class Query
    implements Serializable
{
	
	public enum SearchScope
	{
		TEXT, TAGS;
		
		public String getName()
		{
			return name().toLowerCase();
		}
		

		public static SearchScope find(String name)
		{
			try
			{
				return SearchScope.valueOf(name.toUpperCase());
			}
			catch (IllegalArgumentException e)
			{
				return null;
			}
		}
	}
	
	public enum SortOrder
	{
		RELEVANCE, DATE, INTERESTINGNESS;
		
		public String getName()
		{
			return name().toLowerCase();
		}
		

		public static SortOrder find(String name)
		{
			try
			{
				return SortOrder.valueOf(name.toUpperCase());
			}
			catch (IllegalArgumentException e)
			{
				return null;
			}
		}
	}
	

	private static final long serialVersionUID = 3955897587724588474L;
	
	public static final int DEFAULT_RESULT_COUNT = 10;
	public static final String CT_TEXT = "text";
	public static final String CT_VIDEO = "video";
	public static final String CT_IMAGE = "image";
	public static final String CT_AUDIO = "audio";
	
	private String id;
	private String link;
	private String query;
	private List<ServiceConnector> connectors;
	private List<String> contentTypes;
	private int resultCount;
	private SortOrder sortOrder;
	private Set<SearchScope> searchScopes;
	private Map<String, String> params;
	

	Query(String id,
	      String query,
	      List<String> contentTypes,
	      Map<String, String> params)
	{
		if (id == null)
		{
			throw new NullPointerException("Argument [id] can not be null");
		}
		if (query == null)
		{
			throw new NullPointerException("Argument [query] can not be null");
		}
		if (contentTypes == null)
		{
			throw new NullPointerException("Argument [contentTypes] can not be null");
		}
		if (resultCount < 0)
		{
			throw new IllegalArgumentException("Argument [resultCount] = ["
			                                   + resultCount
			                                   + "] must not apply condition [< 0]");
		}
		if (params == null)
		{
			throw new NullPointerException("Argument [params] can not be null");
		}
		this.id = id;
		this.query = query;
		connectors = new ArrayList<ServiceConnector>();
		this.contentTypes = contentTypes;
		this.params = params;
		resultCount = DEFAULT_RESULT_COUNT;
		sortOrder = SortOrder.RELEVANCE;
		searchScopes = new HashSet<Query.SearchScope>();
	}
	

	public void addConnector(ServiceConnector connector)
	{
		connectors.add(connector);
	}
	

	public void addContentType(String contentType)
	{
		contentTypes.add(contentType);
	}
	

	public void addParam(String key, String value)
	{
		params.put(key, value);
	}
	

	public void addSearchScope(SearchScope searchScope)
	{
		searchScopes.add(searchScope);
	}
	

	public IWSearchQuery createIWSearchQuery()
	{
		IWSearchQuery iwSearchQuery = new IWSearchQuery();
		iwSearchQuery.setId(id);
		iwSearchQuery.setLink(link);
		iwSearchQuery.setQueryString(query);
		iwSearchQuery.setSearchIn(CoreUtils.setToString(searchScopes).toLowerCase());
		iwSearchQuery.setMediaTypes(CoreUtils.collectionToString(contentTypes));
		iwSearchQuery.setDateFrom(getParam("date_from"));
		iwSearchQuery.setDateTill(getParam("date_till"));
		iwSearchQuery.setRanking(sortOrder.getName());
		iwSearchQuery.setNumberOfResults(resultCount);
		//		iwSearchQuery.setUpdated("");
		return iwSearchQuery;
	}
	

	public List<ServiceConnector> getConnectors()
	{
		return connectors;
	}
	

	public List<String> getContentTypes()
	{
		return contentTypes;
	}
	

	public String getId()
	{
		return id;
	}
	

	public String getLink()
	{
		return link;
	}
	

	public String getParam(String name)
	{
		return params.get(name);
	}
	

	public String getParam(String name, String defaultValue)
	{
		return params.containsKey(name)
		    ? getParam(name) : defaultValue;
	}
	

	public Map<String, String> getParams()
	{
		return params;
	}
	

	public String getQuery()
	{
		return query;
	}
	

	public int getResultCount()
	{
		return resultCount;
	}
	

	public Set<SearchScope> getSearchScopes()
	{
		return searchScopes;
	}
	

	public SortOrder getSortOrder()
	{
		return sortOrder;
	}
	

	public void setContentTypes(List<String> contentTypes)
	{
		this.contentTypes = contentTypes;
	}
	

	public void setLink(String link)
	{
		this.link = link;
	}
	

	public void setResultCount(int resultCount)
	{
		this.resultCount = resultCount;
	}
	

	public void setSearchScopes(Set<SearchScope> searchScopes)
	{
		searchScopes = new HashSet<Query.SearchScope>(searchScopes);
	}
	

	public void setSortOrder(SortOrder sortOrder)
	{
		this.sortOrder = sortOrder;
	}
	

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Query [");
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
		if (query != null)
		{
			builder.append("query=");
			builder.append(query);
			builder.append(", ");
		}
		if (contentTypes != null)
		{
			builder.append("contentTypes=");
			builder.append(contentTypes);
			builder.append(", ");
		}
		builder.append("resultCount=");
		builder.append(resultCount);
		builder.append(", ");
		if (sortOrder != null)
		{
			builder.append("sortOrder=");
			builder.append(sortOrder);
			builder.append(", ");
		}
		if (searchScopes != null)
		{
			builder.append("searchScopes=");
			builder.append(searchScopes);
			builder.append(", ");
		}
		if (params != null)
		{
			builder.append("params=");
			builder.append(params);
		}
		builder.append("]");
		return builder.toString();
	}
	
}
