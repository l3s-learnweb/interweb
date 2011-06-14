package de.l3s.interwebj.core;


import java.util.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.config.*;


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
	

	@Override
	public abstract ServiceConnector clone();
	

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
		result = prime * result + ((name == null)
		    ? 0 : name.hashCode());
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
	

	private void init()
	{
		name = configuration.getValue("name");
		baseUrl = configuration.getValue("base-url");
		contentTypes = new TreeSet<String>(configuration.getValues("content-types.content-type"));
	}
}
