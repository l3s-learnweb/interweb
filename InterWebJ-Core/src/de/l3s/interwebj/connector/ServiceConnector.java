package de.l3s.interwebj.connector;


import java.util.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.oauth.*;
import de.l3s.interwebj.query.*;


public abstract class ServiceConnector
{
	
	public enum PermissionLevel
	{
		NONE("none"), READ("read"), WRITE("write"), DELETE("delete");
		
		private String name;
		

		PermissionLevel(String name)
		{
			this.name = name;
		}
		

		public String getName()
		{
			return name;
		}
		

		public static PermissionLevel getPermissionLevel(String name)
		{
			if ("read".equals(name))
			{
				return READ;
			}
			else if ("write".equals(name))
			{
				return WRITE;
			}
			else if ("delete".equals(name))
			{
				return DELETE;
			}
			return NONE;
		}
		
	}
	

	private String name;
	private String baseUrl;
	private AuthData consumerAuthData;
	private Set<String> contentTypes;
	

	public ServiceConnector(String name, String baseUrl)
	{
		this.name = name;
		this.baseUrl = baseUrl;
		init();
	}
	

	public abstract OAuthParams authenticate(PermissionLevel permissionLevel,
	                                         String callbackUrl)
	    throws InterWebException;
	

	public abstract AuthData completeAuthentication(Map<String, String[]> params)
	    throws InterWebException;
	

	public abstract QueryResult get(Query query, AuthData authData)
	    throws InterWebException;
	

	public String getBaseUrl()
	{
		return baseUrl;
	}
	

	public AuthData getConsumerAuthData()
	{
		return consumerAuthData;
	}
	

	public Set<String> getContentTypes()
	{
		return contentTypes;
	}
	

	public String getName()
	{
		return name;
	}
	

	protected abstract void init();
	

	public boolean isRegistered()
	{
		return consumerAuthData != null;
	}
	

	public abstract void put(byte[] data,
	                         Map<String, String> params,
	                         AuthData authData)
	    throws InterWebException;
	

	public void setConsumerAuthData(AuthData consumerAuthData)
	{
		this.consumerAuthData = consumerAuthData;
	}
	

	public void setContentTypes(Set<String> contentTypes)
	{
		this.contentTypes = contentTypes;
	}
	

	public abstract boolean supportOAuth();
	
}
