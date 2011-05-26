package de.l3s.interwebj.connector.flickr;


import java.net.*;
import java.util.*;

import com.aetrion.flickr.*;
import com.aetrion.flickr.auth.*;
import com.aetrion.flickr.util.*;


public class ExtraAuthInterface
    extends AuthInterface
{
	
	private String extraApiKey;
	private String extraSharedSecret;
	private Transport extraTransportAPI;
	private String extraParameter;
	

	public ExtraAuthInterface(String apiKey,
	                          String sharedSecret,
	                          Transport transport,
	                          String extraParameter)
	{
		super(apiKey, sharedSecret, transport);
		extraApiKey = apiKey;
		extraSharedSecret = sharedSecret;
		extraTransportAPI = transport;
		this.extraParameter = extraParameter;
	}
	

	@Override
	public URL buildAuthenticationUrl(Permission permission, String frob)
	    throws MalformedURLException
	{
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(new Parameter("api_key", extraApiKey));
		parameters.add(new Parameter("perms", permission.toString()));
		parameters.add(new Parameter("frob", frob));
		parameters.add(new Parameter("extra", extraParameter));
		parameters.add(new Parameter("api_sig",
		                             AuthUtilities.getSignature(extraSharedSecret,
		                                                        parameters)));
		String host = "www.flickr.com";
		int port = extraTransportAPI.getPort();
		String path = "/services/auth/";
		return UrlUtilities.buildUrl(host, port, path, parameters);
	}
}
