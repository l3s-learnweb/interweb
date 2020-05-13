package de.l3s.interwebj.tomcat.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class UploadResponse extends XMLResponse
{
    @XmlElement(name = "result")
    protected SearchResultEntity result;

    public UploadResponse()
    {
    }

    public UploadResponse(SearchResultEntity result)
    {
	this.result = result;
    }

    public SearchResultEntity getResult()
    {
	return result;
    }

    public void setResult(SearchResultEntity result)
    {
	this.result = result;
    }

}
