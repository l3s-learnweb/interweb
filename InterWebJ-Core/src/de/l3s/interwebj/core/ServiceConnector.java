package de.l3s.interwebj.core;


import java.util.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.query.*;


public interface ServiceConnector
{
	
	public abstract Parameters authenticate(String callbackUrl)
	    throws InterWebException;
	

	public abstract ServiceConnector clone();
	

	public abstract AuthCredentials completeAuthentication(Parameters params)
	    throws InterWebException;
	

	public abstract QueryResult get(Query query, AuthCredentials authCredentials)
	    throws InterWebException;
	

	public abstract AuthCredentials getAuthCredentials();
	

	public abstract String getBaseUrl();
	

	public abstract Set<String> getContentTypes();
	

	public abstract String getEmbedded(AuthCredentials authCredentials,
	                                   String url,
	                                   int width,
	                                   int height)
	    throws InterWebException;
	

	public abstract String getName();
	

	public abstract String getUserId(AuthCredentials userAuthCredentials)
	    throws InterWebException;
	

	public abstract boolean isConnectorRegistrationDataRequired();
	

	public abstract boolean isConnectorRegistered();
	

	public abstract boolean isUserRegistrationDataRequired();
	

	public abstract void put(byte[] data,
	                         String contentType,
	                         Parameters params,
	                         AuthCredentials authCredentials)
	    throws InterWebException;
	

	public abstract Parameters getRefinedCallbackParameters(Parameters parameters);
	

	public abstract void revokeAuthentication()
	    throws InterWebException;
	

	public abstract void setAuthCredentials(AuthCredentials consumerAuthCredentials);
	

	public abstract boolean supportContentType(String contentType);
}
