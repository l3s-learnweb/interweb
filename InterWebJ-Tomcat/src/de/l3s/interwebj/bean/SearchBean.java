package de.l3s.interwebj.bean;

import java.io.*;
import java.util.*;

import javax.faces.application.*;
import javax.faces.bean.*;
import javax.faces.model.*;

import com.sun.istack.internal.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.query.*;
import de.l3s.interwebj.query.Query.SearchScope;
import de.l3s.interwebj.util.*;
import de.l3s.interwebj.webutil.*;

@ManagedBean
@ViewScoped
public class SearchBean implements Serializable
{

    private static final long serialVersionUID = -4894599353026933768L;

    @NotNull
    private String query;
    private int page = 1;
    private String language = "en";
    private QueryResult queryResult;
    private List<String> selectedContentTypes;
    private List<String> selectedConnectorNames;
    @NotNull
    private int resultCount;
    private int timeout = 60;
    private boolean privacyUseImageFeatures = false;
    private boolean usePrivacy = false;

    public SearchBean()
    {
	init();
    }

    public String getConnectorBaseUrl(String connectorName) throws InterWebException
    {
	Engine engine = Environment.getInstance().getEngine();
	ServiceConnector connector = engine.getConnector(connectorName);
	return connector.getBaseUrl();
    }

    public List<SelectItem> getConnectorNames() throws InterWebException
    {
	List<SelectItem> connectorSelectItems = new ArrayList<SelectItem>();
	Engine engine = Environment.getInstance().getEngine();
	for(ServiceConnector connector : engine.getConnectors())
	{
	    if(connector.isRegistered())
	    {
		SelectItem selectItem = new SelectItem(connector.getName());
		connectorSelectItems.add(selectItem);
	    }
	}
	return connectorSelectItems;
    }

    public List<SelectItem> getContentTypes() throws InterWebException
    {
	if(selectedConnectorNames == null)
	{
	    init();
	}
	Engine engine = Environment.getInstance().getEngine();
	Set<String> contentTypes = new TreeSet<String>();
	for(String connectorName : selectedConnectorNames)
	{
	    ServiceConnector connector = engine.getConnector(connectorName);
	    contentTypes.addAll(connector.getContentTypes());
	}
	List<SelectItem> contentTypeSelectItems = new ArrayList<SelectItem>();
	for(String contentType : contentTypes)
	{
	    SelectItem selectItem = new SelectItem(contentType);
	    contentTypeSelectItems.add(selectItem);
	}
	return contentTypeSelectItems;
    }

    public String getImageUrl(Object obj, Long maxWidth, Long maxHeight) throws InterWebException
    {
	ResultItem resultItem = (ResultItem) obj;
	Thumbnail thumbnail = resultItem.getThumbnail(maxWidth.intValue(), maxHeight.intValue());
	if(thumbnail == null)
	{
	    return "";
	}
	return thumbnail.getUrl();
    }

    public String getQuery()
    {
	return query;
    }

    public QueryResult getQueryResult()
    {
	return queryResult;
    }

    public int getResultCount()
    {
	return resultCount;
    }

    public int getResultIndex(Object resultItem)
    {
	return queryResult.getResultItems().indexOf(resultItem);
    }

    public List<String> getSelectedConnectorNames()
    {
	return selectedConnectorNames;
    }

    public List<String> getSelectedContentTypes()
    {
	return selectedContentTypes;
    }

    public String getTags(Object obj)
    {
	ResultItem resultItem = (ResultItem) obj;
	String tags = resultItem.getTags();
	return (tags == null) ? null : CoreUtils.convertToUniqueList(tags).toString();
    }

    public String getTypeImageUrl(String type)
    {
	if(Query.CT_AUDIO.equals(type))
	{
	    return "music.png";
	}
	if(Query.CT_IMAGE.equals(type))
	{
	    return "photo.png";
	}
	if(Query.CT_TEXT.equals(type))
	{
	    return "script.png";
	}
	if(Query.CT_VIDEO.equals(type))
	{
	    return "film.png";
	}
	if(Query.CT_FRIEND.equals(type))
	{
	    return "user.png";
	}
	if(Query.CT_PRESENTATION.equals(type))
	{
	    return "pictures.png";
	}
	return null;
    }

    public boolean hasResults()
    {
	return queryResult != null;
    }

    public void init()
    {
	Engine engine = Environment.getInstance().getEngine();
	selectedConnectorNames = engine.getConnectorNames();
	selectedContentTypes = engine.getContentTypes();
	resultCount = 10;
    }

    public void save()
    {
    }

    public String search()
    {
	QueryFactory queryFactory = new QueryFactory();
	Query query = queryFactory.createQuery(this.query, selectedContentTypes);
	query.setConnectorNames(selectedConnectorNames);
	String link = FacesUtils.getInterWebJBean().getBaseUrl() + "api/search/" + query.getId() + ".xml";
	query.setLink(link);
	query.addSearchScope(SearchScope.TEXT);
	query.addSearchScope(SearchScope.TAGS);
	query.setResultCount(resultCount);
	query.setPage(page);
	query.setLanguage(language);

	query.setPrivacy(usePrivacy ? 1f : -1f);
	query.setTimeout(timeout);
	query.setPrivacyUseImageFeatures(privacyUseImageFeatures);
	QueryResult queryResult = new QueryResult(query);
	Engine engine = Environment.getInstance().getEngine();
	InterWebPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
	try
	{
	    QueryResultCollector collector = engine.getQueryResultCollector(query, principal);
	    queryResult = collector.retrieve();
	}
	catch(InterWebException e)
	{
	    Environment.logger.severe(e.getMessage());
	    FacesUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e);
	}
	ExpirableMap<String, Object> expirableMap = engine.getExpirableMap();
	expirableMap.put(queryResult.getQuery().getId(), queryResult);
	this.queryResult = queryResult;
	return "success";
    }

    public void setQuery(String query)
    {
	this.query = query;
    }

    public void setResultCount(int resultCount)
    {
	this.resultCount = resultCount;
    }

    public void setSelectedConnectorNames(List<String> selectedConnectorNames)
    {
	this.selectedConnectorNames = selectedConnectorNames;
    }

    public void setSelectedContentTypes(List<String> selectedContentTypes)
    {
	this.selectedContentTypes = selectedContentTypes;
    }

    public int getPage()
    {
	return page;
    }

    public void setPage(int page)
    {
	this.page = page;
    }

    public String getLanguage()
    {
	return language;
    }

    public void setLanguage(String language)
    {
	this.language = language;
    }

    public int getTimeout()
    {
	return timeout;
    }

    public void setTimeout(int timeout)
    {
	this.timeout = timeout;
    }

    public boolean isPrivacyUseImageFeatures()
    {
	return privacyUseImageFeatures;
    }

    public void setPrivacyUseImageFeatures(boolean privacyUseImageFeatures)
    {
	this.privacyUseImageFeatures = privacyUseImageFeatures;
    }

    public boolean isUsePrivacy()
    {
	return usePrivacy;
    }

    public void setUsePrivacy(boolean usePrivacy)
    {
	this.usePrivacy = usePrivacy;
    }
}
