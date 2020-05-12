package de.l3s.interwebj.bean;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.HttpServletRequest;

import de.l3s.interwebj.core.Environment;
import de.l3s.interwebj.core.InterWebPrincipal;
import de.l3s.interwebj.db.Database;
import de.l3s.interwebj.webutil.FacesUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ManagedBean
@SessionScoped
public class PrincipalBean
{
	private static final Logger log = LogManager.getLogger(PrincipalBean.class);

    public boolean hasRole(String role)
    {
	SessionBean sessionBean = (SessionBean) FacesUtils.getManagedBean("sessionBean");
	InterWebPrincipal principal = sessionBean.getPrincipal();
	return principal != null && principal.hasRole(role);
    }

    public boolean isLoggedIn()
    {
	SessionBean sessionBean = (SessionBean) FacesUtils.getManagedBean("sessionBean");
	return sessionBean.getPrincipal() != null;
    }

    public String login(String username, String password) throws IOException
    {
	Environment environment = Environment.getInstance();
	Database database = environment.getDatabase();
	InterWebPrincipal principal = null;
	principal = database.authenticate(username, password);
	if(principal == null)
	{
	    FacesUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Incorrect login. Please check username and/or password.");
	    return "failed";
	}
	SessionBean sessionBean = (SessionBean) FacesUtils.getManagedBean("sessionBean");
	sessionBean.setPrincipal(principal);
	String savedRequestUrl = sessionBean.getSavedRequestUrl();
	if(savedRequestUrl != null)
	{
	    sessionBean.setSavedRequestUrl(null);
	    log.info("redirecting to: " + savedRequestUrl);
	    String contextPath = FacesUtils.getContextPath();
	    FacesUtils.redirect(contextPath + savedRequestUrl);
	}
	return "success";
    }

    public String logout() throws IOException
    {
	SessionBean sessionBean = (SessionBean) FacesUtils.getManagedBean("sessionBean");
	sessionBean.setPrincipal(null);
	HttpServletRequest request = FacesUtils.getRequest();
	request.getSession(false).invalidate();
	FacesUtils.getExternalContext().redirect("./index.xhtml");
	return "success";
    }
}
