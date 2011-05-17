package de.l3s.interwebj.jaxb;


import javax.xml.bind.annotation.*;


@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorResponse
    extends XMLResponse
{
	
	public ErrorResponse()
	{
	}
	

	public ErrorResponse(int code, String message)
	{
		stat = "fail";
		error = new ErrorEntity(code, message);
	}
}
