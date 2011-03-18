package de.l3s.interwebj.connector;


import java.io.*;
import java.net.*;
import java.util.*;

import javax.ws.rs.core.*;

import com.google.gdata.client.authn.oauth.*;
import com.google.gdata.client.authn.oauth.OAuthParameters;
import com.google.gdata.client.youtube.*;
import com.google.gdata.data.*;
import com.google.gdata.data.media.*;
import com.google.gdata.data.media.mediarss.*;
import com.google.gdata.data.youtube.*;
import com.google.gdata.util.*;
import com.sun.jersey.api.client.*;
import com.sun.jersey.oauth.client.*;
import com.sun.jersey.oauth.signature.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.query.*;


public class YouTubeConnector
    extends ServiceConnector
{
	
	private class ByteArrayMediaSource
	    implements MediaSource
	{
		
		private String name;
		private byte[] data;
		private String contentType;
		

		public ByteArrayMediaSource(String name, byte[] data)
		{
			this(name, data, "video/mp4");
		}
		

		public ByteArrayMediaSource(String name, byte[] data, String contentType)
		{
			this.name = name;
			this.data = data;
			this.contentType = contentType;
		}
		

		@Override
		public long getContentLength()
		{
			return data.length;
		}
		

		@Override
		public String getContentType()
		{
			return contentType;
		}
		

		@Override
		public String getEtag()
		{
			return null;
		}
		

		@Override
		public InputStream getInputStream()
		    throws IOException
		{
			return new ByteArrayInputStream(data);
		}
		

		@Override
		public DateTime getLastModified()
		{
			return null;
		}
		

		@Override
		public String getName()
		{
			return name;
		}
		

		@Override
		public OutputStream getOutputStream()
		    throws IOException
		{
			return null;
		}
	}
	

	private final static String REQUEST_TOKEN_PATH = "https://www.google.com/accounts/OAuthGetRequestToken";
	private final static String AUTHORIZATION_PATH = "https://www.google.com/accounts/OAuthAuthorizeToken";
	private final static String ACCESS_TOKEN_PATH = "https://www.google.com/accounts/OAuthGetAccessToken";
	private final static String CLIENT_ID = "InterWebJ";
	
	private final static String DEVELOPER_KEY = "***REMOVED***";
	

	public YouTubeConnector(AuthCredentials consumerAuthCredentials)
	    throws InterWebException
	{
		super("youtube", "http://www.youtube.com");
		setConsumerAuthCredentials(consumerAuthCredentials);
		init();
	}
	

	@Override
	public Parameters authenticate(PermissionLevel permissionLevel,
	                               String callbackUrl)
	    throws InterWebException
	{
		if (!isRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		Parameters params = new Parameters();
		Client client = Client.create();
		WebResource resource = client.resource(REQUEST_TOKEN_PATH);
		AuthCredentials consumerAuthCredentials = getConsumerAuthCredentials();
		com.sun.jersey.oauth.signature.OAuthParameters oauthParams = new com.sun.jersey.oauth.signature.OAuthParameters();
		oauthParams.consumerKey(consumerAuthCredentials.getKey());
		oauthParams.signatureMethod(HMAC_SHA1.NAME);
		oauthParams.timestamp();
		oauthParams.nonce();
		oauthParams.callback(callbackUrl);
		oauthParams.version();
		OAuthSecrets oauthSecrets = new OAuthSecrets();
		oauthSecrets.consumerSecret(consumerAuthCredentials.getSecret());
		OAuthClientFilter filter = new OAuthClientFilter(client.getProviders(),
		                                                 oauthParams,
		                                                 oauthSecrets);
		resource.addFilter(filter);
		Environment.logger.debug("getting youtube request token: "
		                         + resource.toString());
		try
		{
			ClientResponse response = resource.queryParam("scope",
			                                              "http://gdata.youtube.com").get(ClientResponse.class);
			printClientResponse(response);
			String content = getClientResponseContent(response);
			Environment.logger.debug("Content: " + content);
			params.addQueryParameters(content);
			String authUrl = AUTHORIZATION_PATH + "?oauth_token="
			                 + params.get(Parameters.OAUTH_TOKEN);
			Environment.logger.debug("requesting url: " + authUrl);
			params.add(Parameters.OAUTH_AUTHORIZATION_URL, authUrl);
		}
		catch (UniformInterfaceException e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
		return params;
	}
	

	@Override
	public AuthCredentials completeAuthentication(Parameters params)
	    throws InterWebException
	{
		if (params == null)
		{
			throw new NullPointerException("Argument [params] can not be null");
		}
		AuthCredentials authCredentials = null;
		if (!isRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		try
		{
			String oauthToken = params.get(Parameters.OAUTH_TOKEN);
			Environment.logger.debug("oauth_token: " + oauthToken);
			String oauthTokenSecret = params.get(Parameters.OAUTH_TOKEN_SECRET);
			Environment.logger.debug("oauth_token_secret: " + oauthTokenSecret);
			String oauthVerifier = params.get(Parameters.OAUTH_VERIFIER);
			Environment.logger.debug("oauth_verifier: " + oauthVerifier);
			Client client = Client.create();
			WebResource resource = client.resource(ACCESS_TOKEN_PATH);
			AuthCredentials consumerAuthCredentials = getConsumerAuthCredentials();
			com.sun.jersey.oauth.signature.OAuthParameters oauthParams = new com.sun.jersey.oauth.signature.OAuthParameters();
			oauthParams.version();
			oauthParams.nonce();
			oauthParams.timestamp();
			oauthParams.consumerKey(consumerAuthCredentials.getKey());
			if (oauthVerifier != null)
			{
				oauthParams.verifier(oauthVerifier);
			}
			oauthParams.token(oauthToken);
			oauthParams.signatureMethod(HMAC_SHA1.NAME);
			OAuthSecrets oauthSecrets = new OAuthSecrets();
			oauthSecrets.consumerSecret(consumerAuthCredentials.getSecret());
			oauthSecrets.tokenSecret(oauthTokenSecret);
			OAuthClientFilter filter = new OAuthClientFilter(client.getProviders(),
			                                                 oauthParams,
			                                                 oauthSecrets);
			resource.addFilter(filter);
			Environment.logger.debug("getting youtube access token: "
			                         + resource.toString());
			ClientResponse response = resource.get(ClientResponse.class);
			String content = getClientResponseContent(response);
			Environment.logger.debug("youtube response: " + content);
			params.addQueryParameters(content);
			String key = params.get(Parameters.OAUTH_TOKEN);
			String secret = params.get(Parameters.OAUTH_TOKEN_SECRET);
			authCredentials = new AuthCredentials(key, secret);
		}
		catch (UniformInterfaceException e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return authCredentials;
	}
	

	@Override
	public QueryResult get(Query query, AuthCredentials authCredentials)
	    throws InterWebException
	{
		QueryResult queryResult = new QueryResult(query);
		YouTubeService service = new YouTubeService(CLIENT_ID);
		AuthCredentials consumerAuthCredentials = getConsumerAuthCredentials();
		OAuthParameters oauthParams = getOAuthParameters(consumerAuthCredentials,
		                                                 authCredentials);
		try
		{
			service.setOAuthCredentials(oauthParams, new OAuthHmacSha1Signer());
			YouTubeQuery ytq = new YouTubeQuery(new URL("http://gdata.youtube.com/feeds/api/videos"));
			// order results by the number of views (most viewed first)
			ytq.setOrderBy(YouTubeQuery.OrderBy.VIEW_COUNT);
			
			// do not exclude restricted content from the search results 
			// (by default, it is excluded) 
			ytq.setSafeSearch(YouTubeQuery.SafeSearch.NONE);
			
			ytq.setFullTextQuery(query.getQuery());
			
			VideoFeed videoFeed = service.query(ytq, VideoFeed.class);
			for (VideoEntry ve : videoFeed.getEntries())
			{
				System.out.println(ve.getTitle().getPlainText());
			}
		}
		catch (OAuthException e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
		catch (ServiceException e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
		return queryResult;
	}
	

	private String getClientResponseContent(ClientResponse response)
	    throws IOException
	{
		int size = response.getLength();
		byte buff[] = new byte[size];
		InputStream is = response.getEntityInputStream();
		is.read(buff);
		return new String(buff);
	}
	

	private OAuthParameters getOAuthParameters(AuthCredentials consumerAuthCredentials,
	                                           AuthCredentials userAuthCredentials)
	{
		com.sun.jersey.oauth.signature.OAuthParameters jerseyOAuthParams = new com.sun.jersey.oauth.signature.OAuthParameters().nonce().timestamp();
		OAuthParameters googleOAuthParams = new OAuthParameters();
		googleOAuthParams.setOAuthNonce(jerseyOAuthParams.getNonce());
		googleOAuthParams.setOAuthTimestamp(jerseyOAuthParams.getTimestamp());
		googleOAuthParams.setOAuthConsumerKey(consumerAuthCredentials.getKey());
		googleOAuthParams.setOAuthConsumerSecret(consumerAuthCredentials.getSecret());
		googleOAuthParams.setOAuthToken(userAuthCredentials.getKey());
		googleOAuthParams.setOAuthTokenSecret(userAuthCredentials.getSecret());
		googleOAuthParams.setOAuthSignatureMethod(HMAC_SHA1.NAME);
		return googleOAuthParams;
	}
	

	@Override
	protected void init()
	{
		// TODO: Stub. Read from configuration file
		TreeSet<String> contentTypes = new TreeSet<String>();
		contentTypes.add("video");
		setContentTypes(contentTypes);
	}
	

	private void printClientResponse(ClientResponse response)
	{
		Environment.logger.debug("Status: " + response.getStatus());
		Environment.logger.debug("Headers: ");
		MultivaluedMap<String, String> headers = response.getHeaders();
		for (String header : headers.keySet())
		{
			Environment.logger.debug(header + ": " + headers.get(header));
		}
	}
	

	@Override
	public void put(byte[] data,
	                Parameters params,
	                AuthCredentials authCredentials)
	    throws InterWebException
	{
		VideoEntry ve = new VideoEntry();
		YouTubeMediaGroup mg = ve.getOrCreateMediaGroup();
		String category = params.get("category", "Film");
		String title = params.get("title", "No title");
		String keywords = params.get("keywords", "empty");
		String description = params.get("description", "No description");
		String name = params.get("name", "No name");
		mg.addCategory(new MediaCategory(YouTubeNamespace.CATEGORY_SCHEME,
		                                 category));
		mg.setTitle(new MediaTitle());
		mg.getTitle().setPlainTextContent(title);
		mg.setKeywords(new MediaKeywords());
		mg.getKeywords().addKeyword(keywords);
		mg.setDescription(new MediaDescription());
		mg.getDescription().setPlainTextContent(description);
		MediaSource ms = new ByteArrayMediaSource(name, data);
		ve.setMediaSource(ms);
		try
		{
			YouTubeService service = new YouTubeService(CLIENT_ID,
			                                            DEVELOPER_KEY);
			AuthCredentials consumerAuthCredentials = getConsumerAuthCredentials();
			OAuthParameters oauthParams = getOAuthParameters(consumerAuthCredentials,
			                                                 authCredentials);
			service.setOAuthCredentials(oauthParams, new OAuthHmacSha1Signer());
			service.insert(new URL("http://uploads.gdata.youtube.com/feeds/api/users/default/uploads"),
			               ve);
		}
		catch (ServiceException e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
		catch (OAuthException e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
	}
	

	@Override
	public boolean requestRegistrationData()
	{
		return true;
	}
	

	public static void main(String[] args)
	    throws Exception
	{
		AuthCredentials consumerAuthCredentials = new AuthCredentials("***REMOVED***",
		                                                              "***REMOVED***");
		AuthCredentials authCredentials = new AuthCredentials("***REMOVED***",
		                                                      "lkMLQqGkmcufaA31tPHFHwJm");
		List<String> contentTypes = new ArrayList<String>();
		contentTypes.add("video");
		Query query = new Query("Japan", contentTypes);
		YouTubeConnector connector = new YouTubeConnector(consumerAuthCredentials);
		Parameters params = new Parameters();
		params.add("title", "Япония");
		params.add("description", "Япония");
		BufferedInputStream is = new BufferedInputStream(new FileInputStream("/home/olex/downloads/Japan.flv"));
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		int c;
		while ((c = is.read()) != -1)
		{
			os.write(c);
		}
		is.close();
		os.close();
		byte[] data = os.toByteArray();
		connector.put(data, params, authCredentials);
		//		connector.get(query, authCredentials);
		System.out.println("done");
	}
}
