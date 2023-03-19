package de.l3s.interweb.tomcat.jaxb.services;

import java.util.List;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServicesResponse {

    @XmlElementWrapper
    @XmlElement(name = "service")
    protected List<ServiceEntity> serviceEntities;

    public ServicesResponse() {
    }

    public List<ServiceEntity> getServiceEntities() {
        return serviceEntities;
    }

    public void setServiceEntities(List<ServiceEntity> services) {
        serviceEntities = services;
    }

}
