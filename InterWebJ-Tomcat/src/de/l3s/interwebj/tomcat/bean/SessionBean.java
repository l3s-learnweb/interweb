package de.l3s.interwebj.tomcat.bean;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import de.l3s.interwebj.core.core.InterWebPrincipal;

import java.io.Serializable;

@Named
@SessionScoped
public class SessionBean implements Serializable
{
    private static final long serialVersionUID = 2772677361579500292L;

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
