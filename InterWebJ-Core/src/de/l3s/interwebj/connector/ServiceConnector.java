package de.l3s.interwebj.connector;


import java.util.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.query.*;


public abstract class ServiceConnector
{
	
	public enum PermissionLevel
	{
		NONE, READ, WRITE, DELETE;
		
		public int getCode()
		{
			return ordinal();
		}
		

		public String getName()
		{
			return name().toLowerCase();
		}
		

		public static PermissionLevel getPermissionLevel(int code)
		{
			if (code >= 0 && code < PermissionLevel.values().length)
			{
				return PermissionLevel.values()[code];
			}
			return NONE;
		}
		

		public static PermissionLevel getPermissionLevel(String name)
		{
			try
			{
				return PermissionLevel.valueOf(name.toUpperCase());
			}
			catch (IllegalArgumentException e)
			{
				return NONE;
			}
		}
	}
	

	private String name;
	private String baseUrl;
	private AuthCredentials consumerAuthCredentials;
	private Set<String> contentTypes;
	

	public ServiceConnector(String name, String baseUrl)
	{
		this.name = name;
		this.baseUrl = baseUrl;
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
		ServiceConnector other = (ServiceConnector) obj;
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
	

	public String getBaseUrl()
	{
		return baseUrl;
	}
	

	public AuthCredentials getConsumerAuthCredentials()
	{
		return consumerAuthCredentials;
	}
	

	public Set<String> getContentTypes()
	{
		return contentTypes;
	}
	

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
	

	protected abstract void init();
	

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
	

	public void setConsumerAuthCredentials(AuthCredentials consumerAuthCredentials)
	{
		this.consumerAuthCredentials = consumerAuthCredentials;
	}
	

	public void setContentTypes(Set<String> contentTypes)
	{
		this.contentTypes = contentTypes;
	}
	

	public boolean supportContentType(String contentType)
	{
		return (contentType != null) && contentTypes.contains(contentType);
	}
	
}
