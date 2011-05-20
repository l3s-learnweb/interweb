package de.l3s.interwebj.jaxb.services;


import javax.xml.bind.annotation.*;


@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceEntity
{
	
	@XmlAttribute(name = "id")
	protected String id;
	@XmlElement(name = "title")
	protected String title;
	@XmlElement(name = "authorized")
	protected boolean authorized;
	@XmlElement(name = "serviceuserid")
	protected String serviceUserId;
	@XmlElement(name = "authorization")
	protected AuthorizationEntity authorizationEntity;
	@XmlElement(name = "revokeauthorization")
	protected AuthorizationEntity revokeAuthorizationEntity;
	

	public ServiceEntity()
	{
	}
	

	public ServiceEntity(AuthorizationEntity authorizationEntity,
	                     boolean authenticated)
	{
		authorized = authenticated;
		if (authenticated)
		{
			revokeAuthorizationEntity = authorizationEntity;
		}
		else
		{
			this.authorizationEntity = authorizationEntity;
		}
	}
	

	public AuthorizationEntity getAuthorizationEntity()
	{
		return authorizationEntity;
	}
	

	public String getId()
	{
		return id;
	}
	

	public AuthorizationEntity getRevokeAuthorizationEntity()
	{
		return revokeAuthorizationEntity;
	}
	

	public String getServiceUserId()
	{
		return serviceUserId;
	}
	

	public String getTitle()
	{
		return title;
	}
	

	public boolean isAuthorized()
	{
		return authorized;
	}
	

	public void setAuthorizationEntity(AuthorizationEntity authorizationEntity)
	{
	}
	

	public void setAuthorized(boolean authorized)
	{
		this.authorized = authorized;
	}
	

	public void setId(String id)
	{
		this.id = id;
	}
	

	public void setRevokeAuthorizationEntity(AuthorizationEntity revokeAuthorizationEntity)
	{
		this.revokeAuthorizationEntity = revokeAuthorizationEntity;
	}
	

	public void setServiceUserId(String serviceUserId)
	{
		this.serviceUserId = serviceUserId;
	}
	

	public void setTitle(String title)
	{
		this.title = title;
	}
	
}
