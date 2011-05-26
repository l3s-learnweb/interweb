package de.l3s.interwebj.jaxb.auth;


import javax.xml.bind.annotation.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.jaxb.*;


@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class OAuthRequestTokenResponse
    extends XMLResponse
{
	
	@XmlElement(name = "request_token")
	protected OAuthRequestTokenEntity requestToken;
	

	public OAuthRequestTokenResponse()
	{
	}
	

	public OAuthRequestTokenResponse(AuthCredentials requestToken)
	{
		this(new OAuthRequestTokenEntity(requestToken));
	}
	

	public OAuthRequestTokenResponse(OAuthRequestTokenEntity requestToken)
	{
		this.requestToken = requestToken;
	}
	

	public OAuthRequestTokenEntity getRequestToken()
	{
		return requestToken;
	}
	

	public void setRequestToken(OAuthRequestTokenEntity requestToken)
	{
		this.requestToken = requestToken;
	}
}
