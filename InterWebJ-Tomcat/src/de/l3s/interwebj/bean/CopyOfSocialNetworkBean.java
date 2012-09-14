package de.l3s.interwebj.bean;


import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.sun.istack.internal.NotNull;

import de.l3s.interwebj.InterWebException;
import de.l3s.interwebj.core.Engine;
import de.l3s.interwebj.core.Environment;
import de.l3s.interwebj.core.InterWebPrincipal;
import de.l3s.interwebj.query.ContactFromSocialNetwork;
import de.l3s.interwebj.query.UserSocialNetworkCollector;
import de.l3s.interwebj.query.UserSocialNetworkResult;
import de.l3s.interwebj.socialsearch.SocialSearchResult;
import de.l3s.interwebj.socialsearch.SocialSearchResultCollector;
import de.l3s.interwebj.socialsearch.SocialSearchResultItem;
import de.l3s.interwebj.webutil.FacesUtils;


@ManagedBean
@ViewScoped
public class CopyOfSocialNetworkBean
    
{
	
	
	
	@NotNull
	private String userid;
	
	SocialSearchResult result;
	String query;
	
	public String getQuery() {
		return query;
	}





	public void setQuery(String query) {
		this.query = query;
	}





	public SocialSearchResult getResult() {
		return result;
	}





	public void setResult(SocialSearchResult result) {
		this.result = result;
	}





	public String getUserid() {
		return userid;
	}





	public void setUserid(String userid) {
		this.userid = userid;
	}





	public CopyOfSocialNetworkBean()
	{
		init();
	}
	

	
	

	public void init()
	{
		Engine engine = Environment.getInstance().getEngine();
		
	}
	

	public void save()
	{
	}
	

	public String search()
	{
		
		Engine engine = Environment.getInstance().getEngine();
		InterWebPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
		
		try
		{
			SocialSearchResultCollector collector = engine.getSocialSearchResultsOf(query, principal);
			result = collector.retrieve();
		}
		catch (InterWebException e)
		{
			e.printStackTrace();
			Environment.logger.severe(e.getMessage());
			FacesUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e);
		}
		ArrayList<SocialSearchResultItem> resitems=new ArrayList<SocialSearchResultItem>(result.getResultItems());
		
		for(SocialSearchResultItem item: resitems)
		{
			System.out.println(item.getUserid());
			System.out.println(item.getReason());
			System.out.println(item.getStory());
			for(String s: item.getEmbedhtmlofphotos())
			{
				System.out.println(s);
			}
		}
		return "success";
	}
	

}
