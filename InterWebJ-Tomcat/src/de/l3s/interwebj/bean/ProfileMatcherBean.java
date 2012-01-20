package de.l3s.interwebj.bean;


import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.NotImplementedException;

import com.sun.istack.internal.NotNull;

import de.l3s.interwebj.InterWebException;
import de.l3s.interwebj.core.Engine;
import de.l3s.interwebj.core.Environment;
import de.l3s.interwebj.core.ServiceConnector;
import de.l3s.interwebj.webutil.FacesUtils;
import de.l3s.profileMatcher.LevenshteinDistance;
import de.l3s.profileMatcher.StringDistance;


@ManagedBean
@ViewScoped
public class ProfileMatcherBean implements Serializable
{	
	private static final long serialVersionUID = -4894599353026933768L;
	
	@NotNull
	private String username;
	@NotNull
	private Integer maxCount;
	private List<String> selectAbleConnectorNames = new LinkedList<String>();
	private String selectedConnectorName;
	
	private Set<String> result;
	
	/*
	@NotNull
	private int resultCount;
*/
	

	public ProfileMatcherBean()
	{
		Engine engine = Environment.getInstance().getEngine();
		for (ServiceConnector connector : engine.getConnectors())
		{
			if (connector.isRegistered())
			{
				try {
					connector.getTags("test", 0); // test if this function is implemented
					selectAbleConnectorNames.add(connector.getName());
					System.out.println(connector.getName());
				}
				catch (NotImplementedException e) {} // do nothing
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	public String getConnectorBaseUrl(String connectorName)
	    throws InterWebException
	{
		Engine engine = Environment.getInstance().getEngine();
		ServiceConnector connector = engine.getConnector(connectorName);
		return connector.getBaseUrl();
	}
	

	

/*
	public QueryResult getQueryResult()
	{
		return queryResult;
	}
	

	public int getResultCount()
	{
		return resultCount;
	}
	
/*
	public int getResultIndex(Object resultItem)
	{
		return queryResult.getResultItems().indexOf(resultItem);
	}

	public boolean hasResults()
	{
		return queryResult != null;
	}*/
	


	public static class TagComperator implements Comparator<String> 
	{
		private StringDistance stringDistance;

		public TagComperator(StringDistance stringDistance)
		{
			this.stringDistance = stringDistance;
		}
		
		public int compare(String o1, String o2) 
		{			
			double distance1 = stringDistance.getDistance(o1); // distance from o1 to username
			double distance2 = stringDistance.getDistance(o2); // distance from o2 to username
//System.out.println(distance1+":"+distance2);
			if(distance1 == distance2) { //System.out.print("g");
				return o1.compareTo(o2);}
			else if(distance1 < distance2)
				return -1;
			else
				return 1;
		}

	}
	public static void main(String[] args) throws IllegalArgumentException, IOException  
	{

	}


	public String search() throws IOException
	{
		//String username = "sergejzr"; // appmodo
		//String serviceName = "Youtube";
		final int MAX_TAG_COUNT = 500;
		final int MAX_USERS_COUNT = 500; // 500
		System.out.println("username:"+username);
		// get tags for user
		Set<String> tags = null;
		Engine engine = Environment.getInstance().getEngine();
		ServiceConnector sourceConnector = engine.getConnector(selectedConnectorName);
		try {
			tags = sourceConnector.getTags(username, MAX_TAG_COUNT);
		}
		catch (IllegalArgumentException e) {
			FacesUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Unknown username");
			return "";
		}
		
		if(null == tags)
			throw new RuntimeException("no tags for user found");
		
		/*
		for(String tag : tags)
			System.out.println(tag);		
		System.out.println("anzahl "+tags.size());
		*/

		// get users at the other services for tags
		for (ServiceConnector connector : engine.getConnectors())
		{
			if (connector.isRegistered() && !connector.getName().equals(selectedConnectorName))
			{
				Set<String> users = null;
				try {
					users = connector.getUsers(tags, MAX_USERS_COUNT);
				}
				catch (NotImplementedException e) 
				{
					continue;
				} 
				catch (Exception e) {
					e.printStackTrace();
				}

				if(null == users || users.size() == 0)
					continue;
				
				// sort users 
				TreeSet<String> orderedUsers = new TreeSet<String>(new TagComperator(new LevenshteinDistance(username)));
				orderedUsers.addAll(users);				
			
				System.out.println("\nBeste Treffer (Levenshtein):");
				int i=0;
				for(String user : orderedUsers)
				{
					System.out.println(user);
					if(i++ == 15)
						break;
				}
				
				result = orderedUsers;
				
				for(String idf : result)
				{
					
				}
				/*
				orderedUsers = new TreeSet<String>(new TagComperator(new JaccardDistance(username)));
				orderedUsers.addAll(users);	
				System.out.println("\nBeste Treffer (Jaccard):");
				i=0;
				for(String user : orderedUsers)
				{
					System.out.println(user);
					if(i++ == 50)
						break;
				}
					*/
			}				
		}
		/*
		QueryFactory queryFactory = new QueryFactory();
		Query query = queryFactory.createQuery(this.query, selectedContentTypes);
		query.setConnectorNames(selectAbleConnectorNames);
		String link = FacesUtils.getInterWebJBean().getBaseUrl()
		              + "api/search/" + query.getId() + ".xml";
		query.setLink(link);
		query.addSearchScope(SearchScope.TEXT);
		query.addSearchScope(SearchScope.TAGS);
		query.setResultCount(resultCount);
		query.setPage(page);
		query.setLanguage(language);
		query.setPrivacy(0.8f);
		QueryResult queryResult = new QueryResult(query);
		Engine engine = Environment.getInstance().getEngine();
		InterWebPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
		try
		{
			QueryResultCollector collector = engine.getQueryResultCollector(query, principal);
			queryResult = collector.retrieve();
		}
		catch (InterWebException e)
		{
			e.printStackTrace();
			Environment.logger.severe(e.getMessage());
			FacesUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e);
		}
		ExpirableMap<String, Object> expirableMap = engine.getExpirableMap();
		expirableMap.put(queryResult.getQuery().getId(), queryResult);
		this.queryResult = queryResult;*/
		return "success";
	}	
	
	public String getSelectedConnectorName() {
		return selectedConnectorName;
	}


	public void setSelectedConnectorName(String selectedConnectorName) {
		this.selectedConnectorName = selectedConnectorName;
	}


	public List<String> getSelectAbleConnectorNames()
	{
		return selectAbleConnectorNames;
	}


	public Integer getMaxCount() {
		return maxCount;
	}


	public void setMaxCount(Integer maxCount) {
		this.maxCount = maxCount;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public Set<String> getResult() {
		return result;
	}
}
