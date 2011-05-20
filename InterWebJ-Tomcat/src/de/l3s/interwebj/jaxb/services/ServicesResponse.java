package de.l3s.interwebj.jaxb.services;


import java.util.*;

import javax.xml.bind.annotation.*;

import de.l3s.interwebj.jaxb.*;


@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServicesResponse
    extends XMLResponse
{
	
	@XmlElementWrapper(name = "services")
	@XmlElement(name = "service")
	protected List<ServiceEntity> serviceEntities;
	

	public ServicesResponse()
	{
	}
	

	public List<ServiceEntity> getServiceEntities()
	{
		return serviceEntities;
	}
	

	public void setServiceEntities(List<ServiceEntity> services)
	{
		serviceEntities = services;
	}
	
}
