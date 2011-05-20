package de.l3s.interwebj.rest;


import static de.l3s.interwebj.webutil.RestUtils.*;

import java.awt.*;
import java.io.*;
import java.net.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.sun.jersey.api.client.*;
import com.sun.jersey.api.core.*;
import com.sun.jersey.core.util.*;
import com.sun.jersey.oauth.server.*;
import com.sun.jersey.oauth.signature.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.db.*;
import de.l3s.interwebj.jaxb.*;
import de.l3s.interwebj.jaxb.oauth.*;
import de.l3s.interwebj.util.*;


@Path("/oauth")
public class OAuth
    extends Endpoint
{
	
	@GET
	@Path("/OAuthAuthorizeToken")
	@Produces(MediaType.APPLICATION_XML)
	public XMLResponse authorizeToken(@QueryParam("oauth_token") String requestToken,
	                                  @QueryParam("oauth_callback") String callbackUrl)
	{
		HttpContext httpContext = getHttpContext();
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
		HttpContext httpContext = getHttpContext();
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
		HttpContext httpContext = getHttpContext();
		OAuthServerRequest request = new OAuthServerRequest(httpContext.getRequest());
		OAuthParameters params = new OAuthParameters();
		params.readRequest(request);
		String consumerKey = params.getConsumerKey();
		expirableMap.put("consumer_token:" + authCredentials.getKey(),
		                 consumerKey);
		return response;
	}
	

	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_XML)
	public XMLResponse registerUser(@FormParam("username") String userName,
	                                @FormParam("password") String password,
	                                @FormParam("default_token") String defaultToken,
	                                @FormParam("default_secret") String defaultTokenSecret)
	{
		HttpContext httpContext = getHttpContext();
		OAuthServerRequest request = new OAuthServerRequest(httpContext.getRequest());
		OAuthParameters params = new OAuthParameters();
		params.readRequest(request);
		Database database = Environment.getInstance().getDatabase();
		InterWebPrincipal principal = new InterWebPrincipal(userName);
		if (database.hasPrincipal(userName))
		{
			throwWebApplicationException(ErrorResponse.USER_EXISTS);
		}
		AuthCredentials accessToken = RandomGenerator.getInstance().nextOAuthCredentials();
		principal.setOauthCredentials(accessToken);
		database.savePrincipal(principal, password);
		OAuthAccessTokenResponse response = new OAuthAccessTokenResponse(accessToken);
		return response;
	}
	

	public static void main(String[] args)
	    throws Exception
	{
		//		testOauthAuthentication();
		//		testSearch();
		testAddUser();
	}
	

	private static void testAddUser()
	    throws Exception
	{
		AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***",
		                                                          "***REMOVED***");
		WebResource resource = createWebResource("http://localhost:8181/InterWebJ/api/oauth/register",
		                                         consumerCredentials,
		                                         null);
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		params.add("username", "user2");
		params.add("password", "123456");
		System.out.println("querying InterWebJ URL: " + resource.toString());
		ClientResponse response = resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class,
		                                                                                    params);
		OAuthAccessTokenResponse accessTokenResponse = response.getEntity(OAuthAccessTokenResponse.class);
		System.out.println(accessTokenResponse);
	}
	

	private static void testOauthAuthentication()
	    throws Exception
	{
		AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***",
		                                                          "***REMOVED***");
		WebResource resource = createWebResource("http://localhost:8181/InterWebJ/api/oauth/OAuthGetRequestToken",
		                                         consumerCredentials,
		                                         null);
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
		
		resource = createWebResource("http://localhost:8181/InterWebJ/api/oauth/OAuthGetAccessToken",
		                             consumerCredentials,
		                             accessTokenAuthCredentials);
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
	

	private static void testSearch()
	    throws Exception
	{
		AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***",
		                                                          "***REMOVED***");
		AuthCredentials userCredentials = new AuthCredentials("***REMOVED***",
		                                                      "***REMOVED***");
		WebResource resource = createWebResource("http://localhost:8181/InterWebJ/api/search",
		                                         consumerCredentials,
		                                         userCredentials);
		resource = resource.queryParam("q", "people");
		resource = resource.queryParam("media_types", "image,video,text,audio");
		System.out.println("querying InterWebJ URL: " + resource.toString());
		ClientResponse response = resource.get(ClientResponse.class);
		SearchResponse searchResponse = response.getEntity(SearchResponse.class);
		System.out.println(searchResponse);
	}
}
