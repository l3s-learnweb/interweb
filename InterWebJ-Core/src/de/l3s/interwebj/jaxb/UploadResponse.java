package de.l3s.interwebj.jaxb;


import javax.xml.bind.annotation.*;


@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class UploadResponse
    extends XMLResponse
{
	
	public UploadResponse()
	{
	}
}