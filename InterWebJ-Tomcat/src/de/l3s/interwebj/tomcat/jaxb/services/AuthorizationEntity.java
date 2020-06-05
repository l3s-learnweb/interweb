package de.l3s.interwebj.tomcat.jaxb.services;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.FIELD)
public class AuthorizationEntity {

    @XmlAttribute(name = "type")
    protected String type;
    @XmlElementWrapper
    @XmlElement(name = "parameter")
    protected List<ParameterEntity> parameters;
    @XmlElement(name = "link")
    protected AuthorizationLinkEntity authorizationLinkEntity;

    public void addParameter(String type, String value) {
        if (parameters == null) {
            parameters = new ArrayList<>();
        }
        parameters.add(new ParameterEntity(type, value));
    }

    public AuthorizationLinkEntity getAuthorizationLinkEntity() {
        return authorizationLinkEntity;
    }

    public void setAuthorizationLinkEntity(AuthorizationLinkEntity authorizationLinkEntity) {
        this.authorizationLinkEntity = authorizationLinkEntity;
    }

    public List<ParameterEntity> getParameters() {
        return parameters;
    }

    public void setParameters(List<ParameterEntity> parameters) {
        this.parameters = parameters;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
