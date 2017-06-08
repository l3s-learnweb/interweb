package de.l3s.interwebj.core;

import java.io.IOException;
import java.util.*;

import org.apache.commons.lang.NotImplementedException;

import de.l3s.interwebj.*;
import de.l3s.interwebj.config.*;
import de.l3s.interwebj.db.Database;
import de.l3s.interwebj.query.Query;
import de.l3s.interwebj.query.QueryResult;
import de.l3s.interwebj.query.ResultItem;
import de.l3s.interwebj.query.UserSocialNetworkResult;

public abstract class AbstractServiceConnector implements ServiceConnector
{

    private String name;
    private String baseUrl;
    private AuthCredentials consumerAuthCredentials;
    private Set<String> contentTypes;
    private Configuration configuration;

    public AbstractServiceConnector()
    {
	// TODO Auto-generated constructor stub
    }

    public AbstractServiceConnector(Configuration configuration)
    {
	this.configuration = configuration;
	init();
    }

    @Override
    public abstract ServiceConnector clone();

    @Override
    public boolean equals(Object obj)
    {
	if(this == obj)
	{
	    return true;
	}
	if(obj == null)
	{
	    return false;
	}
	if(getClass() != obj.getClass())
	{
	    return false;
	}
	AbstractServiceConnector other = (AbstractServiceConnector) obj;
	if(name == null)
	{
	    if(other.name != null)
	    {
		return false;
	    }
	}
	else if(!name.equals(other.name))
	{
	    return false;
	}
	return true;
    }

    @Override
    public Parameters authenticate(String callbackUrl) throws InterWebException
    {
	throw new NotImplementedException();
    }

    @Override
    public Parameters authenticate(String callbackUrl, Parameters parameters) throws InterWebException
    {
	return authenticate(callbackUrl);
    }

    @Override
    public AuthCredentials getAuthCredentials()
    {
	return consumerAuthCredentials;
    }

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
    public Parameters getRefinedCallbackParameters(Parameters parameters)
    {
	Parameters refinedParameters = new Parameters();
	refinedParameters.add(parameters, true);
	return parameters;
    }

    @Override
    public int hashCode()
    {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	return result;
    }

    @Override
    public boolean isRegistered()
    {
	return consumerAuthCredentials != null;
    }

    @Override
    public void setAuthCredentials(AuthCredentials consumerAuthCredentials)
    {
	this.consumerAuthCredentials = consumerAuthCredentials;
    }

    @Override
    public boolean supportContentType(String contentType)
    {
	return (contentType != null) && contentTypes.contains(contentType);
    }

    @Override
    public String generateCallbackUrl(String baseApiUrl, Parameters parameters)
    {
	return baseApiUrl + "callback?" + parameters.toQueryString();
    }

    @Override
    public InterWebPrincipal getPrincipal(Parameters parameters) throws InterWebException
    {
	for(String parameter : parameters.keySet())
	{
	    if(parameter.equals(Parameters.IWJ_USER_ID))
	    {
		String userName = parameters.get(parameter);

		Database database = Environment.getInstance().getDatabase();
		InterWebPrincipal principal = database.readPrincipalByName(userName);
		if(principal == null)
		{
		    throw new InterWebException("User [" + userName + "] not found");
		}
		return principal;
	    }
	}

	throw new InterWebException("Unable to fetch user name from the callback URL");
    }

    private void init()
    {
	name = configuration.getValue("name");
	baseUrl = configuration.getValue("base-url");
	contentTypes = new TreeSet<String>(configuration.getValues("content-types.content-type"));
    }
}
