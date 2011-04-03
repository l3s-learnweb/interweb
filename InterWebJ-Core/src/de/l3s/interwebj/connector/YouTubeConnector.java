package de.l3s.interwebj.connector;


import java.io.*;
import java.net.*;
import java.util.*;

import javax.ws.rs.core.*;

import com.google.gdata.client.authn.oauth.*;
import com.google.gdata.client.authn.oauth.OAuthParameters;
import com.google.gdata.client.youtube.*;
import com.google.gdata.client.youtube.YouTubeQuery.OrderBy;
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
import de.l3s.interwebj.query.Query.SearchScope;
import de.l3s.interwebj.query.Query.SortOrder;
import de.l3s.interwebj.util.*;


public class YouTubeConnector
    extends ServiceConnector
{
	
	private static final String REQUEST_TOKEN_PATH = "https://www.google.com/accounts/OAuthGetRequestToken";
	private static final String AUTHORIZATION_PATH = "https://www.google.com/accounts/OAuthAuthorizeToken";
	private static final String ACCESS_TOKEN_PATH = "https://www.google.com/accounts/OAuthGetAccessToken";
	private static final String CLIENT_ID = "InterWebJ";
	private static final String DEVELOPER_KEY = "***REMOVED***";
	

	public YouTubeConnector(AuthCredentials consumerAuthCredentials)
	{
		super("youtube", "http://www.youtube.com");
		setConsumerAuthCredentials(consumerAuthCredentials);
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
		Environment.logger.debug("querying youtube request token: "
		                         + resource.toString());
		try
		{
			ClientResponse response = resource.queryParam("scope",
			                                              "http://gdata.youtube.com").get(ClientResponse.class);
			printClientResponse(response);
			String content = CoreUtils.getClientResponseContent(response);
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
	public ServiceConnector clone()
	{
		return new YouTubeConnector(getConsumerAuthCredentials());
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
			String content = CoreUtils.getClientResponseContent(response);
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
			e.printStackTrace();
			throw new InterWebException(e);
		}
		return authCredentials;
	}
	

	@Override
	public QueryResult get(Query query, AuthCredentials authCredentials)
	    throws InterWebException
	{
		if (query == null)
		{
			throw new NullPointerException("Argument [query] can not be null");
		}
		if (!isRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		QueryResult queryResult = new QueryResult(query);
		if (query.getContentTypes().contains(Query.CT_VIDEO))
		{
			YouTubeService service = new YouTubeService(CLIENT_ID);
			AuthCredentials consumerAuthCredentials = getConsumerAuthCredentials();
			OAuthParameters oauthParams = getOAuthParameters(consumerAuthCredentials,
			                                                 authCredentials);
			try
			{
				service.setOAuthCredentials(oauthParams,
				                            new OAuthHmacSha1Signer());
				YouTubeQuery ytq = new YouTubeQuery(new URL("http://gdata.youtube.com/feeds/api/videos"));
				ytq.setMaxResults(Math.min(50, query.getResultCount()));
				ytq.setOrderBy(getSortOrder(query.getSortOrder()));
				ytq.setSafeSearch(YouTubeQuery.SafeSearch.NONE);
				if (query.getSearchScopes().contains(SearchScope.TEXT))
				{
					ytq.setFullTextQuery(query.getQuery());
				}
				VideoFeed vf = service.query(ytq, VideoFeed.class);
				int count = 0;
				Environment.logger.debug("Total " + vf.getTotalResults()
				                         + " result(s) found");
				for (VideoEntry ve : vf.getEntries())
				{
					ResultItem resultItem = new YouTubeVideoResultItem(getName());
					resultItem.setServiceName(getName());
					resultItem.setId(ve.getId());
					resultItem.setType(Query.CT_VIDEO);
					resultItem.setTitle(ve.getTitle().getPlainText());
					MediaGroup mg = ve.getMediaGroup();
					resultItem.setDescription(mg.getDescription().getPlainTextContent());
					resultItem.setUrl(mg.getPlayer().getUrl());
					resultItem.setImageUrl(mg.getThumbnails().get(0).getUrl());
					resultItem.setDate(CoreUtils.formatDate(ve.getPublished().getValue()));
					String tags = CoreUtils.convertToString(mg.getKeywords().getKeywords());
					resultItem.setTags(tags);
					resultItem.setRank(count++);
					resultItem.setTotalResultCount(vf.getTotalResults());
					if (ve.getStatistics() != null)
					{
						resultItem.setViewCount((int) ve.getStatistics().getViewCount());
					}
					resultItem.setCommentCount(getCommentCount(ve));
					queryResult.addResultItem(resultItem);
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
		}
		return queryResult;
	}
	

	private int getCommentCount(VideoEntry ve)
	{
		if (ve.getComments() == null)
		{
			return 0;
		}
		if (ve.getComments().getFeedLink() == null)
		{
			return 0;
		}
		if (ve.getComments().getFeedLink().getCountHint() == null)
		{
			return 0;
		}
		return ve.getComments().getFeedLink().getCountHint().intValue();
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
	

	private OrderBy getSortOrder(SortOrder sortOrder)
	{
		switch (sortOrder)
		{
			case RELEVANCE:
				return YouTubeQuery.OrderBy.RELEVANCE;
			case DATE:
				return YouTubeQuery.OrderBy.PUBLISHED;
			case INTERESTINGNESS:
				return YouTubeQuery.OrderBy.VIEW_COUNT;
		}
		return YouTubeQuery.OrderBy.RELEVANCE;
	}
	

	@Override
	protected void init()
	{
		// TODO: Stub. Read from configuration file
		TreeSet<String> contentTypes = new TreeSet<String>();
		contentTypes.add("video");
		setContentTypes(contentTypes);
	}
	

	@Override
	public boolean isRegistrationRequired()
	{
		return true;
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
	                String contentType,
	                Parameters params,
	                AuthCredentials authCredentials)
	    throws InterWebException
	{
		if (data == null)
		{
			throw new NullPointerException("Argument [data] can not be null");
		}
		if (contentType == null)
		{
			throw new NullPointerException("Argument [contentType] can not be null");
		}
		if (params == null)
		{
			throw new NullPointerException("Argument [params] can not be null");
		}
		if (!isRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		if (authCredentials == null)
		{
			throw new InterWebException("Upload is forbidden for non-authorized users");
		}
		VideoEntry ve = new VideoEntry();
		YouTubeMediaGroup mg = ve.getOrCreateMediaGroup();
		String category = params.get("category", "Film");
		mg.addCategory(new MediaCategory(YouTubeNamespace.CATEGORY_SCHEME,
		                                 category));
		mg.setTitle(new MediaTitle());
		String title = params.get(Parameters.TITLE, "No title");
		mg.getTitle().setPlainTextContent(title);
		mg.setKeywords(new MediaKeywords());
		List<String> keywords = CoreUtils.convertToUniqueList(params.get(Parameters.TAGS,
		                                                                 ""));
		mg.getKeywords().addKeywords(keywords);
		mg.setDescription(new MediaDescription());
		String description = params.get(Parameters.DESCRIPTION,
		                                "No description");
		mg.getDescription().setPlainTextContent(description);
		int privacy = Integer.parseInt(params.get(Parameters.PRIVACY, "0"));
		mg.setPrivate(privacy > 0);
		MediaSource ms = new MediaByteArraySource(data, "video/mp4");
		ve.setMediaSource(ms);
		try
		{
			YouTubeService service = new YouTubeService(CLIENT_ID,
			                                            DEVELOPER_KEY);
			AuthCredentials consumerAuthCredentials = getConsumerAuthCredentials();
			OAuthParameters oauthParams = getOAuthParameters(consumerAuthCredentials,
			                                                 authCredentials);
			service.setOAuthCredentials(oauthParams, new OAuthHmacSha1Signer());
			service.getRequestFactory().setHeader("Slug", "no_name.mp4");
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
}
