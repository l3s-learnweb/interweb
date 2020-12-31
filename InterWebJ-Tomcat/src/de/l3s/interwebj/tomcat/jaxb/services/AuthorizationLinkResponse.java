package de.l3s.interwebj.tomcat.jaxb.services;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

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
