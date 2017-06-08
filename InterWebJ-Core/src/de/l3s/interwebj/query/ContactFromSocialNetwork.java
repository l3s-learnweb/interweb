package de.l3s.interwebj.query;

public class ContactFromSocialNetwork
{

    private String username;
    private String userId;
    private int hop;
    private String source;

    public ContactFromSocialNetwork(String username, String userId, int hop, String source)
    {
	super();
	this.username = username;
	this.userId = userId;
	this.hop = hop;
	this.source = source;
    }

    public ContactFromSocialNetwork()
    {
	super();

    }

    public String getUsername()
    {
	return username;
    }

    public void setUsername(String username)
    {
	this.username = username;
    }

    public String getUserId()
    {
	return userId;
    }

    public void setUserId(String userId)
    {
	this.userId = userId;
    }

    public int getHop()
    {
	return hop;
    }

    public void setHop(int hop)
    {
	this.hop = hop;
    }

    public String getSource()
    {
	return source;
    }

    public void setSource(String source)
    {
	this.source = source;
    }

}
