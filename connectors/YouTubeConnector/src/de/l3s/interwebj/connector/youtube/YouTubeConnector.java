package de.l3s.interwebj.connector.youtube;


import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.commons.lang.*;

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
import de.l3s.interwebj.config.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.query.*;
import de.l3s.interwebj.query.Query.SearchScope;
import de.l3s.interwebj.query.Query.SortOrder;
import de.l3s.interwebj.util.*;


public class YouTubeConnector
    extends AbstractServiceConnector
{
	
	private static final String REQUEST_TOKEN_PATH = "https://www.google.com/accounts/OAuthGetRequestToken";
	private static final String AUTHORIZATION_PATH = "https://www.google.com/accounts/OAuthAuthorizeToken";
	private static final String ACCESS_TOKEN_PATH = "https://www.google.com/accounts/OAuthGetAccessToken";
	private static final String GET_VIDEO_FEED_PATH = "http://gdata.youtube.com/feeds/api/videos";
	private static final String UPLOAD_VIDEO_PATH = "http://uploads.gdata.youtube.com/feeds/api/users/default/uploads";
	private static final String USER_PROFILE_PATH = "http://gdata.youtube.com/feeds/api/users/default";
	private static final String CLIENT_ID = "InterWebJ";
	private static final String DEVELOPER_KEY = "***REMOVED***";
	

	public YouTubeConnector(Configuration configuration)
	{
		this(configuration, null);
	}
	

	public YouTubeConnector(Configuration configuration,
	                        AuthCredentials consumerAuthCredentials)
	{
		super(configuration);
		setAuthCredentials(consumerAuthCredentials);
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
		AuthCredentials authCredentials = getAuthCredentials();
		com.sun.jersey.oauth.signature.OAuthParameters oauthParams = new com.sun.jersey.oauth.signature.OAuthParameters();
		oauthParams.consumerKey(authCredentials.getKey());
		oauthParams.signatureMethod(HMAC_SHA1.NAME);
		oauthParams.timestamp();
		oauthParams.nonce();
		oauthParams.callback(callbackUrl);
		oauthParams.version();
		OAuthSecrets oauthSecrets = new OAuthSecrets();
		oauthSecrets.consumerSecret(authCredentials.getSecret());
		OAuthClientFilter filter = new OAuthClientFilter(client.getProviders(),
		                                                 oauthParams,
		                                                 oauthSecrets);
		resource.addFilter(filter);
		Environment.logger.debug("querying youtube request token: "
		                         + resource.toString());
		try
		{
			resource = resource.queryParam("scope", "http://gdata.youtube.com");
			ClientResponse response = resource.get(ClientResponse.class);
			String responseContent = CoreUtils.getClientResponseContent(response);
			Environment.logger.debug("Content: " + responseContent);
			params.addQueryParameters(responseContent);
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
		return new YouTubeConnector(getConfiguration(), getAuthCredentials());
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
			AuthCredentials consumerAuthCredentials = getAuthCredentials();
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
			try
			{
				YouTubeService service = createYouTubeService(authCredentials);
				YouTubeQuery ytq = new YouTubeQuery(new URL(GET_VIDEO_FEED_PATH));
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
					resultItem.setId(ve.getId());
					resultItem.setTitle(ve.getTitle().getPlainText());
					MediaGroup mg = ve.getMediaGroup();
					resultItem.setDescription(mg.getDescription().getPlainTextContent());
					resultItem.setUrl(mg.getPlayer().getUrl());
					resultItem.setThumbnails(createThumbnails(mg));
					resultItem.setDate(CoreUtils.formatDate(ve.getPublished().getValue()));
					String tags = StringUtils.join(mg.getKeywords().getKeywords(),
					                               ',');
					resultItem.setTags(tags);
					resultItem.setRank(count++);
					resultItem.setTotalResultCount(vf.getTotalResults());
					resultItem.setViewCount(getViewCount(ve));
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
	

	@Override
	public String getEmbedded(AuthCredentials authCredentials,
	                          String url,
	                          int maxWidth,
	                          int maxHeight)
	    throws InterWebException
	{
		URI uri = URI.create(url);
		URI baseUri = URI.create(getBaseUrl());
		if (!baseUri.getHost().endsWith(uri.getHost()))
		{
			throw new InterWebException("URL: [" + url
			                            + "] doesn't belong to connector");
		}
		String[] queryParams = uri.getQuery().split("&");
		String id = null;
		for (String queryParam : queryParams)
		{
			String[] param = queryParam.split("=");
			if (param.length == 2 && param[0].equals("v"))
			{
				id = param[1];
			}
		}
		if (id == null)
		{
			throw new InterWebException("No id found in URL: [" + url + "]");
		}
		String embeddedCode = "<embed pluginspage=\"http://www.adobe.com/go/getflashplayer\" src=\"http://www.youtube.com/v/"
		                      + id
		                      + "\" type=\"application/x-shockwave-flash\" width=\""
		                      + maxWidth
		                      + "\" height=\""
		                      + maxHeight
		                      + "\"></embed>";
		return embeddedCode;
	}
	

	@Override
	public String getUserId(AuthCredentials authCredentials)
	    throws InterWebException
	{
		try
		{
			YouTubeService service = createYouTubeService(authCredentials);
			UserProfileEntry profileEntry = service.getEntry(new URL(USER_PROFILE_PATH),
			                                                 UserProfileEntry.class);
			return profileEntry.getUsername();
		}
		catch (OAuthException e)
		{
			e.printStackTrace();
			Environment.logger.error(e);
			throw new InterWebException(e);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			Environment.logger.error(e);
			throw new InterWebException(e);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Environment.logger.error(e);
			throw new InterWebException(e);
		}
		catch (ServiceException e)
		{
			e.printStackTrace();
			Environment.logger.error(e);
			throw new InterWebException(e);
		}
	}
	

	@Override
	public boolean isRegistrationRequired()
	{
		return true;
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
		String title = params.get(Parameters.TITLE, "No Title");
		mg.getTitle().setPlainTextContent(title);
		mg.setKeywords(new MediaKeywords());
		String tags = params.get(Parameters.TAGS, "");
		List<String> keywords = CoreUtils.convertToUniqueList(tags);
		mg.getKeywords().addKeywords(keywords);
		mg.setDescription(new MediaDescription());
		String description = params.get(Parameters.DESCRIPTION,
		                                "No Description");
		mg.getDescription().setPlainTextContent(description);
		int privacy = Integer.parseInt(params.get(Parameters.PRIVACY, "0"));
		mg.setPrivate(privacy > 0);
		MediaSource ms = new MediaByteArraySource(data, "video/*");
		ve.setMediaSource(ms);
		try
		{
			YouTubeService service = createYouTubeService(authCredentials);
			service.getRequestFactory().setHeader("Slug", "no_name.mp4");
			service.insert(new URL(UPLOAD_VIDEO_PATH), ve);
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
	public void revokeAuthentication()
	    throws InterWebException
	{
		// YouTube doesn't provide api for token revokation
	}
	

	private Set<Thumbnail> createThumbnails(MediaGroup mg)
	{
		Set<Thumbnail> thumbnails = new TreeSet<Thumbnail>();
		List<MediaThumbnail> mediaThumbnails = mg.getThumbnails();
		for (MediaThumbnail mt : mediaThumbnails)
		{
			Thumbnail thumbnail = new Thumbnail(mt.getUrl(),
			                                    mt.getWidth(),
			                                    mt.getHeight());
			thumbnails.add(thumbnail);
		}
		return thumbnails;
	}
	

	private YouTubeService createYouTubeService(AuthCredentials authCredentials)
	    throws OAuthException
	{
		YouTubeService service = new YouTubeService(CLIENT_ID, DEVELOPER_KEY);
		if (authCredentials != null)
		{
			OAuthParameters oauthParams = getOAuthParameters(authCredentials);
			service.setOAuthCredentials(oauthParams, new OAuthHmacSha1Signer());
		}
		return service;
	}
	

	private int getCommentCount(VideoEntry ve)
	{
		if (ve.getComments() == null)
		{
			return -1;
		}
		if (ve.getComments().getFeedLink() == null)
		{
			return -1;
		}
		if (ve.getComments().getFeedLink().getCountHint() == null)
		{
			return -1;
		}
		return ve.getComments().getFeedLink().getCountHint().intValue();
	}
	

	private OAuthParameters getOAuthParameters(AuthCredentials userAuthCredentials)
	{
		com.sun.jersey.oauth.signature.OAuthParameters jerseyOAuthParams = new com.sun.jersey.oauth.signature.OAuthParameters().nonce().timestamp();
		OAuthParameters googleOAuthParams = new OAuthParameters();
		googleOAuthParams.setOAuthNonce(jerseyOAuthParams.getNonce());
		googleOAuthParams.setOAuthTimestamp(jerseyOAuthParams.getTimestamp());
		googleOAuthParams.setOAuthConsumerKey(getAuthCredentials().getKey());
		googleOAuthParams.setOAuthConsumerSecret(getAuthCredentials().getSecret());
		if (userAuthCredentials != null)
		{
			googleOAuthParams.setOAuthToken(userAuthCredentials.getKey());
			googleOAuthParams.setOAuthTokenSecret(userAuthCredentials.getSecret());
		}
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
	

	private int getViewCount(VideoEntry ve)
	{
		if (ve.getStatistics() == null)
		{
			return -1;
		}
		return (int) ve.getStatistics().getViewCount();
	}
	
}
