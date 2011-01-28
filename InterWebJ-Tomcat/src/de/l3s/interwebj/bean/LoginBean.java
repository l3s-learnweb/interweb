package de.l3s.interwebj.bean;


import java.net.*;

import javax.faces.application.*;
import javax.faces.bean.*;

import com.sun.istack.internal.*;

import de.l3s.interwebj.core.*;
import de.l3s.interwebj.db.*;
import de.l3s.interwebj.util.*;


@ManagedBean
@RequestScoped
public class LoginBean
{
	
	@NotNull
	private String username;
	@NotNull
	private String password;
	

	public String getPassword()
	{
		return password;
	}
	

	public String getUsername()
	{
		return username;
	}
	

	public String login()
	    throws MalformedURLException
	{
		IWEnvironment environment = Utils.getEnvironment();
		IWDatabase database = environment.getDatabase();
		IWPrincipal principal = database.authenticate(username, password);
		if (principal == null)
		{
			Utils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
			                       "Incorrect login. Please check username/password.",
			                       "login_form:login_button");
			return "failed";
		}
		Utils.getPrincipalBean().setPrincipal(principal);
		return "success";
	}
	

	public void setPassword(String password)
	{
		this.password = password;
	}
	

	public void setUsername(String userName)
	{
		username = userName;
	}
}
