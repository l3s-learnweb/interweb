package de.l3s.interwebj.query;


import java.io.*;
import java.util.*;


public class Query
    implements Serializable
{
	
	private static final long serialVersionUID = 3955897587724588474L;
	
	private String query;
	private List<String> contentTypes;
	private Map<String, String> params;
	

	public Query(String query, List<String> contentTypes)
	{
		// TODO: get default parameters
		this(query, contentTypes, new HashMap<String, String>());
	}
	

	public Query(String query,
	             List<String> contentTypes,
	             Map<String, String> params)
	{
		if (query == null)
		{
			throw new NullPointerException("Argument [query] can not be null");
		}
		if (contentTypes == null)
		{
			throw new NullPointerException("Argument [contentTypes] can not be null");
		}
		if (params == null)
		{
			throw new NullPointerException("Argument [params] can not be null");
		}
		this.query = query;
		this.contentTypes = contentTypes;
		this.params = params;
	}
	

	public void addContentType(String contentType)
	{
		contentTypes.add(contentType);
	}
	

	public void addParam(String key, String value)
	{
		params.put(key, value);
	}
	

	public List<String> getContentTypes()
	{
		return contentTypes;
	}
	

	public Map<String, String> getParams()
	{
		return params;
	}
	

	public String getQuery()
	{
		return query;
	}
	

	public void setContentTypes(List<String> contentTypes)
	{
		this.contentTypes = contentTypes;
	}
	

	public void setParams(Map<String, String> params)
	{
		this.params = params;
	}
	

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Query [");
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
		if (params != null)
		{
			builder.append("params=");
			builder.append(params);
		}
		builder.append("]");
		return builder.toString();
	}
}
