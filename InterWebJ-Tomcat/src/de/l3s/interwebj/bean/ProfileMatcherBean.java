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
	private List<String> selectAbleConnectorNames = new LinkedList<String>();
	private String selectedConnectorName;	
	private Set<String> result;
	@NotNull
	private int maxTagCount = 50;
	@NotNull
	private int maxUserCount = 800; 

	
	

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
	


	public static class UserComperator implements Comparator<String> 
	{
		private StringDistance stringDistance;

		public UserComperator(StringDistance stringDistance)
		{
			this.stringDistance = stringDistance;
		}
		
		public int compare(String o1, String o2) 
		{			
			double distance1 = stringDistance.getDistance(o1); // distance from o1 to username
			double distance2 = stringDistance.getDistance(o2); // distance from o2 to username

			if(distance1 == distance2) 
				return o1.compareTo(o2);
			else if(distance1 < distance2)
				return -1;
			else
				return 1;
		}

	}


	public String search() throws IOException
	{
		if(maxTagCount < 1)
			maxTagCount = 50;
		if(maxUserCount < 1)
			maxUserCount = 100;
		
		// get tags for user
		Set<String> tags = null;
		Engine engine = Environment.getInstance().getEngine();
		ServiceConnector sourceConnector = engine.getConnector(selectedConnectorName);
		try {
			tags = sourceConnector.getTags(username, maxTagCount);
		}
		catch (IllegalArgumentException e) {
			FacesUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Unknown username");
			return "";
		}
		
		if(null == tags) {
			FacesUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "No tags for user found");
			return "";
		}
		
		System.out.println("tags "+tags.size());
		for(String tag : tags)
			System.out.print(tag+", ");		
		
		System.out.println("tags ende");

		// get users by tags at the other services
		for (ServiceConnector connector : engine.getConnectors())
		{
			if (connector.isRegistered() && !connector.getName().equals(selectedConnectorName))
			{
				Set<String> users = null;
				try {
					users = connector.getUsers(tags, maxUserCount);
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
				
				// sort users 														new JaccardDistance(username);
				TreeSet<String> orderedUsers = new TreeSet<String>(new UserComperator(new LevenshteinDistance(username)));
				orderedUsers.addAll(users);				
			
				
				result = orderedUsers;
				
				/*
				for(String idf : result)
				{
					
				}*/

			}				
		}
		
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

	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public Set<String> getResult() {
		return result;
	}


	public int getMaxTagCount() {
		return maxTagCount;
	}


	public void setMaxTagCount(int maxTagCount) {
		this.maxTagCount = maxTagCount;
	}


	public int getMaxUserCount() {
		return maxUserCount;
	}


	public void setMaxUserCount(int maxUserCount) {
		this.maxUserCount = maxUserCount;
	}
	
	
}
