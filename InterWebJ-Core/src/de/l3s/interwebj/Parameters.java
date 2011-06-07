package de.l3s.interwebj;


import java.io.*;
import java.net.*;
import java.util.*;


public class Parameters
{
	
	public static final String AUTHORIZATION_URL = "authorization_url";
	public static final String USER_KEY = "user_key";
	public static final String USER_SECRET = "user_secret";
	public static final String OAUTH_TOKEN = "oauth_token";
	public static final String OAUTH_TOKEN_SECRET = "oauth_token_secret";
	public static final String OAUTH_VERIFIER = "oauth_verifier";
	public static final String OAUTH_CONSUMER_KEY = "oauth_consumer_key";
	public static final String TITLE = "title";
	public static final String DESCRIPTION = "description";
	public static final String FILENAME = "filename";
	public static final String TAGS = "tags";
	public static final String PRIVACY = "privacy";
	public static final String CLIENT_TYPE = "client_type";
	public static final String CONNECTOR_NAME = "connector_name";
	public static final String TOKEN = "token";
	public static final String CONSUMER_KEY = "consumer_key";
	public static final String CALLBACK = "callback";
	public static final String IWJ_USER_ID = "iwj_user_id";
	public static final String IWJ_CONNECTOR_ID = "iwj_connector_id";
	
	private Map<String, String> parameters;
	

	public Parameters()
	{
		parameters = new TreeMap<String, String>();
	}
	

	public void add(Map<String, String> parameters)
	{
		this.parameters.putAll(parameters);
	}
	

	public void add(Parameters parameters)
	{
		for (String parameter : parameters.keySet())
		{
			this.parameters.put(parameter, parameters.get(parameter));
		}
	}
	

	public void add(String name, String value)
	{
		parameters.put(name, value);
	}
	

	public void addDecoded(String name, String value)
	{
		try
		{
			String decodedValue = URLDecoder.decode(value, "UTF-8");
			add(name, decodedValue);
		}
		catch (UnsupportedEncodingException shouldNeverOccur)
		{
			shouldNeverOccur.printStackTrace();
		}
	}
	

	public void addMultivaluedParams(Map<String, String[]> parameters)
	{
		for (String name : parameters.keySet())
		{
			String[] values = parameters.get(name);
			String value = null;
			if (values != null && values.length > 0)
			{
				value = values[0];
			}
			add(name, value);
		}
	}
	

	public void addQueryParameters(String query)
	{
		int startQueryIndex = query.indexOf("?") == -1
		    ? 0 : query.indexOf("?") + 1;
		query = query.substring(startQueryIndex);
		String[] params = query.split("&");
		for (String param : params)
		{
			String[] paramPair = param.split("=");
			String name = paramPair[0];
			String value = null;
			if (paramPair.length > 1)
			{
				value = paramPair[1];
			}
			addDecoded(name, value);
		}
	}
	

	public boolean containsKey(String name)
	{
		return parameters.containsKey(name);
	}
	

	public String get(String name)
	{
		return parameters.get(name);
	}
	

	public String get(String name, String defaultValue)
	{
		String value = get(name);
		if (value == null)
		{
			return defaultValue;
		}
		return value;
	}
	

	public boolean hasParameter(String key)
	{
		return parameters.containsKey(key);
	}
	

	public Set<String> keySet()
	{
		return parameters.keySet();
	}
	

	public String remove(String name)
	{
		return parameters.remove(name);
	}
	

	public String toQueryString()
	{
		StringBuilder sb = new StringBuilder();
		for (Iterator<String> iterator = parameters.keySet().iterator(); iterator.hasNext();)
		{
			String name = iterator.next();
			String value = parameters.get(name);
			sb.append(name).append("=").append(value);
			if (iterator.hasNext())
			{
				sb.append('&');
			}
		}
		return sb.toString();
	}
	

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Parameters ");
		if (parameters != null)
		{
			builder.append(parameters);
		}
		return builder.toString();
	}
}
