package de.l3s.interwebj.bean;


import java.io.*;
import java.util.*;

import javax.faces.bean.*;
import javax.faces.model.*;

import com.sun.istack.internal.*;

import de.l3s.interwebj.connector.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.query.*;
import de.l3s.interwebj.query.Query.SearchScope;
import de.l3s.interwebj.webutil.*;


@ManagedBean
@ViewScoped
public class SearchBean
    implements Serializable
{
	
	private static final long serialVersionUID = -4894599353026933768L;
	
	@NotNull
	private String query;
	private List<String> selectedContentTypes;
	private QueryResult queryResult;
	

	public List<SelectItem> getAllContentTypes()
	    throws InterWebException
	{
		List<SelectItem> allContentTypes = new ArrayList<SelectItem>();
		Engine engine = Environment.getInstance().getEngine();
		for (String contentType : engine.getContentTypes())
		{
			SelectItem selectItem = new SelectItem(contentType);
			selectItem.setNoSelectionOption(true);
			allContentTypes.add(selectItem);
		}
		return allContentTypes;
	}
	

	public String getConnectorBaseUrl(String connectorName)
	    throws InterWebException
	{
		Engine engine = Environment.getInstance().getEngine();
		ServiceConnector connector = engine.getConnector(connectorName);
		return connector.getBaseUrl();
	}
	

	public String getQuery()
	{
		return query;
	}
	

	public QueryResult getQueryResult()
	{
		return queryResult;
	}
	

	public int getResultIndex(Object resultItem)
	{
		return queryResult.getResultItems().indexOf(resultItem);
	}
	

	public List<String> getSelectedContentTypes()
	{
		SessionBean sessionBean = (SessionBean) FacesUtils.getManagedBean("sessionBean");
		return sessionBean.getSelectedContentTypes();
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
		return null;
	}
	

	public boolean hasResults()
	{
		return queryResult != null && queryResult.size() > 0;
	}
	

	public String search()
	    throws InterWebException
	{
		SessionBean sessionBean = (SessionBean) FacesUtils.getManagedBean("sessionBean");
		sessionBean.setSelectedContentTypes(selectedContentTypes);
		Environment.logger.debug("searching the query [" + query
		                         + "], content types " + selectedContentTypes);
		QueryFactory queryFactory = new QueryFactory();
		Query query = queryFactory.createQuery(this.query, selectedContentTypes);
		query.addSearchScope(SearchScope.TEXT);
		query.addSearchScope(SearchScope.TAGS);
		query.setResultCount(2);
		Engine engine = Environment.getInstance().getEngine();
		for (ServiceConnector connector : engine.getConnectors())
		{
			query.addConnector(connector);
		}
		IWPrincipal principal = FacesUtils.getPrincipalBean().getPrincipal();
		Environment.logger.debug("user: [" + principal + "]");
		queryResult = engine.search(query, principal);
		Environment.logger.debug(queryResult.size() + " results found");
		return "success";
	}
	

	public void setQuery(String query)
	{
		this.query = query;
	}
	

	public void setSelectedContentTypes(List<String> selectedContentTypes)
	{
		this.selectedContentTypes = selectedContentTypes;
	}
	
}
