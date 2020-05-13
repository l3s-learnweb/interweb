package de.l3s.interwebj.tomcat.jaxb.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class UnauthorizedServiceEntity extends ServiceEntity
{
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
