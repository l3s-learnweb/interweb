package de.l3s.interwebj.connector;


import java.util.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.query.*;


public abstract class ServiceConnector
{
	
	public enum PermissionLevel
	{
		NONE("none", 0), READ("read", 1), WRITE("write", 2),
		DELETE("delete", 3);
		
		private String name;
		private int code;
		

		PermissionLevel(String name, int code)
		{
			this.name = name;
			this.code = code;
		}
		

		public int getCode()
		{
			return code;
		}
		

		public String getName()
		{
			return name;
		}
		

		public static PermissionLevel getPermissionLevel(int code)
		{
			switch (code)
			{
				case 1:
					return READ;
				case 2:
					return WRITE;
				case 3:
					return DELETE;
			}
			return NONE;
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
	

	public abstract void put(byte[] data,
	                         Parameters params,
	                         AuthCredentials authCredentials)
	    throws InterWebException;
	

	public abstract boolean requestRegistrationData();
	

	public void setConsumerAuthCredentials(AuthCredentials consumerAuthCredentials)
	{
		this.consumerAuthCredentials = consumerAuthCredentials;
	}
	

	public void setContentTypes(Set<String> contentTypes)
	{
		this.contentTypes = contentTypes;
	}
	
}
