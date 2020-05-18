package de.l3s.interwebj.tomcat.jaxb.services;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.l3s.interwebj.tomcat.jaxb.XMLResponse;

@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServicesResponse extends XMLResponse {

    @XmlElementWrapper(name = "services")
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
