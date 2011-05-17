package de.l3s.interwebj.core;


import static de.l3s.interwebj.util.Assertions.*;
import de.l3s.interwebj.*;


public class Consumer
{
	
	private String name;
	private String url;
	private String description;
	private AuthCredentials authCredentials;
	

	public Consumer(String name,
	                String url,
	                String description,
	                AuthCredentials authCredentials)
	{
		notNull(name, "name");
		notNull(authCredentials, "authCredentials");
		this.name = name;
		this.url = url;
		this.description = description;
		this.authCredentials = authCredentials;
	}
	

	public AuthCredentials getAuthCredentials()
	{
		return authCredentials;
	}
	

	public String getDescription()
	{
		return description;
	}
	

	public String getName()
	{
		return name;
	}
	

	public String getUrl()
	{
		return url;
	}
}
