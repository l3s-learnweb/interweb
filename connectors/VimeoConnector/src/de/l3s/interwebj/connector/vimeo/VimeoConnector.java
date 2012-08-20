package de.l3s.interwebj.connector.vimeo;


import static de.l3s.interwebj.util.Assertions.notNull;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
import de.l3s.interwebj.connector.vimeo.jaxb.LoginResponse;
import de.l3s.interwebj.connector.vimeo.jaxb.SearchResponse;
import de.l3s.interwebj.connector.vimeo.jaxb.Video;
import de.l3s.interwebj.core.AbstractServiceConnector;
import de.l3s.interwebj.core.Environment;
import de.l3s.interwebj.core.ServiceConnector;
import de.l3s.interwebj.query.Query;
import de.l3s.interwebj.query.Query.SortOrder;
import de.l3s.interwebj.query.QueryResult;
import de.l3s.interwebj.query.ResultItem;
import de.l3s.interwebj.query.Thumbnail;
import de.l3s.interwebj.query.UserSocialNetworkResult;
import de.l3s.interwebj.util.CoreUtils;


public class VimeoConnector extends AbstractServiceConnector
{			
	private static final String REQUEST_TOKEN_PATH = "https://vimeo.com/oauth/request_token";
	private static final String AUTHORIZATION_PATH = "https://vimeo.com/oauth/authorize";
	private static final String ACCESS_TOKEN_PATH = "https://vimeo.com/oauth/access_token";
	private static final String VIMEO_BASE = "http://vimeo.com/api/rest/v2?format=xml&method=";
	
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
	
	private static String createSortOrder(SortOrder sortOrder)
	{
		/*
		 * Method to sort by: relevant, newest, oldest, most_played, most_commented, or most_liked.
		 */
		switch (sortOrder)
		{
			case RELEVANCE:
				return "relevant";
			case DATE:
				return "newest";
			case INTERESTINGNESS:
				return "most_played";
			default:
				return "relevant";
		}
	}
	
	private static Date parseDate(String dateString) throws InterWebException
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		try
		{
			return dateFormat.parse(dateString);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			throw new InterWebException("dateString: [" + dateString + "] " + e.getMessage());
		}
	}
	
	@Override
	public QueryResult get(Query query, AuthCredentials authCredentials) throws InterWebException
	{
		notNull(query, "query");
		if (!isRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		QueryResult queryResult = new QueryResult(query);
		
		if (!query.getContentTypes().contains(Query.CT_VIDEO))
			return queryResult;
		
		WebResource resource = createWebResource(VIMEO_BASE +"vimeo.videos.search", getAuthCredentials(), null);
		
		resource = resource.queryParam("query", query.getQuery());
		resource = resource.queryParam("page", Integer.toString(query.getPage()));
		resource = resource.queryParam("per_page", Integer.toString(query.getResultCount()));
		resource = resource.queryParam("full_response", "1"); // summary_response		
		resource = resource.queryParam("sort", createSortOrder(query.getSortOrder()));

		SearchResponse sr = resource.get(SearchResponse.class);	
		
		if (sr.getVideos() == null)
		{
			return queryResult;
		}
		List<Video> videos = sr.getVideos().getVideo();
		
		long totalResultCount = sr.getVideos().getTotal();
		queryResult.setTotalResultCount(totalResultCount);
		int count = (sr.getVideos().getPage()-1)*sr.getVideos().getPerpage();		
		
		for (Video video : videos)
		{
			ResultItem resultItem = new ResultItem(getName());
			resultItem.setType(Query.CT_VIDEO);
			resultItem.setId(Long.toString(video.getId()));
			resultItem.setTitle(video.getTitle());			
			resultItem.setDescription(video.getDescription());
			resultItem.setUrl("http://vimeo.com/"+ video.getId());			
			resultItem.setDate(CoreUtils.formatDate(parseDate(video.getUploadDate())));
			resultItem.setRank(count++);			
			resultItem.setTotalResultCount(totalResultCount);
			resultItem.setCommentCount(video.getNumberOfComments());
			resultItem.setViewCount(video.getNumberOfPlays());
			
			Set<Thumbnail> thumbnails = new LinkedHashSet<Thumbnail>();
			for(de.l3s.interwebj.connector.vimeo.jaxb.Thumbnail vimeoThumbnail : video.getThumbnails().getThumbnail())
			{
				thumbnails.add(new Thumbnail(vimeoThumbnail.getValue(), vimeoThumbnail.getWidth(), vimeoThumbnail.getHeight()));
				
				resultItem.setImageUrl(vimeoThumbnail.getValue()); // thumbnails are orderd by size. so the last assigned image is the largest
				
				if(vimeoThumbnail.getWidth() <= 100)
					resultItem.setEmbeddedSize1(CoreUtils.createImageCode(vimeoThumbnail.getValue(), vimeoThumbnail.getWidth(), vimeoThumbnail.getHeight(), 100, 100));
				else if(vimeoThumbnail.getWidth() <= 240)
					resultItem.setEmbeddedSize2("<img src=\""+ vimeoThumbnail.getValue() +"\" width=\""+ vimeoThumbnail.getWidth() +"\" height=\""+ vimeoThumbnail.getHeight() +"\" />");
			}			
			resultItem.setThumbnails(thumbnails);
			
			resultItem.setEmbeddedSize3("<iframe src=\"http://player.vimeo.com/video/"+ video.getId() +"\" width=\"500\" height=\"282\" frameborder=\"0\" webkitAllowFullScreen mozallowfullscreen allowFullScreen></iframe>");	
			resultItem.setEmbeddedSize4("<iframe src=\"http://player.vimeo.com/video/"+ video.getId() +"\" width=\"100%\" height=\"100%\" frameborder=\"0\" webkitAllowFullScreen mozallowfullscreen allowFullScreen></iframe>");	
			
			queryResult.addResultItem(resultItem);
		}
		return queryResult;
	}	
	
	public static WebResource createWebResource(String apiUrl, AuthCredentials consumerAuthCredentials, AuthCredentials userAuthCredentials) 
	{
		Client client = Client.create();
		WebResource resource = client.resource(apiUrl);
		OAuthParameters oauthParams = new OAuthParameters();
		oauthParams.consumerKey(consumerAuthCredentials.getKey());
		if (userAuthCredentials != null) {
			oauthParams.token(userAuthCredentials.getKey());
		}
		oauthParams.signatureMethod(HMAC_SHA1.NAME);
		oauthParams.timestamp();
		oauthParams.nonce();
		oauthParams.version();
		OAuthSecrets oauthSecrets = new OAuthSecrets();
		oauthSecrets.consumerSecret(consumerAuthCredentials.getSecret());
		if (userAuthCredentials != null && userAuthCredentials.getSecret() != null) {
			oauthSecrets.tokenSecret(userAuthCredentials.getSecret());
		}
		OAuthClientFilter filter = new OAuthClientFilter(client.getProviders(), oauthParams, oauthSecrets);
		resource.addFilter(filter);

		// System.out.println("requesting filter url: " + filter.toString());
		return resource;
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
		Environment.logger.info("querying vimeo request token: "+ resource.toString());
		try
		{
			//resource = resource.queryParam("scope", "http://gdata.youtube.com");
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
			Environment.logger.info("getting vimeo access token: "+ resource.toString());
			ClientResponse response = resource.get(ClientResponse.class);
			String content = CoreUtils.getClientResponseContent(response);
			Environment.logger.info("vimeo response: " + content);
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
	
	
	// alles hier drunter muss noch überarbeitet werden

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
}
		
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
		} */
		throw new InterWebException("URL: [" + url
                + "] doesn't belong to connector ["
                + getName() + "]");
              
	}
	

	@Override
	public String getUserId(AuthCredentials authCredentials) throws InterWebException
	{
		WebResource resource = createWebResource(VIMEO_BASE +"vimeo.test.login", getAuthCredentials(), authCredentials);
		
		LoginResponse response = resource.get(LoginResponse.class);
		
		return Integer.toString(response.getUser().getId());
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
		// keine möglichkeit gefunden bei vimeo
	}

	@Override
	public UserSocialNetworkResult getUserSocialNetwork(String userid,
			AuthCredentials authCredentials) throws InterWebException {
		// TODO Auto-generated method stub
		return null;
	}	

	
}
