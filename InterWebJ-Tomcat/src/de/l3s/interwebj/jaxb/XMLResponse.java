package de.l3s.interwebj.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class XMLResponse
{

    public static final String OK = "ok";
    public static final String FAILED = "fail";

    @XmlAttribute(name = "stat")
    protected String stat;
    @XmlElement(name = "error")
    protected ErrorEntity error;

    public XMLResponse()
    {
	stat = OK;
    }

    public ErrorEntity getError()
    {
	return error;
    }

    public String getStat()
    {
	return stat;
    }

    public void setError(ErrorEntity error)
    {
	this.error = error;
    }

}
