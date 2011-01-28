package de.l3s.interwebj.query;


import java.util.*;


public class Query
{
	
	private String query;
	

	//	private List<String> types;
	//	private Properties properties;
	
	public Query(String query)
	{
		this(query, null, null);
	}
	

	public Query(String query, List<String> types, Properties properties)
	{
		if (query == null)
		{
			throw new NullPointerException("Argument [query] can not be null");
		}
		this.query = query;
		//		this.types = types;
		//		this.properties = properties;
	}
	

	public String getQuery()
	{
		return query;
	}
}
