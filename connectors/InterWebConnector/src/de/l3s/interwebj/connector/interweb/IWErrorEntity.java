package de.l3s.interwebj.connector.interweb;


import javax.xml.bind.annotation.*;


@XmlRootElement(name = "error")
@XmlAccessorType(XmlAccessType.FIELD)
public class IWErrorEntity
{
	
	@XmlAttribute(name = "code")
	protected int code;
	@XmlAttribute(name = "message")
	protected String message;
	

	public IWErrorEntity()
	{
	}
	

	public IWErrorEntity(int code, String message)
	{
		this();
		this.code = code;
		this.message = message;
	}
	

	public int getCode()
	{
		return code;
	}
	

	public String getMessage()
	{
		return message;
	}
	

	public void setCode(int code)
	{
		this.code = code;
	}
	

	public void setMessage(String message)
	{
		this.message = message;
	}
	

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("ResponseError [code=");
		builder.append(code);
		builder.append(", ");
		if (message != null)
		{
			builder.append("message=");
			builder.append(message);
		}
		builder.append("]");
		return builder.toString();
	}
}
