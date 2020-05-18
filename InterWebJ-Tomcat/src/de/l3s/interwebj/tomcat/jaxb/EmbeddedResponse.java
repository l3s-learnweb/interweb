package de.l3s.interwebj.tomcat.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class EmbeddedResponse extends XMLResponse {

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
