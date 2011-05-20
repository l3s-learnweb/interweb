package de.l3s.interwebj.jaxb.services;


import java.util.*;

import javax.xml.bind.annotation.*;


@XmlAccessorType(XmlAccessType.FIELD)
public class LoginAuthorizationEntity
    extends AuthorizationEntity
{
	
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class ParameterEntity
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
	

	@XmlElementWrapper(name = "parameters")
	@XmlElement(name = "parameter")
	protected List<ParameterEntity> parameters;
	

	public LoginAuthorizationEntity()
	{
		type = "login";
	}
	

	public void addParameter(String type, String value)
	{
		parameters.add(new ParameterEntity(type, value));
	}
	

	public List<ParameterEntity> getParameters()
	{
		return parameters;
	}
	

	public void setParameters(List<ParameterEntity> parameters)
	{
		this.parameters = parameters;
	}
}
