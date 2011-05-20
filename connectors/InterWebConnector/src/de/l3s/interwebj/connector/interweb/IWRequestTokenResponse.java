package de.l3s.interwebj.connector.interweb;


import javax.xml.bind.annotation.*;


@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class IWRequestTokenResponse
    extends XMLResponse
{
	
	@XmlElement(name = "token")
	protected IWRequestTokenEntity requestToken;
	

	public IWRequestTokenResponse()
	{
	}
	

	public IWRequestTokenEntity getRequestToken()
	{
		return requestToken;
	}
	

	public void setRequestToken(IWRequestTokenEntity requestToken)
	{
		this.requestToken = requestToken;
	}
}
