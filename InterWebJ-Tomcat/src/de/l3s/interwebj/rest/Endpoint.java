package de.l3s.interwebj.rest;


import javax.ws.rs.core.*;

import com.sun.jersey.api.client.*;
import com.sun.jersey.api.core.*;
import com.sun.jersey.oauth.client.*;
import com.sun.jersey.oauth.server.*;
import com.sun.jersey.oauth.signature.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.db.*;


public class Endpoint
{
	
	@Context
	private HttpContext httpContext;
	

	public HttpContext getHttpContext()
	{
		return httpContext;
	}
	

	public OAuthParameters getOAuthParameters()
	{
		OAuthServerRequest request = new OAuthServerRequest(httpContext.getRequest());
		OAuthParameters params = new OAuthParameters();
		params.readRequest(request);
		return params;
	}
	

	public InterWebPrincipal getPrincipal()
	{
		OAuthServerRequest request = new OAuthServerRequest(httpContext.getRequest());
		OAuthParameters params = new OAuthParameters();
		params.readRequest(request);
		String token = params.getToken();
		if (token == null)
		{
			return null;
		}
		Database database = Environment.getInstance().getDatabase();
		InterWebPrincipal principal = database.readPrincipalByKey(token);
		return principal;
	}
	

	public static WebResource createWebResource(String url,
	                                            AuthCredentials consumerCredentials,
	                                            AuthCredentials userCredentials)
	{
		Client client = Client.create();
		WebResource resource = client.resource(url);
		OAuthParameters oauthParams = new OAuthParameters();
		oauthParams.consumerKey(consumerCredentials.getKey());
		if (userCredentials != null)
		{
			oauthParams.token(userCredentials.getKey());
		}
		oauthParams.signatureMethod(HMAC_SHA1.NAME);
		oauthParams.timestamp();
		oauthParams.nonce();
		oauthParams.version();
		OAuthSecrets oauthSecrets = new OAuthSecrets();
		oauthSecrets.consumerSecret(consumerCredentials.getSecret());
		if (userCredentials != null && userCredentials.getSecret() != null)
		{
			oauthSecrets.tokenSecret(userCredentials.getSecret());
		}
		OAuthClientFilter filter = new OAuthClientFilter(client.getProviders(),
		                                                 oauthParams,
		                                                 oauthSecrets);
		resource.addFilter(filter);
		return resource;
	}
}
