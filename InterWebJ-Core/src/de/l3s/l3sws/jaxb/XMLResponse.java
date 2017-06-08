package de.l3s.l3sws.jaxb;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
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
	stat = FAILED;
	this.error = error;
    }

    @Override
    public String toString()
    {
	StringBuilder sb = new StringBuilder();
	try
	{
	    JAXBContext context = JAXBContext.newInstance(getClass());
	    Marshaller m = context.createMarshaller();
	    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    m.marshal(this, baos);
	    sb.append(baos.toString("UTF8"));
	}
	catch(JAXBException e)
	{
	    e.printStackTrace();
	}
	catch(UnsupportedEncodingException e)
	{
	    e.printStackTrace();
	}
	return sb.toString();
    }
}
