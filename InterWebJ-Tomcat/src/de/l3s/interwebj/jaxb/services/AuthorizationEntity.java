package de.l3s.interwebj.jaxb.services;

import java.util.*;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class AuthorizationEntity
{

    @XmlAttribute(name = "type")
    protected String type;
    @XmlElementWrapper(name = "parameters")
    @XmlElement(name = "parameter")
    protected List<ParameterEntity> parameters;
    @XmlElement(name = "link")
    protected AuthorizationLinkEntity authorizationLinkEntity;

    public void addParameter(String type, String value)
    {
	if(parameters == null)
	{
	    parameters = new ArrayList<ParameterEntity>();
	}
	parameters.add(new ParameterEntity(type, value));
    }

    public AuthorizationLinkEntity getAuthorizationLinkEntity()
    {
	return authorizationLinkEntity;
    }

    public List<ParameterEntity> getParameters()
    {
	return parameters;
    }

    public String getType()
    {
	return type;
    }

    public void setAuthorizationLinkEntity(AuthorizationLinkEntity authorizationLinkEntity)
    {
	this.authorizationLinkEntity = authorizationLinkEntity;
    }

    public void setParameters(List<ParameterEntity> parameters)
    {
	this.parameters = parameters;
    }

    public void setType(String type)
    {
	this.type = type;
    }

}
