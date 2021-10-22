package de.l3s.interwebj.tomcat.jaxb.services;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
public class AuthorizationLinkEntity {

    @XmlAttribute(name = "method")
    protected String method;
    @XmlValue
    protected String link;

    public AuthorizationLinkEntity() {
    }

    public AuthorizationLinkEntity(String method, String link) {
        this.method = method;
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
