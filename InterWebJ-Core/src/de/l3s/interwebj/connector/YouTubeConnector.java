package de.l3s.interwebj.connector;


import java.awt.*;
import java.net.*;
import java.util.*;

import javax.ws.rs.core.*;

import com.sun.jersey.api.client.*;
import com.sun.jersey.core.util.*;
import com.sun.jersey.oauth.client.*;
import com.sun.jersey.oauth.signature.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.oauth.*;
import de.l3s.interwebj.query.*;


public class YouTubeConnector
    extends ServiceConnector
{
	
	private final static String REQUEST_TOKEN_PATH = "https://www.google.com/accounts/OAuthGetRequestToken";
	private final static String AUTHORIZATION_PATH = "https://www.google.com/accounts/OAuthAuthorizeToken";
	private final static String ACCESS_TOKEN_PATH = "https://www.google.com/accounts/OAuthGetAccessToken";
	

	public YouTubeConnector(AuthData consumerAuthData)
	    throws InterWebException
	{
		super("youtube", "http://www.youtube.com");
		setConsumerAuthData(consumerAuthData);
		init();
	}
	

	@Override
	public OAuthParams authenticate(PermissionLevel permissionLevel,
	                                String callbackUrl)
	    throws InterWebException
	{
		if (!isRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		OAuthParams oAuthParams = new OAuthParams();
		Client client = Client.create();
		WebResource resource = client.resource(REQUEST_TOKEN_PATH);
		AuthData consumerAuthData = getConsumerAuthData();
		OAuthParameters oauthParams = new OAuthParameters();
		oauthParams = oauthParams.consumerKey(consumerAuthData.getKey());
		oauthParams = oauthParams.signatureMethod(HMAC_SHA1.NAME);
		oauthParams = oauthParams.timestamp();
		oauthParams = oauthParams.nonce();
		OAuthSecrets oauthSecrets = new OAuthSecrets();
		oauthSecrets = oauthSecrets.consumerSecret(consumerAuthData.getSecret());
		OAuthClientFilter filter = new OAuthClientFilter(client.getProviders(),
		                                                 oauthParams,
		                                                 oauthSecrets);
		resource.addFilter(filter);
		Environment.logger.debug("getting youtube request token: "
		                         + resource.toString());
		MultivaluedMap<String, String> postData = new MultivaluedMapImpl();
		postData.add("scope", "http://gdata.youtube.com");
		try
		{
			String response = resource.type("application/x-www-form-urlencoded").post(String.class,
			                                                                          postData);
			Environment.logger.debug("youtube response: " + response);
			Map<String, String> queryMap = queryToMap(response);
			oAuthParams.setOauth_token(queryMap.get("oauth_token"));
			oAuthParams.setOauth_token_secret(queryMap.remove("oauth_token_secret"));
			String query = mapToQuery(queryMap);
			String authUrl = AUTHORIZATION_PATH + "?" + query;
			if (callbackUrl != null)
			{
				authUrl += "&oauth_callback=" + callbackUrl;
			}
			Environment.logger.debug("requesting url: " + authUrl);
			oAuthParams.setRequestUrl(new URL(authUrl));
		}
		catch (UniformInterfaceException e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
		return oAuthParams;
	}
	

	@Override
	public AuthData completeAuthentication(Map<String, String[]> params)
	    throws InterWebException
	{
		if (params == null)
		{
			throw new NullPointerException("Argument [params] can not be null");
		}
		AuthData authData = null;
		if (!isRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		try
		{
			String[] values = params.get("oauth_token");
			if (values == null || values.length == 0)
			{
				return null;
			}
			if (values.length > 1)
			{
				Environment.logger.warn("More than one parameter values for \"oauth_token\" key found");
			}
			String oauth_token = values[0];
			Environment.logger.info("oauth_token: " + oauth_token);
			Client client = Client.create();
			WebResource resource = client.resource(ACCESS_TOKEN_PATH);
			AuthData consumerAuthData = getConsumerAuthData();
			OAuthParameters oauthParams = new OAuthParameters();
			oauthParams = oauthParams.consumerKey(consumerAuthData.getKey());
			oauthParams = oauthParams.token(oauth_token);
			oauthParams = oauthParams.signatureMethod(HMAC_SHA1.NAME);
			oauthParams = oauthParams.timestamp();
			oauthParams = oauthParams.nonce();
			OAuthSecrets oauthSecrets = new OAuthSecrets();
			oauthSecrets = oauthSecrets.consumerSecret(consumerAuthData.getSecret());
			OAuthClientFilter filter = new OAuthClientFilter(client.getProviders(),
			                                                 oauthParams,
			                                                 oauthSecrets);
			resource.addFilter(filter);
			Environment.logger.debug("getting youtube access token: "
			                         + resource.toString());
			String response = resource.type("application/x-www-form-urlencoded").get(String.class);
			Environment.logger.debug("youtube response: " + response);
			Map<String, String> queryMap = queryToMap(response);
			String key = queryMap.get("oauth_token");
			String secret = queryMap.get("oauth_token_secret");
			authData = new AuthData(key, secret);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
		return authData;
	}
	

	@Override
	public QueryResult get(Query query, AuthData authData)
	    throws InterWebException
	{
		QueryResult queryResult = new QueryResult(query);
		return queryResult;
	}
	

	@Override
	protected void init()
	{
		// TODO: Stub. Read from configuration file
		TreeSet<String> contentTypes = new TreeSet<String>();
		contentTypes.add("video");
		setContentTypes(contentTypes);
	}
	

	private String mapToQuery(Map<String, String> queryMap)
	{
		String query = "";
		for (Iterator<String> iterator = queryMap.keySet().iterator(); iterator.hasNext();)
		{
			String key = iterator.next();
			query += key + "=" + queryMap.get(key);
			if (iterator.hasNext())
			{
				query += "&";
			}
		}
		return query;
	}
	

	@Override
	public void put(byte[] data, Map<String, String> params, AuthData authData)
	    throws InterWebException
	{
	}
	

	private Map<String, String> queryToMap(String query)
	{
		String[] params = query.split("&");
		Map<String, String> map = new TreeMap<String, String>();
		for (String param : params)
		{
			String[] paramPair = param.split("=");
			String name = paramPair[0];
			String value = null;
			if (paramPair.length > 1)
			{
				value = paramPair[1];
			}
			map.put(name, value);
		}
		return map;
	}
	

	@Override
	public boolean supportOAuth()
	{
		return true;
	}
	

	public static void main(String[] args)
	    throws Exception
	{
		AuthData consumerAuthData = new AuthData("***REMOVED***",
		                                         "***REMOVED***");
		YouTubeConnector connector = new YouTubeConnector(consumerAuthData);
		OAuthParams oAuthParams = connector.authenticate(null,
		                                                 "http://***REMOVED***:8181/InterWebJ/callback");
		Desktop.getDesktop().browse(oAuthParams.getRequestUrl().toURI());
		//		String oauth_token = oAuthParams.getOauth_token();
		//		Map<String, String[]> params = new HashMap<String, String[]>();
		//		params.put("oauth_token", new String[] {oauth_token});
		//		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		//		br.readLine();
		//		connector.completeAuthentication(oAuthParams, params);
	}
}
