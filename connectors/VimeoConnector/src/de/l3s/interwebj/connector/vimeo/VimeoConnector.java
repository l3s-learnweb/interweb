package de.l3s.interwebj.connector.vimeo;


import static de.l3s.interwebj.util.Assertions.notNull;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.NotImplementedException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.oauth.client.OAuthClientFilter;
import com.sun.jersey.oauth.signature.HMAC_SHA1;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;

import de.l3s.interwebj.AuthCredentials;
import de.l3s.interwebj.InterWebException;
import de.l3s.interwebj.Parameters;
import de.l3s.interwebj.config.Configuration;
import de.l3s.interwebj.connector.slideshare.SearchResponse;
import de.l3s.interwebj.connector.slideshare.SearchResultEntity;
import de.l3s.interwebj.core.AbstractServiceConnector;
import de.l3s.interwebj.core.Environment;
import de.l3s.interwebj.core.ServiceConnector;
import de.l3s.interwebj.query.Query;
import de.l3s.interwebj.query.QueryResult;
import de.l3s.interwebj.query.ResultItem;
import de.l3s.interwebj.query.Thumbnail;
import de.l3s.interwebj.util.CoreUtils;


public class VimeoConnector extends AbstractServiceConnector
{			
	private static final String REQUEST_TOKEN_PATH = "https://vimeo.com/oauth/request_token";
	private static final String AUTHORIZATION_PATH = "https://vimeo.com/oauth/authorize";
	private static final String ACCESS_TOKEN_PATH = "https://vimeo.com/oauth/access_token";
	/*
	private static final String GET_VIDEO_FEED_PATH = "http://gdata.youtube.com/feeds/api/videos";
	private static final String UPLOAD_VIDEO_PATH = "http://uploads.gdata.youtube.com/feeds/api/users/default/uploads";
	private static final String USER_PROFILE_PATH = "http://gdata.youtube.com/feeds/api/users/default";
	private static final String USER_FEED_PREFIX = "http://gdata.youtube.com/feeds/api/users/";	
	private static final String STANDARD_FEED_PREFIX = "http://gdata.youtube.com/feeds/api/standardfeeds/";
	private static final String CLIENT_ID = "InterWebJ";
	private static final String DEVELOPER_KEY = "***REMOVED***";
	*/
	
	public VimeoConnector(Configuration configuration)
	{
		this(configuration, null);
	}	

	public VimeoConnector(Configuration configuration, AuthCredentials consumerAuthCredentials)
	{
		super(configuration);
		setAuthCredentials(consumerAuthCredentials);
	}

	@Override
	public ServiceConnector clone()
	{
		return new VimeoConnector(getConfiguration(), getAuthCredentials());
	}	

	@Override
	public Set<String> getTags(String username, int maxCount) throws IllegalArgumentException, IOException
	{
		throw new NotImplementedException();
	}

	@Override
	public Set<String> getUsers(Set<String> tags, int maxCount) throws IOException, InterWebException 
	{
		throw new NotImplementedException();
	}
	
// alles hier drunter muss noch überarbeitet werden
	
	@Override
	public QueryResult get(Query query, AuthCredentials authCredentials) throws InterWebException
	{
		System.out.println("vimeo get");
		notNull(query, "query");
		if (!isRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		QueryResult queryResult = new QueryResult(query);
		
		if (!query.getContentTypes().contains(Query.CT_VIDEO))
			return queryResult;
		
		 Client client = Client.create();
		WebResource resource = client.resource("http://www.slideshare.net/api/2/search_slideshows");
		resource = resource.queryParam("q", query.getQuery());
		resource = resource.queryParam("lang", query.getLanguage());
		resource = resource.queryParam("page", Integer.toString(query.getPage()));
		resource = resource.queryParam("items_per_page", Integer.toString(query.getResultCount()));
		resource = resource.queryParam("sort", createSortOrder(query.getSortOrder()));
		String searchScope = createSearchScope(query.getSearchScopes());
		if (searchScope != null)
		{
			resource = resource.queryParam("what", searchScope);
		}
		String fileType = createFileType(query.getContentTypes());
		if (fileType == null)
		{
			return queryResult;
		}
		resource = resource.queryParam("file_type", fileType);
		//		resource = resource.queryParam("detailed", "1");
		ClientResponse response = postQuery(resource);

		SearchResponse sr;
		try { // macht oft probleme. womöglich liefert slideshare einen fehler im html format oder jersey spinnt
			sr = response.getEntity(SearchResponse.class);
		}
		catch(Exception e) {
			e.printStackTrace();			
			return queryResult;
		}
		queryResult.setTotalResultCount(sr.getMeta().getTotalResults());
		int count = sr.getMeta().getResultOffset() - 1;
		List<SearchResultEntity> searchResults = sr.getSearchResults();
		if (searchResults == null)
		{
			return queryResult;
		}
		for (SearchResultEntity sre : searchResults)
		{
			ResultItem resultItem = new ResultItem(getName());
			resultItem.setType(createType(sre.getSlideshowType()));
			resultItem.setId(Integer.toString(sre.getId()));
			resultItem.setTitle(sre.getTitle());
			resultItem.setDescription(sre.getDescription());
			resultItem.setUrl(sre.getUrl());			
			resultItem.setDate(CoreUtils.formatDate(parseDate(sre.getUpdated())));
			resultItem.setRank(count++);			
			resultItem.setTotalResultCount(sr.getMeta().getTotalResults());
			
			Set<Thumbnail> thumbnails = new TreeSet<Thumbnail>();
			thumbnails.add(new Thumbnail(sre.getThumbnailSmallURL(), 120, 90));
			thumbnails.add(new Thumbnail(sre.getThumbnailURL(), 170, 128));			
			resultItem.setThumbnails(thumbnails);
			
			resultItem.setEmbeddedSize1(CoreUtils.createImageCode(sre.getThumbnailSmallURL(), 120, 90, 100, 100));
			resultItem.setEmbeddedSize2("<img src=\""+ sre.getThumbnailURL() +"\" width=\"170\" height=\"128\" />");
			resultItem.setImageUrl(sre.getThumbnailURL());
			
			// remove spam from the embedded code
			Pattern pattern = Pattern.compile("(<object.*</object>)"); 
			Matcher matcher = pattern.matcher(sre.getEmbed()); 
			
		    if(matcher.find()) 
		    	resultItem.setEmbeddedSize3(matcher.group(0));				
		    else
		    	resultItem.setEmbeddedSize3(sre.getEmbed());	
			
			queryResult.addResultItem(resultItem);
		}
		return queryResult;
		/*
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
			
			int resultCount = (int) queryResult.getTotalResultCount();
			for (VideoEntry ve : vf.getEntries())
			{
				ResultItem resultItem = createResultItem(ve, rank++, resultCount);
				
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
		}*/
		
		return queryResult;
	}
	
	
	

	@Override
	public Parameters authenticate(String callbackUrl) throws InterWebException
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
	/*
	private ResultItem createResultItem(VideoEntry ve,
            int rank,
            int totalResultCount)
	{
		ResultItem resultItem = new ResultItem(getName());
		resultItem.setType(Query.CT_VIDEO);
		resultItem.setId(ve.getId());
		resultItem.setTitle(ve.getTitle().getPlainText());
		MediaGroup mg = ve.getMediaGroup();
		resultItem.setDescription(mg.getDescription().getPlainTextContent());
		resultItem.setUrl(mg.getPlayer().getUrl());
		resultItem.setDate(CoreUtils.formatDate(ve.getPublished().getValue()));
		resultItem.setTags(StringUtils.join(mg.getKeywords().getKeywords(), ','));
		resultItem.setRank(rank++);
		resultItem.setTotalResultCount(totalResultCount);
		resultItem.setViewCount(getViewCount(ve));
		resultItem.setCommentCount(getCommentCount(ve));

		// load thumbnails
		Set<Thumbnail> thumbnails = new TreeSet<Thumbnail>();
		List<MediaThumbnail> mediaThumbnails = mg.getThumbnails();
		for (MediaThumbnail mt : mediaThumbnails)
		{
			Thumbnail thumbnail = new Thumbnail(mt.getUrl(), mt.getWidth(),  mt.getHeight());
			thumbnails.add(thumbnail);
			
			if(thumbnail.getUrl().contains("/default.jpg"))
				resultItem.setEmbeddedSize1(CoreUtils.createImageCode(thumbnail, 100, 100));
			else if(thumbnail.getUrl().contains("/hqdefault.jpg"))
			{
				resultItem.setEmbeddedSize2(CoreUtils.createImageCode(thumbnail, 240, 240));
				resultItem.setImageUrl(thumbnail.getUrl());
			}
		}
		resultItem.setThumbnails(thumbnails);
		
		Pattern pattern = Pattern.compile("v[/=]([^&]+)"); 
		Matcher matcher = pattern.matcher(mg.getPlayer().getUrl()); 
		
	    if(matcher.find()) 
	    {
	        String id = matcher.group(1);
	        
			//create embedded flash video player
			String embeddedCode = "<embed pluginspage=\"http://www.adobe.com/go/getflashplayer\" src=\"http://www.youtube.com/v/"+
									id +"\" type=\"application/x-shockwave-flash\" width=\"500\" height=\"400\"></embed>";
			resultItem.setEmbeddedSize3(embeddedCode);				
	    }
	    return resultItem;
	}
	*/
	

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
		return "not implemented";
		/*
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
		}*/
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
	public ResultItem put(byte[] data,
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
		throw new NotImplementedException();
		/*
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
		ResultItem resultItem = null;
		try
		{
			YouTubeService service = createYouTubeService(authCredentials);
			service.getRequestFactory().setHeader("Slug", "no_name.mp4");
			ve = service.insert(new URL(UPLOAD_VIDEO_PATH), ve);
			
			resultItem = createResultItem(ve, 0, 0);
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
		
		return resultItem;*/
	}
	

	@Override
	public void revokeAuthentication()
	    throws InterWebException
	{
		// YouTube doesn't provide api for token revokation
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
	
	/*
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
* /	

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
/*
	private static int getViewCount(VideoEntry ve)
	{
		if (ve.getStatistics() == null)
		{
			return -1;
		}
		return (int) ve.getStatistics().getViewCount();
	}
*/

	
}
