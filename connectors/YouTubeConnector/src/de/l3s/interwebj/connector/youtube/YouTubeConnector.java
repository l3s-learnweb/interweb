package de.l3s.interwebj.connector.youtube;


import static de.l3s.interwebj.util.Assertions.notNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;

import com.google.gdata.client.Query.CustomParameter;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.google.gdata.client.authn.oauth.OAuthParameters;
import com.google.gdata.client.youtube.YouTubeQuery;
import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.data.media.MediaByteArraySource;
import com.google.gdata.data.media.MediaSource;
import com.google.gdata.data.media.mediarss.MediaCategory;
import com.google.gdata.data.media.mediarss.MediaDescription;
import com.google.gdata.data.media.mediarss.MediaGroup;
import com.google.gdata.data.media.mediarss.MediaKeywords;
import com.google.gdata.data.media.mediarss.MediaThumbnail;
import com.google.gdata.data.media.mediarss.MediaTitle;
import com.google.gdata.data.youtube.UserProfileEntry;
import com.google.gdata.data.youtube.VideoEntry;
import com.google.gdata.data.youtube.VideoFeed;
import com.google.gdata.data.youtube.YouTubeMediaGroup;
import com.google.gdata.data.youtube.YouTubeNamespace;
import com.google.gdata.util.ServiceException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.oauth.client.OAuthClientFilter;
import com.sun.jersey.oauth.signature.HMAC_SHA1;
import com.sun.jersey.oauth.signature.OAuthSecrets;

import de.l3s.interwebj.AuthCredentials;
import de.l3s.interwebj.InterWebException;
import de.l3s.interwebj.Parameters;
import de.l3s.interwebj.config.Configuration;
import de.l3s.interwebj.core.AbstractServiceConnector;
import de.l3s.interwebj.core.Environment;
import de.l3s.interwebj.core.ServiceConnector;
import de.l3s.interwebj.query.Query;
import de.l3s.interwebj.query.Query.SearchScope;
import de.l3s.interwebj.query.QueryResult;
import de.l3s.interwebj.query.ResultItem;
import de.l3s.interwebj.query.Thumbnail;
import de.l3s.interwebj.util.CoreUtils;


public class YouTubeConnector
    extends AbstractServiceConnector
{
	
	private static final String REQUEST_TOKEN_PATH = "https://www.google.com/accounts/OAuthGetRequestToken";
	private static final String AUTHORIZATION_PATH = "https://www.google.com/accounts/OAuthAuthorizeToken";
	private static final String ACCESS_TOKEN_PATH = "https://www.google.com/accounts/OAuthGetAccessToken";
	private static final String GET_VIDEO_FEED_PATH = "http://gdata.youtube.com/feeds/api/videos";
	private static final String UPLOAD_VIDEO_PATH = "http://uploads.gdata.youtube.com/feeds/api/users/default/uploads";
	private static final String USER_PROFILE_PATH = "http://gdata.youtube.com/feeds/api/users/default";
	private static final String USER_FEED_PREFIX = "http://gdata.youtube.com/feeds/api/users/";	
	private static final String STANDARD_FEED_PREFIX = "http://gdata.youtube.com/feeds/api/standardfeeds/";
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
	public Parameters authenticate(String callbackUrl)
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
		Environment.logger.info("querying youtube request token: "
		                        + resource.toString());
		try
		{
			resource = resource.queryParam("scope", "http://gdata.youtube.com");
			ClientResponse response = resource.get(ClientResponse.class);
			String responseContent = CoreUtils.getClientResponseContent(response);
			Environment.logger.info("Content: " + responseContent);
			params.addQueryParameters(responseContent);
			String authUrl = AUTHORIZATION_PATH + "?oauth_token="
			                 + params.get(Parameters.OAUTH_TOKEN);
			Environment.logger.info("requesting url: " + authUrl);
			params.add(Parameters.AUTHORIZATION_URL, authUrl);
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
		notNull(params, "params");
		AuthCredentials authCredentials = null;
		if (!isRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		try
		{
			String oauthToken = params.get(Parameters.OAUTH_TOKEN);
			Environment.logger.info("oauth_token: " + oauthToken);
			String oauthTokenSecret = params.get(Parameters.OAUTH_TOKEN_SECRET);
			Environment.logger.info("oauth_token_secret: " + oauthTokenSecret);
			String oauthVerifier = params.get(Parameters.OAUTH_VERIFIER);
			Environment.logger.info("oauth_verifier: " + oauthVerifier);
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
			Environment.logger.info("getting youtube access token: "
			                        + resource.toString());
			ClientResponse response = resource.get(ClientResponse.class);
			String content = CoreUtils.getClientResponseContent(response);
			Environment.logger.info("youtube response: " + content);
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
		notNull(query, "query");
		if (!isRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		QueryResult queryResult = new QueryResult(query);
		
		if (!query.getContentTypes().contains(Query.CT_VIDEO))
			return queryResult;
		
		try
		{				
			YouTubeService service = createYouTubeService(authCredentials);
			YouTubeQuery ytq = new YouTubeQuery(new URL(GET_VIDEO_FEED_PATH));
			ytq.setMaxResults(Math.min(50, query.getResultCount()));
			ytq.setStartIndex(Math.min(50, query.getResultCount()) * (query.getPage()-1)+1);				
			
			switch (query.getSortOrder())
			{
				case RELEVANCE:
					ytq.addCustomParameter(new CustomParameter("orderby", "relevance_lang_"+ query.getLanguage())); break;
				case DATE:
					ytq.setOrderBy(YouTubeQuery.OrderBy.PUBLISHED); break;
				case INTERESTINGNESS:
					ytq.setOrderBy(YouTubeQuery.OrderBy.VIEW_COUNT); break;
				default:
					ytq.setOrderBy(YouTubeQuery.OrderBy.RELEVANCE);
			}
			

			ytq.setSafeSearch(YouTubeQuery.SafeSearch.NONE);
			if (query.getSearchScopes().contains(SearchScope.TEXT))
			{
				ytq.setFullTextQuery(query.getQuery());
			}
			VideoFeed vf = service.query(ytq, VideoFeed.class);
			int rank = ytq.getStartIndex();
			queryResult.setTotalResultCount(vf.getTotalResults());
			for (VideoEntry ve : vf.getEntries())
			{
				ResultItem resultItem = new ResultItem(getName());
				resultItem.setType(Query.CT_VIDEO);
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
				resultItem.setRank(rank++);
				resultItem.setTotalResultCount(queryResult.getTotalResultCount());
				resultItem.setViewCount(getViewCount(ve));
				resultItem.setCommentCount(getCommentCount(ve));
				resultItem.setEmbedded(getEmbedded(authCredentials,
				                                   resultItem.getUrl(),
				                                   ResultItem.DEFAULT_EMBEDDED_WIDTH,
				                                   ResultItem.DEFAULT_EMBEDDED_HEIGHT));
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
		
		return queryResult;
	}
	

	@Override
	public String getEmbedded(AuthCredentials authCredentials,
	                          String url,
	                          int maxWidth,
	                          int maxHeight)
	    throws InterWebException
	{
		/*
		URI uri = URI.create(url);
		URI baseUri = URI.create(getBaseUrl());
		if (!baseUri.getHost().endsWith(uri.getHost()))
		{
			throw new InterWebException("URL: [" + url
			                            + "] doesn't belong to connector ["
			                            + getName() + "]");
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
		}*/
		if(url.toLowerCase().contains("youtube.com"))
		{
			Pattern pattern = Pattern.compile("v[/=]([^&]+)"); 
			Matcher matcher = pattern.matcher(url); 
			
		    if(matcher.find()) 
		    {
		        String id = matcher.group(1);
		        
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
		}
		throw new InterWebException("URL: [" + url
                + "] doesn't belong to connector ["
                + getName() + "]");
	}
	

	@Override
	public String getUserId(AuthCredentials authCredentials)
	    throws InterWebException
	{
		try
		{
			YouTubeService service = createYouTubeService(authCredentials);
			UserProfileEntry profileEntry = service.getEntry(new URL(USER_PROFILE_PATH), UserProfileEntry.class);
			return profileEntry.getUsername();
		}
		catch (OAuthException e)
		{
			e.printStackTrace();
			Environment.logger.severe(e.getMessage());
			throw new InterWebException(e);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			Environment.logger.severe(e.getMessage());
			throw new InterWebException(e);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Environment.logger.severe(e.getMessage());
			throw new InterWebException(e);
		}
		catch (ServiceException e)
		{
			e.printStackTrace();
			Environment.logger.severe(e.getMessage());
			throw new InterWebException(e);
		}
	}
	

	@Override
	public boolean isConnectorRegistrationDataRequired()
	{
		return true;
	}
	

	@Override
	public boolean isUserRegistrationDataRequired()
	{
		return false;
	}
	

	@Override
	public boolean isUserRegistrationRequired()
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
		notNull(data, "data");
		notNull(contentType, "contentType");
		notNull(params, "params");
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

	private static int getViewCount(VideoEntry ve)
	{
		if (ve.getStatistics() == null)
		{
			return -1;
		}
		return (int) ve.getStatistics().getViewCount();
	}


	@Override
	public Set<String> getTags(String username, int maxCount) throws IllegalArgumentException, IOException
	{
		if(maxCount < 1)
			return null;
		
		HashSet<String> tags = new HashSet<String>();
		YouTubeService service;
		try {
			service = createYouTubeService(null);
		}
		catch (OAuthException e) { // should never happen
			throw new RuntimeException(e);
		}
	    
	    UserProfileEntry userProfileEntry = null;
	    VideoFeed videoFeed;
		try { 
			userProfileEntry = service.getEntry(new URL(USER_FEED_PREFIX + username), UserProfileEntry.class);
			videoFeed = service.getFeed(new URL(userProfileEntry.getUploadsFeedLink().getHref()), VideoFeed.class);
		}
		catch (MalformedURLException e) { // should never happen
			throw new RuntimeException(e);
		}
		catch (ServiceException e) {
			if(e.getMessage().equals("User not found"))
				throw new IllegalArgumentException("User not found");
			throw new RuntimeException(e);
		}

		for (VideoEntry videoEntry : videoFeed.getEntries()) 
		{
			YouTubeMediaGroup mediaGroup = videoEntry.getMediaGroup();
			MediaKeywords keywords = mediaGroup.getKeywords();

			if (null != keywords) {
				for (String keyword : keywords.getKeywords()) 
				{
					tags.add(keyword);
					
					if(tags.size() == maxCount)
						return tags;
				}
			}
		}
		
		// not enough tags found, get more:
		try {
			videoFeed = service.getFeed(new URL(STANDARD_FEED_PREFIX + "top_rated"), VideoFeed.class);
		}
		catch (ServiceException e) {
			throw new RuntimeException(e);
		}
		
		for (VideoEntry videoEntry : videoFeed.getEntries()) 
		{
			YouTubeMediaGroup mediaGroup = videoEntry.getMediaGroup();
			MediaKeywords keywords = mediaGroup.getKeywords();

			if (null != keywords) {
				for (String keyword : keywords.getKeywords()) 
				{
					tags.add(keyword);
					
					if(tags.size() == maxCount)
						return tags;
				}
			}
		}
		
		return tags;
	}

	@Override
	public Set<String> getUsers(Set<String> tags, int maxCount) throws IOException, InterWebException 
	{
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}
	
}
