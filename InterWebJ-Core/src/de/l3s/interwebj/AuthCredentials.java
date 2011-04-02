package de.l3s.interwebj;


public class AuthCredentials
{
	
	private String key;
	private String secret;
	

	public AuthCredentials(String key)
	{
		this(key, null);
	}
	

	public AuthCredentials(String key, String secret)
	{
		if (key == null)
		{
			throw new NullPointerException("Argument [key] can not be null");
		}
		this.key = key;
		this.secret = secret;
	}
	

	public String getKey()
	{
		return key;
	}
	

	public String getSecret()
	{
		return secret;
	}
	

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("AuthCredentials [");
		if (key != null)
		{
			builder.append("key=");
			builder.append(key);
			builder.append(", ");
		}
		if (secret != null)
		{
			builder.append("secret=");
			builder.append(secret);
		}
		builder.append("]");
		return builder.toString();
	}
	
}
