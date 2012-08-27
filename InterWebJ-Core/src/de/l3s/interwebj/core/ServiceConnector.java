package de.l3s.interwebj.core;


import java.io.IOException;
import java.util.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.query.*;
import de.l3s.interwebj.socialsearch.SocialSearchQuery;


public interface ServiceConnector
{
	
	public abstract Parameters authenticate(String callbackUrl)
	    throws InterWebException;
	

	public abstract ServiceConnector clone();
	

	public abstract AuthCredentials completeAuthentication(Parameters params)
	    throws InterWebException;
	

	public abstract QueryResult get(Query query, AuthCredentials authCredentials)
	    throws InterWebException;
	
	public abstract UserSocialNetworkResult getUserSocialNetwork(String userid, AuthCredentials authCredentials)
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
	

	public abstract Parameters getRefinedCallbackParameters(Parameters parameters);
	

	public abstract String getUserId(AuthCredentials userAuthCredentials)
	    throws InterWebException;
	

	public abstract boolean isConnectorRegistrationDataRequired();
	

	public abstract boolean isRegistered();
	

	public abstract boolean isUserRegistrationDataRequired();
	

	public abstract boolean isUserRegistrationRequired();
	

	public abstract ResultItem put(byte[] data,
	                         String contentType,
	                         Parameters params,
	                         AuthCredentials authCredentials)
	    throws InterWebException;
	

	public abstract void revokeAuthentication()
	    throws InterWebException;
	

	public abstract void setAuthCredentials(AuthCredentials consumerAuthCredentials);
	

	public abstract boolean supportContentType(String contentType);
	
	/**
	 * Returns a set of tags. The tags have to belong <i>somehow</i> to the  user.
	 * (Tags that the user has used or the users favorite resources are tagged with ... depends on service)
	 *
	 * @param username the function throws an IllegalArgumentException if the username is not valid at the service
	 * @param maxCount the maximal result count
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IOException 
	 */
	public abstract Set<String> getTags(String username, int maxCount) 
		throws IllegalArgumentException, IOException;
	
	
	/**
	 * Returns a set of usernames that belong <i>somehow</i> to the specified tags
	 * @param tags
	 * @param maxCount the maximal result count
	 * @return
	 * @throws InterWebException 
	 */
	public abstract Set<String> getUsers(Set<String> tags, int maxCount) throws IOException, InterWebException;


	public abstract de.l3s.interwebj.socialsearch.SocialSearchResult get(
			SocialSearchQuery query, AuthCredentials authCredentials);
}
