package de.l3s.interwebj.tomcat.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.l3s.interwebj.core.query.ResultItem;
import de.l3s.interwebj.core.xml.XmlResponse;

@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class UploadResponse extends XmlResponse {
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
