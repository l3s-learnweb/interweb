package de.l3s.interwebj.rest;


import java.awt.*;
import java.io.*;
import java.net.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.sun.jersey.api.client.*;
import com.sun.jersey.api.core.*;
import com.sun.jersey.oauth.client.*;
import com.sun.jersey.oauth.server.*;
import com.sun.jersey.oauth.signature.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.db.*;
import de.l3s.interwebj.jaxb.*;
import de.l3s.interwebj.util.*;


@Path("/oauth")
public class OAuth
{
	
	@Context
	HttpContext httpContext;
	

	@GET
	@Path("/OAuthAuthorizeToken")
	@Produces(MediaType.APPLICATION_XML)
	public XMLResponse authorizeToken(@QueryParam("oauth_token") String requestToken,
	                                  @QueryParam("oauth_callback") String callbackUrl)
	{
		URI uri = httpContext.getUriInfo().getBaseUri().resolve("../view/authorize_consumer.xhtml");
		UriBuilder builder = UriBuilder.fromUri(uri);
		builder = builder.queryParam("oauth_token", requestToken);
		if (callbackUrl != null)
		{
			builder = builder.queryParam("oauth_callback", callbackUrl);
		}
		uri = builder.build();
		Response response = Response.seeOther(uri).build();
		throw new WebApplicationException(response);
	}
	

	@GET
	@Path("/OAuthGetAccessToken")
	@Produces(MediaType.APPLICATION_XML)
	public XMLResponse getAccessToken()
	{
		OAuthServerRequest request = new OAuthServerRequest(httpContext.getRequest());
		OAuthParameters params = new OAuthParameters();
		params.readRequest(request);
		String token = params.getToken();
		Engine engine = Environment.getInstance().getEngine();
		ExpirableMap<String, Object> expirableMap = engine.getExpirableMap();
		expirableMap.remove("access_token:" + token);
		expirableMap.remove("consumer_token:" + token);
		InterWebPrincipal principal = (InterWebPrincipal) expirableMap.remove("principal:"
		                                                                      + token);
		Database database = Environment.getInstance().getDatabase();
		AuthCredentials accessToken = RandomGenerator.getInstance().nextOAuthCredentials();
		principal.setOauthCredentials(accessToken);
		database.updatePrincipal(principal);
		OAuthAccessTokenResponse response = new OAuthAccessTokenResponse(accessToken);
		return response;
	}
	

	@GET
	@Path("/OAuthGetRequestToken")
	@Produces(MediaType.APPLICATION_XML)
	public XMLResponse getRequestToken()
	{
		AuthCredentials authCredentials = RandomGenerator.getInstance().nextOAuthCredentials();
		OAuthRequestTokenResponse response = new OAuthRequestTokenResponse(authCredentials);
		Engine engine = Environment.getInstance().getEngine();
		ExpirableMap<String, Object> expirableMap = engine.getExpirableMap();
		expirableMap.put("request_token:" + authCredentials.getKey(),
		                 authCredentials);
		OAuthServerRequest request = new OAuthServerRequest(httpContext.getRequest());
		OAuthParameters params = new OAuthParameters();
		params.readRequest(request);
		String consumerKey = params.getConsumerKey();
		expirableMap.put("consumer_token:" + authCredentials.getKey(),
		                 consumerKey);
		return response;
	}
	

	public static void main(String[] args)
	    throws Exception
	{
		//		oauthAuthentication();
		apiSearch();
	}
	

	private static void apiSearch()
	    throws Exception
	{
		Client client = Client.create();
		WebResource resource = client.resource("http://localhost:8181/InterWebJ/api/search");
		AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***",
		                                                          "***REMOVED***");
		AuthCredentials userCredentials = new AuthCredentials("***REMOVED***",
		                                                      "***REMOVED***");
		OAuthParameters oauthParams = new OAuthParameters();
		oauthParams.consumerKey(consumerCredentials.getKey());
		oauthParams.token(userCredentials.getKey());
		oauthParams.signatureMethod(HMAC_SHA1.NAME);
		oauthParams.timestamp();
		oauthParams.nonce();
		oauthParams.version();
		OAuthSecrets oauthSecrets = new OAuthSecrets();
		oauthSecrets.consumerSecret(consumerCredentials.getSecret());
		oauthSecrets.tokenSecret(userCredentials.getSecret());
		OAuthClientFilter filter = new OAuthClientFilter(client.getProviders(),
		                                                 oauthParams,
		                                                 oauthSecrets);
		resource.addFilter(filter);
		resource = resource.queryParam("q", "people");
		resource = resource.queryParam("media_types", "image,video,text,audio");
		System.out.println("querying InterWebJ URL: " + resource.toString());
		ClientResponse response = resource.get(ClientResponse.class);
		SearchResponse searchResponse = response.getEntity(SearchResponse.class);
		System.out.println(searchResponse);
	}
	

	private static void oauthAuthentication()
	    throws Exception
	{
		Parameters params = new Parameters();
		Client client = Client.create();
		WebResource resource = client.resource("http://localhost:8181/InterWebJ/api/oauth/OAuthGetRequestToken");
		AuthCredentials authCredentials = new AuthCredentials("***REMOVED***",
		                                                      "***REMOVED***");
		OAuthParameters oauthParams = new OAuthParameters();
		oauthParams.consumerKey(authCredentials.getKey());
		oauthParams.signatureMethod(HMAC_SHA1.NAME);
		oauthParams.timestamp();
		oauthParams.nonce();
		oauthParams.version();
		OAuthSecrets oauthSecrets = new OAuthSecrets();
		oauthSecrets.consumerSecret(authCredentials.getSecret());
		OAuthClientFilter filter = new OAuthClientFilter(client.getProviders(),
		                                                 oauthParams,
		                                                 oauthSecrets);
		resource.addFilter(filter);
		System.out.println("querying InterWebJ request token: "
		                   + resource.toString());
		ClientResponse response = resource.get(ClientResponse.class);
		OAuthRequestTokenResponse requestTokenResponse = response.getEntity(OAuthRequestTokenResponse.class);
		System.out.println(requestTokenResponse);
		String tokenKey = requestTokenResponse.getRequestToken().getOauthToken();
		String tokenSecret = requestTokenResponse.getRequestToken().getOauthTokenSecret();
		AuthCredentials requestTokenAuthCredentials = new AuthCredentials(tokenKey,
		                                                                  tokenSecret);
		URI authorizationUri = URI.create("http://localhost:8181/InterWebJ/api/oauth/OAuthAuthorizeToken"
		                                  + "?oauth_token=" + tokenKey
		//		                                  + "&oauth_callback=http://localhost:8181/InterWebJ/view/search.xhtml"
		);
		System.out.println("authorize token url: "
		                   + authorizationUri.toASCIIString());
		Desktop.getDesktop().browse(authorizationUri);
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter access token:");
		String accessToken = in.readLine();
		System.out.println("Enter access token secret:");
		String accessTokenSecret = in.readLine();
		AuthCredentials accessTokenAuthCredentials = new AuthCredentials(accessToken,
		                                                                 accessTokenSecret);
		System.out.println(accessTokenAuthCredentials);
		resource = client.resource("http://localhost:8181/InterWebJ/api/oauth/OAuthGetAccessToken");
		oauthParams = new OAuthParameters();
		oauthParams.token(accessToken);
		oauthParams.consumerKey(authCredentials.getKey());
		oauthParams.signatureMethod(HMAC_SHA1.NAME);
		oauthParams.timestamp();
		oauthParams.nonce();
		oauthParams.version();
		oauthSecrets = new OAuthSecrets();
		oauthSecrets.consumerSecret(authCredentials.getSecret());
		oauthSecrets.tokenSecret(accessTokenSecret);
		filter = new OAuthClientFilter(client.getProviders(),
		                               oauthParams,
		                               oauthSecrets);
		resource.addFilter(filter);
		System.out.println("querying InterWebJ access token: "
		                   + resource.toString());
		response = resource.get(ClientResponse.class);
		OAuthAccessTokenResponse accessTokenResponse = response.getEntity(OAuthAccessTokenResponse.class);
		System.out.println(accessTokenResponse);
		accessToken = accessTokenResponse.getAccessToken().getOauthToken();
		accessTokenSecret = accessTokenResponse.getAccessToken().getOauthTokenSecret();
		System.out.println("accessToken: [" + accessToken + "]");
		System.out.println("accessTokenSecret: [" + accessTokenSecret + "]");
	}
}
