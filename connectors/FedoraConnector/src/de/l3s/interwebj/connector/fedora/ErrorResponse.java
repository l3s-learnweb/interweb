package de.l3s.interwebj.connector.fedora;


import java.io.*;

import javax.xml.bind.*;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang.text.*;


@XmlRootElement(name = "SlideShareServiceError")
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorResponse
{
	
	@XmlElement(name = "Message")
	protected ErrorMessageEntity errorMessage;
	

	public ErrorMessageEntity getErrorMessage()
	{
		return errorMessage;
	}
	

	public void setErrorMessage(ErrorMessageEntity errorMessage)
	{
		this.errorMessage = errorMessage;
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
