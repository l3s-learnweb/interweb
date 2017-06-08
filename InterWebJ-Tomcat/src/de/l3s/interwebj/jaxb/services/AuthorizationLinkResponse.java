package de.l3s.interwebj.jaxb.services;

import javax.xml.bind.annotation.*;

import de.l3s.interwebj.jaxb.*;

@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class AuthorizationLinkResponse extends XMLResponse
{

    @XmlElement(name = "link")
    protected AuthorizationLinkEntity authorizationLinkEntity;

    public AuthorizationLinkResponse()
    {
    }

    public AuthorizationLinkEntity getAuthorizationLinkEntity()
    {
	return authorizationLinkEntity;
    }

    public void setAuthorizationLinkEntity(AuthorizationLinkEntity authorizationLinkEntity)
    {
	this.authorizationLinkEntity = authorizationLinkEntity;
    }

}
