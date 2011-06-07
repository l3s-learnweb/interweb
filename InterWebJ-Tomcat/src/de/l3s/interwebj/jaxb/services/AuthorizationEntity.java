package de.l3s.interwebj.jaxb.services;


import javax.xml.bind.annotation.*;


@XmlAccessorType(XmlAccessType.FIELD)
public class AuthorizationEntity
{
	
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