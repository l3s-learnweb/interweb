package de.l3s.interwebj.tomcat.jaxb.services;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceEntity {

    @XmlAttribute(name = "id")
    protected String id;
    @XmlElement(name = "title")
    protected String title;
    @XmlElement(name = "mediatypes")
    protected String mediaTypes;
    @XmlElement(name = "authorized")
    protected boolean authorized;
    @XmlElement(name = "serviceuserid")
    protected String serviceUserId;
    @XmlElement(name = "message")
    protected String message;
    @XmlElement(name = "authorization")
    protected AuthorizationEntity authorizationEntity;
    @XmlElement(name = "revokeauthorization")
    protected AuthorizationEntity revokeAuthorizationEntity;

    public ServiceEntity() {
    }

    public ServiceEntity(AuthorizationEntity authorizationEntity, boolean authenticated) {
        authorized = authenticated;
        if (authenticated) {
            revokeAuthorizationEntity = authorizationEntity;
        } else {
            this.authorizationEntity = authorizationEntity;
        }
    }

    public AuthorizationEntity getAuthorizationEntity() {
        return authorizationEntity;
    }

    public void setAuthorizationEntity(AuthorizationEntity authorizationEntity) {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMediaTypes() {
        return mediaTypes;
    }

    public void setMediaTypes(String mediaTypes) {
        this.mediaTypes = mediaTypes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AuthorizationEntity getRevokeAuthorizationEntity() {
        return revokeAuthorizationEntity;
    }

    public void setRevokeAuthorizationEntity(AuthorizationEntity revokeAuthorizationEntity) {
        this.revokeAuthorizationEntity = revokeAuthorizationEntity;
    }

    public String getServiceUserId() {
        return serviceUserId;
    }

    public void setServiceUserId(String serviceUserId) {
        this.serviceUserId = serviceUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

}
