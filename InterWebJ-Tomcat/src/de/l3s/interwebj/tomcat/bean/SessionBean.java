package de.l3s.interwebj.tomcat.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import de.l3s.interwebj.core.core.InterWebPrincipal;

@ManagedBean
@SessionScoped
public class SessionBean
{

    private InterWebPrincipal principal;
    private String savedRequestUrl;

    public InterWebPrincipal getPrincipal()
    {
	return principal;
    }

    public String getSavedRequestUrl()
    {
	return savedRequestUrl;
    }

    public void setPrincipal(InterWebPrincipal principal)
    {
	this.principal = principal;
    }

    public void setSavedRequestUrl(String savedRequestUrl)
    {
	this.savedRequestUrl = savedRequestUrl;
    }
}
