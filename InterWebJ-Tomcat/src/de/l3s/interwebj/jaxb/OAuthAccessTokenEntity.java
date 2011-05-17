package de.l3s.interwebj.jaxb;


import javax.xml.bind.annotation.*;

import de.l3s.interwebj.*;


@XmlRootElement(name = "access_token")
@XmlAccessorType(XmlAccessType.FIELD)
public class OAuthAccessTokenEntity
{
	
	@XmlElement(name = "oauth_token")
	protected String oauthToken;
	@XmlElement(name = "oauth_token_secret")
	protected String oauthTokenSecret;
	

	public OAuthAccessTokenEntity()
	{
	}
	

	public OAuthAccessTokenEntity(AuthCredentials authCredentials)
	{
		this(authCredentials.getKey(), authCredentials.getSecret());
	}
	

	public OAuthAccessTokenEntity(String oauthToken, String oauthTokenSecret)
	{
		this.oauthToken = oauthToken;
		this.oauthTokenSecret = oauthTokenSecret;
	}
	

	public String getOauthToken()
	{
		return oauthToken;
	}
	

	public String getOauthTokenSecret()
	{
		return oauthTokenSecret;
	}
	

	public void setOauthToken(String oauthToken)
	{
		this.oauthToken = oauthToken;
	}
	

	public void setOauthTokenSecret(String oauthTokenSecret)
	{
		this.oauthTokenSecret = oauthTokenSecret;
	}
}