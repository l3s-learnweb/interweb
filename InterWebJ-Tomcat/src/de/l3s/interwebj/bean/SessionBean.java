package de.l3s.interwebj.bean;

import javax.faces.bean.*;

import de.l3s.interwebj.core.*;

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
