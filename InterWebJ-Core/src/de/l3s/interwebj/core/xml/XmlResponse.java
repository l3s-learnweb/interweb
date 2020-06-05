package de.l3s.interwebj.core.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class XmlResponse {
    public enum ResponseStatus {
        OK,
        FAILED,
    }

    @XmlAttribute(name = "status")
    protected final ResponseStatus status;

    public XmlResponse() {
        this(ResponseStatus.OK);
    }

    public XmlResponse(ResponseStatus status) {
        this.status = status;
    }

    public ResponseStatus getStatus() {
        return status;
    }
}
