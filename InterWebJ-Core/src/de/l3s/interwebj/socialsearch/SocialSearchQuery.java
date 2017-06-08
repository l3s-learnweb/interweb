package de.l3s.interwebj.socialsearch;

import java.io.Serializable;

public class SocialSearchQuery implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    String query;

    public SocialSearchQuery()
    {
	// TODO Auto-generated constructor stub
    }

    public String getQuery()
    {
	return query;
    }

    public void setQuery(String query)
    {
	this.query = query;
    }

    public SocialSearchQuery(String q)
    {
	query = q;
    }
}
