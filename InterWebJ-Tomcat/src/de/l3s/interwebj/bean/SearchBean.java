package de.l3s.interwebj.bean;


import java.io.*;
import java.util.*;

import javax.faces.application.*;
import javax.faces.bean.*;
import javax.faces.model.*;

import com.sun.istack.internal.*;

import de.l3s.interwebj.InterWebException;
import de.l3s.interwebj.connector.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.query.*;
import de.l3s.interwebj.query.Query.SearchScope;
import de.l3s.interwebj.util.*;
import de.l3s.interwebj.webutil.*;


@ManagedBean
@ViewScoped
public class SearchBean
    implements Serializable
{
	
	private static final long serialVersionUID = -4894599353026933768L;
	
	@NotNull
	private String query;
	private QueryResult queryResult;
	private List<String> selectedContentTypes;
	private List<String> selectedConnectorNames;
	@NotNull
	private int resultCount;
	

	public SearchBean()
	{
		init();
	}
	

	public String getConnectorBaseUrl(String connectorName)
	    throws InterWebException
	{
		Engine engine = Environment.getInstance().getEngine();
		ServiceConnector connector = engine.getConnector(connectorName);
		return connector.getBaseUrl();
	}
	

	public List<SelectItem> getConnectorNames()
	    throws InterWebException
	{
		List<SelectItem> connectorSelectItems = new ArrayList<SelectItem>();
		Engine engine = Environment.getInstance().getEngine();
		IWPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
		for (ServiceConnector connector : engine.getConnectors())
		{
			if (connector.isRegistered()
			    && engine.isUserAuthenticated(connector, principal))
			{
				SelectItem selectItem = new SelectItem(connector.getName());
				connectorSelectItems.add(selectItem);
			}
		}
		return connectorSelectItems;
	}
	

	public List<SelectItem> getContentTypes()
	    throws InterWebException
	{
		if (selectedConnectorNames == null)
		{
			init();
		}
		Engine engine = Environment.getInstance().getEngine();
		Set<String> contentTypes = new TreeSet<String>();
		for (String connectorName : selectedConnectorNames)
		{
			ServiceConnector connector = engine.getConnector(connectorName);
			contentTypes.addAll(connector.getContentTypes());
		}
		List<SelectItem> contentTypeSelectItems = new ArrayList<SelectItem>();
		for (String contentType : contentTypes)
		{
			SelectItem selectItem = new SelectItem(contentType);
			contentTypeSelectItems.add(selectItem);
		}
		return contentTypeSelectItems;
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
	

	public String getTags(Object resultItem)
	{
		return CoreUtils.convertToUniqueList(((ResultItem) resultItem).getTags()).toString();
	}
	

	public String getTypeImageUrl(String type)
	{
		if (Query.CT_AUDIO.equals(type))
		{
			return "music.png";
		}
		if (Query.CT_IMAGE.equals(type))
		{
			return "photo.png";
		}
		if (Query.CT_TEXT.equals(type))
		{
			return "script.png";
		}
		if (Query.CT_VIDEO.equals(type))
		{
			return "film.png";
		}
		if (Query.CT_FRIEND.equals(type))
		{
			return "user.png";
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
		Environment.logger.debug("searching the query [" + query
		                         + "], content types " + selectedContentTypes);
		QueryFactory queryFactory = new QueryFactory();
		Query query = queryFactory.createQuery(this.query, selectedContentTypes);
		query.setConnectorNames(selectedConnectorNames);
		String link = FacesUtils.getInterWebJBean().getBaseUrl()
		              + "api/search/" + query.getId() + ".xml";
		query.setLink(link);
		query.addSearchScope(SearchScope.TEXT);
		query.addSearchScope(SearchScope.TAGS);
		query.setResultCount(resultCount);
		QueryResult queryResult = new QueryResult(query);
		Engine engine = Environment.getInstance().getEngine();
		IWPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
		try
		{
			QueryResultCollector collector = engine.getQueryResultCollector(query,
			                                                                principal);
			queryResult = collector.retrieve();
		}
		catch (InterWebException e)
		{
			e.printStackTrace();
			FacesUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
			                            e.getMessage());
		}
		engine.getStandingQueryResultPool().add(queryResult);
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
	
}
