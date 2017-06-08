package de.l3s.interwebj.jaxb.services;

import javax.xml.bind.annotation.*;

import de.l3s.interwebj.jaxb.*;

@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceResponse extends XMLResponse
{

    @XmlElement(name = "service")
    protected ServiceEntity serviceEntity;

    public ServiceResponse()
    {
    }

    public ServiceEntity getServiceEntity()
    {
	return serviceEntity;
    }

    public void setServiceEntity(ServiceEntity serviceEntity)
    {
	this.serviceEntity = serviceEntity;
    }

}
