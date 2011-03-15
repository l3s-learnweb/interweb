package de.l3s.interwebj.oauth;


import java.net.*;


public class OAuthParams
{
	
	private URL requestUrl;
	private String oauth_token;
	private String oauth_token_secret;
	

	public String getOauth_token()
	{
		return oauth_token;
	}
	

	public String getOauth_token_secret()
	{
		return oauth_token_secret;
	}
	

	public URL getRequestUrl()
	{
		return requestUrl;
	}
	

	public void setOauth_token(String oauth_token)
	{
		this.oauth_token = oauth_token;
	}
	

	public void setOauth_token_secret(String oauth_token_secret)
	{
		this.oauth_token_secret = oauth_token_secret;
	}
	

	public void setRequestUrl(URL requestUrl)
	{
		this.requestUrl = requestUrl;
	}
	
}
