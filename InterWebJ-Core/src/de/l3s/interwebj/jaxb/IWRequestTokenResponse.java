package de.l3s.interwebj.jaxb;


import javax.xml.bind.annotation.*;


@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class IWRequestTokenResponse
{
	
	@XmlRootElement(name = "token")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class IWRequestToken
	{
		
		@XmlAttribute(name = "type")
		protected String type;
		@XmlAttribute(name = "token")
		protected String token;
		

		public String getToken()
		{
			return token;
		}
		

		public String getType()
		{
			return type;
		}
		

		public void setToken(String token)
		{
			this.token = token;
		}
		

		public void setType(String type)
		{
			this.type = type;
		}
		

		@Override
		public String toString()
		{
			StringBuilder builder = new StringBuilder();
			builder.append("RequestToken [");
			if (type != null)
			{
				builder.append("type=");
				builder.append(type);
				builder.append(", ");
			}
			if (token != null)
			{
				builder.append("token=");
				builder.append(token);
			}
			builder.append("]");
			return builder.toString();
		}
		
	}
	

	@XmlAttribute(name = "stat")
	protected String stat;
	@XmlElement(name = "error")
	protected IWError error;
	@XmlElement(name = "token")
	protected IWRequestToken requestToken;
	

	public IWError getError()
	{
		return error;
	}
	

	public IWRequestToken getRequestToken()
	{
		return requestToken;
	}
	

	public String getStat()
	{
		return stat;
	}
	

	public void setError(IWError error)
	{
		this.error = error;
	}
	

	public void setRequestToken(IWRequestToken requestToken)
	{
		this.requestToken = requestToken;
	}
	

	public void setStat(String stat)
	{
		this.stat = stat;
	}
	

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("RequestTokenResponse [");
		if (stat != null)
		{
			builder.append("stat=");
			builder.append(stat);
			builder.append(", ");
		}
		if (requestToken != null)
		{
			builder.append("requestToken=");
			builder.append(requestToken);
			builder.append(", ");
		}
		if (error != null)
		{
			builder.append("error=");
			builder.append(error);
		}
		builder.append("]");
		return builder.toString();
	}
	
}
