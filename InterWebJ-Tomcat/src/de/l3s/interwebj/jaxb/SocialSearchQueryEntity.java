package de.l3s.interwebj.jaxb;


import java.util.*;

import javax.xml.bind.annotation.*;

import org.apache.commons.lang.*;

import de.l3s.interwebj.query.*;
import de.l3s.interwebj.socialsearch.SocialSearchQuery;
import de.l3s.interwebj.util.*;


@XmlRootElement(name = "query")
@XmlAccessorType(XmlAccessType.FIELD)
public class SocialSearchQueryEntity
{
	
	@XmlAttribute(name = "id")
	protected String id;
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getQuery() {
		return query;
	}


	public void setQuery(String query) {
		this.query = query;
	}


	public List<SocialSearchResultEntity> getResults() {
		return results;
	}


	public void setResults(List<SocialSearchResultEntity> results) {
		this.results = results;
	}


	@XmlAttribute(name = "query")
	protected String query;
	@XmlElementWrapper(name = "results")
	@XmlElement(name = "result")
	protected List<SocialSearchResultEntity> results;
	

	public SocialSearchQueryEntity()
	{
		results = new ArrayList<SocialSearchResultEntity>();
	}
	

	public SocialSearchQueryEntity(SocialSearchQuery q)
	{
		this();
		id="me";
		query=q.getQuery();
		
	}
	

	public void addResult(SocialSearchResultEntity result)
	{
		results.add(result);
	}
	

	}
