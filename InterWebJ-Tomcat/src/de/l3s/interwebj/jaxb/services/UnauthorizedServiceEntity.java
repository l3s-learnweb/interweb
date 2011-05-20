package de.l3s.interwebj.jaxb.services;


import javax.xml.bind.annotation.*;


@XmlAccessorType(XmlAccessType.FIELD)
public class UnauthorizedServiceEntity
    extends ServiceEntity
{
	
	@SuppressWarnings("hiding")
	@XmlElement(name = "authorization")
	protected AuthorizationEntity authorizationEntity;
	

	public UnauthorizedServiceEntity()
	{
		authorized = false;
	}
	

	@Override
	public AuthorizationEntity getAuthorizationEntity()
	{
		return authorizationEntity;
	}
	

	@Override
	public String getServiceUserId()
	{
		return serviceUserId;
	}
	

	@Override
	public void setAuthorizationEntity(AuthorizationEntity authorizationEntity)
	{
		this.authorizationEntity = authorizationEntity;
	}
	

	@Override
	public void setServiceUserId(String serviceUserId)
	{
		this.serviceUserId = serviceUserId;
	}
	
}
