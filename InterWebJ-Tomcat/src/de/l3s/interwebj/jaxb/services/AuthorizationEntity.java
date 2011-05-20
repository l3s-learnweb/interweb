package de.l3s.interwebj.jaxb.services;


import javax.xml.bind.annotation.*;


@XmlAccessorType(XmlAccessType.FIELD)
public class AuthorizationEntity
{
	
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class AuthorizationLinkEntity
	{
		
		@XmlAttribute(name = "method")
		protected String method;
		@XmlValue
		protected String link;
		

		public AuthorizationLinkEntity()
		{
		}
		

		public AuthorizationLinkEntity(String method, String link)
		{
			this.method = method;
			this.link = link;
		}
		

		public String getLink()
		{
			return link;
		}
		

		public String getMethod()
		{
			return method;
		}
		

		public void setLink(String link)
		{
			this.link = link;
		}
		

		public void setMethod(String method)
		{
			this.method = method;
		}
	}
	

	@XmlAttribute(name = "type")
	protected String type;
	@XmlElement(name = "link")
	protected AuthorizationLinkEntity authorizationLinkEntity;
	

	public AuthorizationLinkEntity getAuthorizationLinkEntity()
	{
		return authorizationLinkEntity;
	}
	

	public String getType()
	{
		return type;
	}
	

	public void setAuthorizationLinkEntity(AuthorizationLinkEntity authorizationLinkEntity)
	{
		this.authorizationLinkEntity = authorizationLinkEntity;
	}
	

	public void setType(String type)
	{
		this.type = type;
	}
	
}
