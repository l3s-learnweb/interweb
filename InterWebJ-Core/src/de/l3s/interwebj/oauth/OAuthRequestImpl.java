package de.l3s.interwebj.oauth;


import java.net.*;
import java.util.*;

import com.sun.jersey.oauth.signature.*;


public class OAuthRequestImpl
    implements OAuthRequest
{
	
	private String requestMethod;
	private URL requestURL;
	private Map<String, List<String>> parameters;
	private Map<String, List<String>> headers;
	

	public OAuthRequestImpl(String requestMethod, URL requestURL)
	{
		this.requestMethod = requestMethod;
		this.requestURL = requestURL;
		parameters = new TreeMap<String, List<String>>();
		headers = new TreeMap<String, List<String>>();
	}
	

	@Override
	public void addHeaderValue(String name, String value)
	    throws IllegalStateException
	{
		List<String> oldValues = headers.get(name);
		if (oldValues == null)
		{
			oldValues = new ArrayList<String>();
		}
		oldValues.add(value);
		headers.put(name, oldValues);
	}
	

	public void addParameterValue(String name, String value)
	{
		List<String> oldValues = parameters.get(name);
		if (oldValues == null)
		{
			oldValues = new ArrayList<String>();
		}
		oldValues.add(value);
		parameters.put(name, oldValues);
	}
	

	@Override
	public List<String> getHeaderValues(String name)
	{
		return headers.get(name);
	}
	

	@Override
	public Set<String> getParameterNames()
	{
		return parameters.keySet();
	}
	

	@Override
	public List<String> getParameterValues(String name)
	{
		return parameters.get(name);
	}
	

	@Override
	public String getRequestMethod()
	{
		return requestMethod;
	}
	

	@Override
	public URL getRequestURL()
	{
		return requestURL;
	}
	

	@Override
	public String toString()
	{
		String request = requestURL.toExternalForm();
		if (getParameterNames().size() > 0)
		{
			request += "?";
		}
		for (Iterator<String> iterator = getParameterNames().iterator(); iterator.hasNext();)
		{
			String name = iterator.next();
			List<String> parameterValues = getParameterValues(name);
			for (String value : parameterValues)
			{
				request += name + "=" + value + "&";
			}
			if (iterator.hasNext())
			{
				request += "&";
			}
		}
		return request;
	}
	

	public static void main(String[] args)
	    throws Exception
	{
		OAuthRequestImpl oauthRequest = new OAuthRequestImpl("GET",
		                                                     new URL("https://www.google.com/accounts/OAuthGetAccessToken"));
		OAuthParameters oauthParams = new OAuthParameters();
		oauthParams.version("1.0");
		oauthParams.nonce("***REMOVED***");
		oauthParams.timestamp("1300360338");
		oauthParams.consumerKey("***REMOVED***");
		oauthParams.verifier("***REMOVED***");
		oauthParams.token("***REMOVED***");
		oauthParams.signatureMethod("HMAC-SHA1");
		OAuthSecrets oauthSecrets = new OAuthSecrets();
		oauthSecrets.consumerSecret("***REMOVED***");
		oauthSecrets.tokenSecret("***REMOVED***");
		System.out.println(OAuthSignature.generate(oauthRequest,
		                                           oauthParams,
		                                           oauthSecrets));
	}
}
