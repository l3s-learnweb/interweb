package de.l3s.interweb.tomcat.jaxb.services;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

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
