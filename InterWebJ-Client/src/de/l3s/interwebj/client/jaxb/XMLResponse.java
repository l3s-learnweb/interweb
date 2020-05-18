package de.l3s.interwebj.client.jaxb;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class XMLResponse {

    public static final String OK = "ok";
    public static final String FAILED = "fail";

    @XmlAttribute(name = "stat")
    protected String stat;
    @XmlElement(name = "error")
    protected ErrorEntity error;

    public XMLResponse() {
        stat = OK;
    }

    public ErrorEntity getError() {
        return error;
    }

    public void setError(ErrorEntity error) {
        this.error = error;
    }

    public String getStat() {
        return stat;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        try {
            JAXBContext context = JAXBContext.newInstance(getClass());
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            m.marshal(this, baos);
            sb.append(baos.toString(StandardCharsets.UTF_8));
        } catch (JAXBException e) {
            sb.append("Interweb processing error: ").append(e.getMessage());
        }
        return sb.toString();
    }
}
