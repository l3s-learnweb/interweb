package de.l3s.interwebj.tomcat.jaxb.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class AuthorizationLinkResponse {

    @XmlElement(name = "link")
    protected AuthorizationLinkEntity authorizationLinkEntity;

    public AuthorizationLinkResponse() {
    }

    public AuthorizationLinkEntity getAuthorizationLinkEntity() {
        return authorizationLinkEntity;
    }

    public void setAuthorizationLinkEntity(AuthorizationLinkEntity authorizationLinkEntity) {
        this.authorizationLinkEntity = authorizationLinkEntity;
    }

}
