package de.l3s.interwebj.jaxb.services;


import javax.xml.bind.annotation.*;


@XmlAccessorType(XmlAccessType.FIELD)
public class ParameterEntity
{
	
	@XmlAttribute
	protected String type;
	@XmlValue
	protected String value;
	

	public ParameterEntity()
	{
	}
	

	public ParameterEntity(String type, String value)
	{
		this.type = type;
		this.value = value;
	}
	

	public String getType()
	{
		return type;
	}
	

	public String getValue()
	{
		return value;
	}
	

	public void setType(String type)
	{
		this.type = type;
	}
	

	public void setValue(String value)
	{
		this.value = value;
	}
}
