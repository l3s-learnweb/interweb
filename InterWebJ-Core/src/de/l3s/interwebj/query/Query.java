package de.l3s.interwebj.query;

import static de.l3s.interwebj.util.Assertions.*;

import java.io.*;
import java.util.*;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import de.l3s.interwebj.core.Environment;

public class Query implements Serializable
{

    public enum SearchScope
    {
	TEXT,
	TAGS;

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
	    catch(IllegalArgumentException e)
	    {
		return null;
	    }
	}
    }

    public enum SortOrder
    {
	RELEVANCE,
	DATE,
	INTERESTINGNESS;

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
	    catch(IllegalArgumentException e)
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
    public static final String CT_PRESENTATION = "presentation";
    public static final String CT_FRIEND = "friend";

    private String id;
    private String link;
    private String query;
    private List<String> connectorNames;
    private List<String> contentTypes;
    private int resultCount;
    private int page = 1;
    private String language = "en";
    private float privacy = -1f;
    private boolean privacyUseImageFeatures = false;
    private long updated;
    private SortOrder sortOrder;
    private Set<SearchScope> searchScopes;
    private Map<String, String> params;
    private int timeout = 30;

    @Override
    public boolean equals(Object other)
    {
	if(!other.getClass().equals(getClass()))
	    return false;

	Query oQuery = (Query) other;
	boolean ret = true;

	if(!query.equalsIgnoreCase(oQuery.query) || page != oQuery.page || resultCount != oQuery.resultCount || !language.equals(oQuery.language))
	    ret = false;

	Environment.logger.info("equals: " + query + " - " + oQuery.query + ";" + ret);

	return ret;
    }

    @Override
    public int hashCode()
    {
	HashFunction hf = Hashing.md5();
	HashCode hc = hf.newHasher().putString(query.toLowerCase(), Charsets.UTF_8).putString(language, Charsets.UTF_8).putInt(page).putInt(resultCount).putInt(connectorNames.hashCode()).putInt(contentTypes.hashCode()).hash();

	return hc.asInt();
    }

    public Query(String id, String query, List<String> contentTypes, Map<String, String> params)
    {
	notNull(id, "id");
	notNull(query, "query");
	notNull(contentTypes, "contentTypes");
	notNull(params, "params");
	if(resultCount < 0)
	{
	    throw new IllegalArgumentException("Argument [resultCount] = [" + resultCount + "] must not apply condition [< 0]");
	}
	this.id = id;
	this.query = query;
	connectorNames = new ArrayList<String>();
	this.contentTypes = contentTypes;
	this.params = params;
	resultCount = DEFAULT_RESULT_COUNT;
	sortOrder = SortOrder.RELEVANCE;
	searchScopes = new HashSet<Query.SearchScope>();
    }

    public void addConnectorName(String connectorName)
    {
	connectorNames.add(connectorName);
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

    public List<String> getConnectorNames()
    {
	return connectorNames;
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
	return params.containsKey(name) ? getParam(name) : defaultValue;
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

    public long getUpdated()
    {
	return updated;
    }

    public void setConnectorNames(List<String> connectorNames)
    {
	this.connectorNames = connectorNames;
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

    public void setUpdated(long updated)
    {
	this.updated = updated;
    }

    public int getPage()
    {
	return page;
    }

    public void setPage(int page)
    {
	this.page = page > 1 ? page : 1;
    }

    public String getLanguage()
    {
	return language;
    }

    public void setLanguage(String language)
    {
	this.language = language;
    }

    public float getPrivacy()
    {
	return privacy;
    }

    public void setPrivacy(float privacy)
    {
	this.privacy = privacy;
    }

    public boolean isPrivacyUseImageFeatures()
    {
	return privacyUseImageFeatures;
    }

    public void setPrivacyUseImageFeatures(boolean privacyUseImageFeatures)
    {
	this.privacyUseImageFeatures = privacyUseImageFeatures;
    }

    public int getTimeout()
    {
	return timeout;
    }

    public void setTimeout(int timeout)
    {
	this.timeout = timeout;
    }

    @Override
    public String toString()
    {
	StringBuilder builder = new StringBuilder();
	builder.append("Query [");
	if(id != null)
	{
	    builder.append("id=");
	    builder.append(id);
	    builder.append(", ");
	}
	if(link != null)
	{
	    builder.append("link=");
	    builder.append(link);
	    builder.append(", ");
	}
	if(query != null)
	{
	    builder.append("query=");
	    builder.append(query);
	    builder.append(", ");
	}
	if(contentTypes != null)
	{
	    builder.append("contentTypes=");
	    builder.append(contentTypes);
	    builder.append(", ");
	}
	if(connectorNames != null)
	{
	    builder.append("connectorNames=");
	    builder.append(connectorNames);
	    builder.append(", ");
	}
	builder.append("resultCount=");
	builder.append(resultCount);
	builder.append(", ");
	if(sortOrder != null)
	{
	    builder.append("sortOrder=");
	    builder.append(sortOrder);
	    builder.append(", ");
	}
	if(searchScopes != null)
	{
	    builder.append("searchScopes=");
	    builder.append(searchScopes);
	    builder.append(", ");
	}
	if(params != null)
	{
	    builder.append("params=");
	    builder.append(params);
	}
	builder.append("]");
	return builder.toString();
    }

}
