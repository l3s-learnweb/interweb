package de.l3s.interwebj.jaxb;


import javax.xml.bind.annotation.*;

import de.l3s.interwebj.query.*;


@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class IWSearchResponse
{
	
	@XmlAttribute(name = "stat")
	protected String stat;
	@XmlElement(name = "error")
	protected IWError error;
	@XmlElement(name = "query")
	protected IWSearchQuery query;
	

	public IWSearchResponse()
	{
		stat = "ok";
	}
	

	public IWSearchResponse(IWError error)
	{
		stat = "fail";
		this.error = error;
	}
	

	public IWSearchResponse(QueryResult queryResult)
	{
		this();
		IWSearchQuery iwSearchQuery = new IWSearchQuery(queryResult.getQuery());
		iwSearchQuery.setElapsedTime(String.valueOf(queryResult.getElapsedTime()));
		for (ResultItem resultItem : queryResult.getResultItems())
		{
			IWSearchResult iwSearchResult = new IWSearchResult(resultItem);
			iwSearchQuery.addResult(iwSearchResult);
		}
		query = iwSearchQuery;
	}
	

	public IWError getError()
	{
		return error;
	}
	

	public IWSearchQuery getQuery()
	{
		return query;
	}
	

	public String getStat()
	{
		return stat;
	}
	

	public void setError(IWError error)
	{
		this.error = error;
	}
	

	public void setQuery(IWSearchQuery query)
	{
		this.query = query;
	}
	

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("IWSearchResponse [");
		if (stat != null)
		{
			builder.append("stat=");
			builder.append(stat);
			builder.append(", ");
		}
		if (error != null)
		{
			builder.append("error=");
			builder.append(error);
			builder.append(", ");
		}
		if (query != null)
		{
			builder.append("query=");
			builder.append(query);
		}
		builder.append("]");
		return builder.toString();
	}
}
