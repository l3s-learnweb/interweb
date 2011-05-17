package de.l3s.interwebj.jaxb;


import java.io.*;

import javax.xml.bind.*;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang.text.*;


@XmlAccessorType(XmlAccessType.FIELD)
public class XMLResponse
{
	
	@XmlAttribute(name = "stat")
	protected String stat;
	@XmlElement(name = "error")
	protected ErrorEntity error;
	

	public XMLResponse()
	{
		stat = "ok";
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
	

	@Override
	public String toString()
	{
		StrBuilder sb = new StrBuilder();
		try
		{
			JAXBContext context = JAXBContext.newInstance(getClass());
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			m.marshal(this, baos);
			sb.append(baos.toString("UTF8"));
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return sb.toString();
	}
}
