package de.l3s.interwebj.connector.fedora;


import javax.xml.bind.annotation.*;


@XmlRootElement(name = "Message")
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorMessageEntity
{
	
	@XmlAttribute(name = "ID")
	protected int id;
	@XmlValue
	protected String message;
	

	public int getId()
	{
		return id;
	}
	

	public String getMessage()
	{
		return message;
	}
	

	public void setId(int id)
	{
		this.id = id;
	}
	

	public void setMessage(String message)
	{
		this.message = message;
	}
	
}
