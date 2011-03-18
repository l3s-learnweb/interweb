package de.l3s.interwebj.oauth;


public class OAuthParams
{
	
	private String requestUrl;
	private String oauthToken;
	private String oauthTokenSecret;
	

	public String getOauthToken()
	{
		return oauthToken;
	}
	

	public String getOauthTokenSecret()
	{
		return oauthTokenSecret;
	}
	

	public String getRequestUrl()
	{
		return requestUrl;
	}
	

	public void setOauthToken(String oauthToken)
	{
		this.oauthToken = oauthToken;
	}
	

	public void setOauthTokenSecret(String oauthTokenSecret)
	{
		this.oauthTokenSecret = oauthTokenSecret;
	}
	

	public void setRequestUrl(String requestUrl)
	{
		this.requestUrl = requestUrl;
	}
	
}
