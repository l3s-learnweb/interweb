package de.l3s.interwebj.jaxb.services;


import javax.xml.bind.annotation.*;


@XmlAccessorType(XmlAccessType.FIELD)
public class AuthorizedServiceEntity
    extends ServiceEntity
{
	
	@SuppressWarnings("hiding")
	@XmlElement(name = "revokeauthorization")
	protected AuthorizationEntity authorizationEntity;
	

	public AuthorizedServiceEntity()
	{
		authorized = true;
	}
	

	@Override
	public AuthorizationEntity getAuthorizationEntity()
	{
		return authorizationEntity;
	}
	

	@Override
	public void setAuthorizationEntity(AuthorizationEntity authorizationEntity)
	{
		this.authorizationEntity = authorizationEntity;
	}
}
