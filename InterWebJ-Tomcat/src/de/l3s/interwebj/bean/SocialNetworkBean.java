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
import de.l3s.interwebj.webutil.FacesUtils;


@ManagedBean
@ViewScoped
public class SocialNetworkBean
    
{
	
	
	
	@NotNull
	private String userid;
	
	UserSocialNetworkResult result;
	
	public UserSocialNetworkResult getResult() {
		return result;
	}





	public void setResult(UserSocialNetworkResult result) {
		this.result = result;
	}





	public String getUserid() {
		return userid;
	}





	public void setUserid(String userid) {
		this.userid = userid;
	}





	public SocialNetworkBean()
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
			UserSocialNetworkCollector collector = engine.getSocialNetworkOf("me", principal, "Facebook");
			result = collector.retrieve();
		}
		catch (InterWebException e)
		{
			e.printStackTrace();
			Environment.logger.severe(e.getMessage());
			FacesUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e);
		}
		
		ArrayList<ContactFromSocialNetwork> contacts= new ArrayList<ContactFromSocialNetwork>(result.getSocialnetwork().values());
		for (ContactFromSocialNetwork c: contacts) {
			System.out.println("friend:"+c.getUsername());
		}
		System.out.println("list complete");
		return "success";
	}
	

}
