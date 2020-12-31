package de.l3s.interwebj.tomcat.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import de.l3s.interwebj.core.query.ResultItem;

@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class UploadResponse {
    @XmlElement(name = "result")
    protected ResultItem result;

    public UploadResponse() {
    }

    public UploadResponse(ResultItem result) {
        this.result = result;
    }

    public ResultItem getResult() {
        return result;
    }

    public void setResult(ResultItem result) {
        this.result = result;
    }

}
