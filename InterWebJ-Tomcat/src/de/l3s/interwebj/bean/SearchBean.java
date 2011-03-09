package de.l3s.interwebj.bean;


import java.io.*;
import java.util.*;

import javax.faces.bean.*;
import javax.faces.model.*;

import com.sun.istack.internal.*;

import de.l3s.interwebj.connector.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.query.*;
import de.l3s.interwebj.util.*;


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
		Engine engine = Utils.getEngine();
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
		Engine engine = Utils.getEngine();
		ServiceConnector connector = engine.getConnector(connectorName);
		return connector.getBaseUrl().toExternalForm();
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
		SessionBean sessionBean = (SessionBean) Utils.getManagedBean("sessionBean");
		return sessionBean.getSelectedContentTypes();
	}
	

	public boolean hasResults()
	{
		return queryResult != null && queryResult.size() > 0;
	}
	

	public String search()
	    throws InterWebException
	{
		SessionBean sessionBean = (SessionBean) Utils.getManagedBean("sessionBean");
		sessionBean.setSelectedContentTypes(selectedContentTypes);
		Environment.logger.debug("searching the query [" + query
		                         + "], content types " + selectedContentTypes);
		Query query = new Query(this.query, selectedContentTypes);
		Engine engine = Utils.getEngine();
		IWPrincipal principal = Utils.getPrincipalBean().getPrincipal();
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
