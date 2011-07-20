package de.l3s.interweb;


import java.io.*;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.oauth.client.OAuthClientFilter;
import com.sun.jersey.oauth.signature.HMAC_SHA1;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;

import de.l3s.interwebj.AuthCredentials;



public class Util
{

	public static WebResource createWebResource(String apiUrl,
            AuthCredentials consumerAuthCredentials,
            AuthCredentials userAuthCredentials)
{
Client client = Client.create();
WebResource resource = client.resource(apiUrl);
OAuthParameters oauthParams = new OAuthParameters();
oauthParams.consumerKey(consumerAuthCredentials.getKey());
if (userAuthCredentials != null)
{
oauthParams.token(userAuthCredentials.getKey());
}
oauthParams.signatureMethod(HMAC_SHA1.NAME);
oauthParams.timestamp();
oauthParams.nonce();
oauthParams.version();
OAuthSecrets oauthSecrets = new OAuthSecrets();
oauthSecrets.consumerSecret(consumerAuthCredentials.getSecret());
if (userAuthCredentials != null
&& userAuthCredentials.getSecret() != null)
{
oauthSecrets.tokenSecret(userAuthCredentials.getSecret());
}
OAuthClientFilter filter = new OAuthClientFilter(client.getProviders(),
                     oauthParams,
                     oauthSecrets);
resource.addFilter(filter);
System.out.println("requesting url: " + resource.toString());
return resource;
}

	
	public static void redirect(String redirectPath)
	    throws IOException
	{
		/*
		ExternalContext externalContext = getExternalContext();
		externalContext.redirect(redirectPath);
		*/
	}
	

	// ------------------------ 
	
	public static int time()
	{
		return (int) (System.currentTimeMillis() / 1000);
	}
}
