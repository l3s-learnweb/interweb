package de.l3s.interwebj.connector;


import java.util.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.config.*;
import de.l3s.interwebj.query.*;


public abstract class AbstractServiceConnector
    implements ServiceConnector
{
	
	private String name;
	private String baseUrl;
	private AuthCredentials consumerAuthCredentials;
	private Set<String> contentTypes;
	private Configuration configuration;
	

	public AbstractServiceConnector(Configuration configuration)
	{
		this.configuration = configuration;
		init();
	}
	

	public abstract Parameters authenticate(PermissionLevel permissionLevel,
	                                        String callbackUrl)
	    throws InterWebException;
	

	@Override
	public abstract ServiceConnector clone();
	

	public abstract AuthCredentials completeAuthentication(Parameters params)
	    throws InterWebException;
	

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		AbstractServiceConnector other = (AbstractServiceConnector) obj;
		if (name == null)
		{
			if (other.name != null)
			{
				return false;
			}
		}
		else if (!name.equals(other.name))
		{
			return false;
		}
		return true;
	}
	

	public abstract QueryResult get(Query query, AuthCredentials authCredentials)
	    throws InterWebException;
	

	@Override
	public String getBaseUrl()
	{
		return baseUrl;
	}
	

	public Configuration getConfiguration()
	{
		return configuration;
	}
	

	@Override
	public AuthCredentials getConsumerAuthCredentials()
	{
		return consumerAuthCredentials;
	}
	

	@Override
	public Set<String> getContentTypes()
	{
		return contentTypes;
	}
	

	@Override
	public String getName()
	{
		return name;
	}
	

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null)
		    ? 0 : name.hashCode());
		return result;
	}
	

	private void init()
	{
		name = configuration.getValue("name");
		baseUrl = configuration.getValue("base-url");
		contentTypes = new TreeSet<String>(configuration.getValues("content-types.content-type"));
	}
	

	@Override
	public boolean isRegistered()
	{
		return consumerAuthCredentials != null;
	}
	

	public abstract boolean isRegistrationRequired();
	

	public abstract void put(byte[] data,
	                         String contentType,
	                         Parameters params,
	                         AuthCredentials authCredentials)
	    throws InterWebException;
	

	@Override
	public void setConsumerAuthCredentials(AuthCredentials consumerAuthCredentials)
	{
		this.consumerAuthCredentials = consumerAuthCredentials;
	}
	

	@Override
	public boolean supportContentType(String contentType)
	{
		return (contentType != null) && contentTypes.contains(contentType);
	}
	
}
