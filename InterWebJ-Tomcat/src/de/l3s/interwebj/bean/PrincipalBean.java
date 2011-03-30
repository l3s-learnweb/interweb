package de.l3s.interwebj.bean;


import java.io.*;

import javax.faces.application.*;
import javax.faces.bean.*;
import javax.faces.context.*;
import javax.servlet.http.*;

import de.l3s.interwebj.core.*;
import de.l3s.interwebj.db.*;
import de.l3s.interwebj.webutil.*;


@ManagedBean
@SessionScoped
public class PrincipalBean
{
	
	private IWPrincipal principal;
	

	public IWPrincipal getPrincipal()
	{
		return principal;
	}
	

	public boolean hasRole(String role)
	{
		//		Environment.logger.debug("requesting role for principal "
		//		                         + principal
		//		                         + ": "
		//		                         + role
		//		                         + " - "
		//		                         + (principal != null && principal.hasRole(role)));
		return principal != null && principal.hasRole(role);
	}
	

	public boolean isLoggedIn()
	{
		return principal != null;
	}
	

	public String login(String username, String password)
	    throws InterWebException, IOException
	{
		Environment environment = Environment.getInstance();
		Database database = environment.getDatabase();
		principal = database.authenticate(username, password);
		if (principal == null)
		{
			FacesUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
			                            "Incorrect login. Please check username/password.",
			                            "login_form:login_button");
			return "failed";
		}
		SessionBean sessionBean = (SessionBean) FacesUtils.getManagedBean("sessionBean");
		String savedRequestUrl = sessionBean.getSavedRequestUrl();
		if (savedRequestUrl != null)
		{
			sessionBean.setSavedRequestUrl(null);
			Environment.logger.debug("redirecting to: " + savedRequestUrl);
			ExternalContext externalContext = FacesUtils.getExternalContext();
			externalContext.redirect(savedRequestUrl);
		}
		return "success";
	}
	

	public String logout()
	    throws IOException
	{
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext ec = context.getExternalContext();
		HttpServletRequest request = (HttpServletRequest) ec.getRequest();
		request.getSession(false).invalidate();
		principal = null;
		ec.redirect("./index.xhtml");
		return "success";
	}
	

	public void setPrincipal(IWPrincipal principal)
	{
		this.principal = principal;
	}
	
}
