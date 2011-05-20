package de.l3s.interwebj.core;


import java.util.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.query.*;


public interface ServiceConnector
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
	

	public abstract Parameters authenticate(PermissionLevel permissionLevel,
	                                        String callbackUrl)
	    throws InterWebException;
	

	public abstract ServiceConnector clone();
	

	public abstract AuthCredentials completeAuthentication(Parameters params)
	    throws InterWebException;
	

	public abstract QueryResult get(Query query, AuthCredentials authCredentials)
	    throws InterWebException;
	

	public abstract AuthCredentials getAuthCredentials();
	

	public abstract String getBaseUrl();
	

	public abstract Set<String> getContentTypes();
	

	public abstract String getName();
	

	public abstract String getUserId(AuthCredentials userAuthCredentials)
	    throws InterWebException;
	

	public abstract boolean isRegistered();
	

	public abstract boolean isRegistrationRequired();
	

	public abstract void put(byte[] data,
	                         String contentType,
	                         Parameters params,
	                         AuthCredentials authCredentials)
	    throws InterWebException;
	

	public abstract void setAuthCredentials(AuthCredentials consumerAuthCredentials);
	

	public abstract boolean supportContentType(String contentType);
}
