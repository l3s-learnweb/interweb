package de.l3s.interwebj.connector.interweb;


import javax.xml.bind.annotation.*;


@XmlRootElement(name = "token")
@XmlAccessorType(XmlAccessType.FIELD)
public class IWRequestTokenEntity
{
	
	@XmlAttribute(name = "type")
	protected String type;
	@XmlAttribute(name = "token")
	protected String token;
	

	public String getToken()
	{
		return token;
	}
	

	public String getType()
	{
		return type;
	}
	

	public void setToken(String token)
	{
		this.token = token;
	}
	

	public void setType(String type)
	{
		this.type = type;
	}
}
