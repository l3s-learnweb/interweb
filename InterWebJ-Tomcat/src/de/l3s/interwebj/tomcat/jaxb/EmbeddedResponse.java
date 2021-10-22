package de.l3s.interwebj.tomcat.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class EmbeddedResponse {

    @XmlElement(name = "embedded")
    protected String embedded;

    public EmbeddedResponse() {
    }

    public EmbeddedResponse(String embedded) {
        this.embedded = embedded;
    }

    public String getEmbedded() {
        return embedded;
    }

    public void setEmbedded(String embedded) {
        this.embedded = embedded;
    }

}
