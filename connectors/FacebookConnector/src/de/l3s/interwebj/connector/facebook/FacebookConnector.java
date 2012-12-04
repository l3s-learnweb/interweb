package de.l3s.interwebj.connector.facebook;

import static de.l3s.interwebj.util.Assertions.notNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.ws.rs.core.MediaType;

import l3s.facebook.listresponse.books.Books;
import l3s.facebook.listresponse.books.Data;
import l3s.facebook.listresponse.friends.Friends;
import l3s.facebook.listresponse.likes.Likes;
import l3s.facebook.listresponse.movies.Movies;
import l3s.facebook.listresponse.music.Music;
import l3s.facebook.listresponse.notes.Notes;
import l3s.facebook.listresponse.photos.Photos;
import l3s.facebook.listresponse.profilefeed.Feedobject;
import l3s.facebook.listresponse.profilefeed.ProfileFeed;
import l3s.facebook.listresponse.userevents.Events;
import l3s.facebook.listresponse.usergroups.Groups;
import l3s.facebook.listresponse.userlocations.Objectwithlocation;
import l3s.facebook.listresponse.userlocations.UserLocationObjects;
import l3s.facebook.listresponse.userphotoalbums.Photoalbums;
import l3s.facebook.listresponse.videosuploadedandtagged.Videolist;
import l3s.facebook.objects.event.Event;
import l3s.facebook.objects.group.Group;
import l3s.facebook.objects.note.Note;
import l3s.facebook.objects.page.Page;
import l3s.facebook.objects.photo.Comments;
import l3s.facebook.objects.photo.Photo;
import l3s.facebook.objects.photoalbum.Photoalbum;
import l3s.facebook.objects.user.Education;
import l3s.facebook.objects.user.FavoriteAthletes;
import l3s.facebook.objects.user.FavoriteTeams;
import l3s.facebook.objects.user.Languages;
import l3s.facebook.objects.user.Projects;
import l3s.facebook.objects.user.Sports;
import l3s.facebook.objects.user.User;
import l3s.facebook.objects.user.Work;
import l3s.facebook.objects.video.Video;

import org.apache.commons.lang.NotImplementedException;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.oauth.client.OAuthClientFilter;
import com.sun.jersey.oauth.signature.HMAC_SHA1;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;

import de.l3s.interwebj.AuthCredentials;
import de.l3s.interwebj.InterWebException;
import de.l3s.interwebj.Parameters;
import de.l3s.interwebj.config.Configuration;
import de.l3s.interwebj.core.AbstractServiceConnector;
import de.l3s.interwebj.core.Environment;
import de.l3s.interwebj.core.ServiceConnector;
import de.l3s.interwebj.query.ContactFromSocialNetwork;
import de.l3s.interwebj.query.Query;
import de.l3s.interwebj.query.Query.SortOrder;
import de.l3s.interwebj.query.QueryResult;
import de.l3s.interwebj.query.ResultItem;
import de.l3s.interwebj.query.UserSocialNetworkResult;
import de.l3s.interwebj.socialsearch.SocialSearchQuery;
import de.l3s.interwebj.socialsearch.SocialSearchResult;
import de.l3s.interwebj.socialsearch.SocialSearchResultItem;
import facebook.api.Facebook;

public class FacebookConnector extends AbstractServiceConnector
{			

	private String[] likes_fields= {"title", "description","category","about","website"};
	private String[] notes_fields= {"title", "message"};
	private String[] personality_fields= {"name", "gender","political","education1 type",
			"education1 school","education1 concentration","education1 year",
			"education2 type","education2 school","education2 concentration","education2 year",
			"education3 type","education3 school","education3 concentration","education3 year",
			"education4 type","education4 school","education4 concentration","education4 year",
			"education5 type","education5 school","education5 concentration","education5 year",
			"education6 type","education6 school","education6 concentration","education6 year",
			"work1 location","work1 position", "work1 employer","work1 employer descreption","work1 employer about",
			"work1 project1 name", "work1 project1 description", "work1 project2 name", "work1 project2 description", "work1 project3 name", "work1 project3 description"
			,"work2 location","work2 position", "work2 employer","work2 employer descreption","work2 employer about",
			"work2 project1 name", "work2 project2 description", "work2 project2 name", "work2 project2 description", "work2 project3 name", "work2 project3 description"
			,"work3 location","work3 position", "work3 employer","work3 employer descreption","work3 employer about",
			"work3 project1 name", "work3 project1 description", "work3 project2 name", "work3 project2 description", "work3 project3 name", "work3 project3 description"
			,"work4 location","work4 position", "work4 employer","work4 employer descreption","work4 employer about",
			"work4 project1 name", "work4 project1 description", "work4 project2 name", "work4 project2 description", "work4 project3 name", "work4 project3 description"
			,"work5 location","work5 position", "work5 employer","work5 employer descreption","work5 employer about",
			"work5 project1 name", "work5 project1 description", "work5 project2 name", "work5 project2 description", "work5 project3 name", "work5 project3 description"
			,"work6 location","work6 position", "work6 employer","work6 employer descreption","work6 employer about",
			"work6 project1 name", "work6 project1 description", "work6 project2 name", "work6 project2 description", "work6 project3 name", "work6 project3 description"
			,"languages", "sports","favourite athletes","favourite teams","bio","website","hometown","current location","quotes"};
	private String[] photostaggedin_fields= {"title", "location","city","country","comments","from name"};
	private String[] profilefeed_fields= {"title", "description","message","caption","type","from name","to name","application"};
	private String[] userevents_fields= {"title", "description","location","owner","status"};
	private String[] usergroups_fields= {"title", "description"};
	private String[] userlocations_fields= {"title", "city","country","application"};
	private String[] videostaggedin_fields= {"title", "city","type","country","application","from name"};
	private String[] videosuploaded_fields= {"title", "city","type","country","application","from name"};
	private String[] photoalbums_fields= {"title", "type"};
	
	private static final String CLIENT_ID = "181712555296062";
	private static final String DEVELOPER_KEY = "***REMOVED***";
	private static final String REQUEST_TOKEN_PATH = "https://www.facebook.com/dialog/oauth?client_id=" +CLIENT_ID+
			
			"&redirect_uri=";
	
	private static final String ACCESS_TOKEN_PATH = "https://graph.facebook.com/oauth/access_token?";
	private static final String PERMISSIONS = "user_about_me,user_activities,user_checkins,user_education_history,user_events,user_groups,user_hometown,user_interests,user_likes,user_location,user_notes,user_photos,user_questions,user_relationships,user_relationship_details,user_religion_politics,user_status,user_subscriptions,user_videos,user_work_history,email," +
			"friends_about_me,friends_activities,friends_birthday,friends_checkins,friends_education_history,friends_events,friends_groups,friends_hometown,friends_interests,friends_likes,friends_location," +
			"friends_notes,friends_photos,friends_questions,friends_relationships,friends_relationship_details,friends_religion_politics,friends_status,friends_subscriptions,friends_videos,friends_website,friends_work_history," +
			"friends_actions.music,friends_actions.news,friends_actions.video,friends_games_activity";
	
	private static String CALLBACK_URL = "";
	private static String GRAPHAPI_URL = "https://graph.facebook.com/";
	
	
	
	public FacebookConnector(Configuration configuration)
	{
		this(configuration, null);
		
	}	

	public FacebookConnector(Configuration configuration, AuthCredentials consumerAuthCredentials)
	{
		super(configuration);
		setAuthCredentials(consumerAuthCredentials);
		
	}

	@Override
	public ServiceConnector clone()
	{
		return new FacebookConnector(getConfiguration(), getAuthCredentials());
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
	
	public Date GetLocalDateFromUTCString(String utcLongDateTime) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+SSSS");
	    

	    long when = 0;
	    try {
	        when = dateFormat.parse(utcLongDateTime).getTime();
	    } catch (ParseException e) {
	        e.printStackTrace();
	    }
	    Date localDate = new Date(when + TimeZone.getDefault().getRawOffset() + (TimeZone.getDefault().inDaylightTime(new Date()) ? TimeZone.getDefault().getDSTSavings() : 0));

	    return localDate;
	}
	
	@Override
	public QueryResult get(Query query, AuthCredentials authCredentials) throws InterWebException
	{

		
		notNull(query, "query");
		return socialSearch(query, authCredentials);
//		if (!isRegistered())
//		{
//			throw new InterWebException("Service is not yet registered");
//		}
//		QueryResult queryResult = new QueryResult(query);
//		Facebook fbapi= null;
//		
//			fbapi= new Facebook(authCredentials.getKey());
//			
//			Results resultlist = fbapi.searchPublicPosts(query.getQuery(),query.getResultCount());
//			
//			for(l3s.facebook.search.response.Data data: resultlist.getData())
//			{
//				
//				System.out.println(data.getObjectId()+data.getType());
//				
//				Photo photo= null;
//				Statusupdate status= null;
//				Sharedlink link = null;
//				Number commentcount =  0;
//				int likes=0;
//				ResultItem resultItem = new ResultItem(getName());
//				if(data.getType().equalsIgnoreCase("photo") && data.getObjectId()!=null && query.getContentTypes().contains("image"))
//				{
//					resultItem.setType(Query.CT_IMAGE);
//					photo=fbapi.getEntity(""+data.getObjectId(), Photo.class);
//					/*if(photo.getComments()!=null)
//						commentcount=photo.getComments().getData().size();
//					if(photo.getLikes()!=null)
//						likes=photo.getLikes().getContent().size();*/
//				}
//					
//				else if(data.getType().equalsIgnoreCase("status") && data.getObjectId()!=null && query.getContentTypes().contains("text"))
//				{
//					resultItem.setType(Query.CT_TEXT);
//					status=fbapi.getEntity(""+data.getObjectId(), Statusupdate.class);
//					/*if(status.getComments()!=null)
//						commentcount= status.getComments().getCount();
//					if(status.getLikes()!=null)
//						status.getLikes().getContent().size();*/
//				}
//				else if(data.getType().equalsIgnoreCase("link") && query.getContentTypes().contains("text"))
//				{
//					resultItem.setType(Query.CT_TEXT);
//				}
//				else
//				{
//					continue;
//				}
//				resultItem.setId(data.getId());
//				resultItem.setTitle(data.getName());			
//				resultItem.setDescription(data.getCaption()+"Message:"+data.getMessage());
//				resultItem.setUrl(data.getLink());			
//				resultItem.setDate(CoreUtils.formatDate(GetLocalDateFromUTCString(data.getUpdatedTime())));
//				resultItem.setRank(1);			
//				resultItem.setTotalResultCount(25);
//				int num=0;
//				
//				resultItem.setCommentCount(num+commentcount.intValue());
//				
//				resultItem.setViewCount(likes);
//				if(photo!=null)
//				{
//					Set<Thumbnail> thumbnails = new LinkedHashSet<Thumbnail>();
//					for(Images image: photo.getImages())
//					{
//						
//						thumbnails.add(new Thumbnail(image.getSource(), image.getWidth().intValue(), image.getHeight().intValue()));
//						
//						resultItem.setImageUrl(image.getSource()); // thumbnails are orderd by size. so the last assigned image is the largest
//						
//						if(image.getWidth().intValue() <= 100)
//							resultItem.setEmbeddedSize1(CoreUtils.createImageCode(image.getSource(), image.getWidth().intValue(), image.getHeight().intValue(), 100, 100));
//						
//						else if(image.getWidth().intValue() <= 240)
//							resultItem.setEmbeddedSize2("<img src=\""+ image.getSource() +"\" width=\""+ image.getWidth() +"\" height=\""+ image.getHeight() +"\" />");
//						
//						else if(image.getWidth().intValue() <= 500)
//							resultItem.setEmbeddedSize3("<img src=\""+ image.getSource() +"\" width=\""+ image.getWidth() +"\" height=\""+ image.getHeight() +"\" />");
//						
//						else if(image.getWidth().intValue() > 500)
//							resultItem.setEmbeddedSize4("<img src=\""+ image.getSource() +"\" width=\""+ image.getWidth() +"\" height=\""+ image.getHeight() +"\" />");
//					}	
//
//					resultItem.setThumbnails(thumbnails);
//				}
//				
//				
//				queryResult.addResultItem(resultItem);
//			}
//			
//		
//	
//		return queryResult;
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
		try
		{
			
			callbackUrl=URLEncoder.encode(callbackUrl, "UTF-8");
			CALLBACK_URL=callbackUrl;
			
			Environment.logger.info("interwebjCallbackUrl: ["
			                        + callbackUrl + "]");
			
			
			URL requestTokenUrl =new URL(REQUEST_TOKEN_PATH+CALLBACK_URL+"&response_type=code&scope="+PERMISSIONS);
			
			
			
			
			System.out.println("callbackUrl: [" + CALLBACK_URL + "]");
			System.out.println("requestTokenUrl: ["
			                   + requestTokenUrl.toExternalForm() + "]");
			params.add(Parameters.AUTHORIZATION_URL,
			           requestTokenUrl.toExternalForm());
			
		}
		catch (Exception e)
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
			
			String code = params.get("code");
			System.out.println(code);
		
	
		
	String	$token_url =  ACCESS_TOKEN_PATH
			       + "client_id=" + params.get(Parameters.USER_KEY) + "&redirect_uri=" + CALLBACK_URL
			       + "&client_secret=" +params.get(Parameters.USER_SECRET) + "&code=" +code;
	
	 Client c=Client.create();
	WebResource r = c.resource($token_url);
		ClientResponse res = r.get(ClientResponse.class);

		
		BufferedReader br=new BufferedReader(new InputStreamReader(res.getEntityInputStream()));
		HashMap<String, String> accessparams=new HashMap<String, String>();
		String line = br.readLine();
		br.close();
	for(String pair:line.split("&"))
	{
		String[] kv = pair.split("=");
		accessparams.put(kv[0], kv[1]);
	}
	
	
	
String token=accessparams.get("access_token");
System.out.println(token);		
Hashtable<String, String[]> parmmap = new Hashtable<String, String[]>();

for(String key:accessparams.keySet())
{
	parmmap.put(key, new String []{params.get(key)} );	
}
			
			
			/*String frob = params.get("frob");
			Flickr flickr = createFlickrInstance();
			Environment.logger.info("request token frob: " + frob);
			AuthInterface authInterface = flickr.getAuthInterface();
			Auth auth = authInterface.getToken(frob);*/
			
			
			
			authCredentials = new AuthCredentials(token);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
		return authCredentials;
		
		
		
		
		/*AuthCredentials authCredentials = null;
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
		return authCredentials;*/
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
		/*
		WebResource resource = createWebResource(VIMEO_BASE +"vimeo.test.login", getAuthCredentials(), authCredentials);
		
		LoginResponse response = resource.get(LoginResponse.class);
		*/
		Client c=Client.create();
		String profileurl= GRAPHAPI_URL+"me?access_token="+authCredentials.getKey();
		WebResource r = c.resource(profileurl);
		User user = r.accept(MediaType.APPLICATION_JSON).get(User.class);

		return user.getId().toString();
		//return "unknown";//Integer.toString(response.getUser().getId());
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
		Facebook fb = new Facebook(authCredentials.getKey());
		Friends friends = fb.getFriendsof(userid);
		
		Facebook fbapi= new Facebook(authCredentials.getKey());
		//User user = fbapi.getEntity(userid, User.class);
		
		//String path= "C:\\Users\\singh\\workspaceinterweb\\FacebookConnector\\FacebookIndex\\"+"test";
		String path= "/home/singh/learnweb/FacebookIndex/index";
		
		Lucene base= new Lucene(false, new File(path));
		
		
		/*Lucene groupsbase= new Lucene(false, new File(path+"\\groupbase"));
		Lucene notesbase= new Lucene(false, new File(path+"\\notesbase"));
		Lucene albumbase = new Lucene(false, new File(path+"\\albumsbase"));
		Lucene locationsbase= new Lucene(false, new File(path+"\\locationsbase"));
		Lucene videosuploadedbase= new Lucene(false, new File(path+"\\videosuploadedbase"));
		Lucene likesbase= new Lucene(false, new File(path+"\\likesbase"));
		Lucene personalitybase= new Lucene(false, new File(path+"\\personalitybase"));
		Lucene profilefeedbase= new Lucene(false, new File(path+"\\profilefeedbase"));
		Lucene videostaggedinbase= new Lucene(false, new File(path+"\\videostaggedinbase"));
		Lucene phototaggedbase= new Lucene(false, new File(path+"\\photostaggedinbase"));
		Lucene eventsbase= new Lucene(false, new File(path+"\\eventsbase"));
		
		
		HashMap<String, IndexWriter> writers= new HashMap<String, IndexWriter>();
		
		IndexWriter noteswriter= null;
		IndexWriter photoalbumwriter =null;
		IndexWriter eventswriter = null;
		IndexWriter groupswriter =null;
		IndexWriter taggedphotoswriter= null;
		IndexWriter likeswriter = null;
		IndexWriter feedwriter =null;
		IndexWriter videosuploadedwriter =null;
		IndexWriter videostaggedinwriter =null;
		IndexWriter userwriter=null;
		IndexWriter locationwriter=null;*/
		
		IndexWriter writer=null;
		
		
		try {
			
			writer= base.getWriter();
			
			/*videostaggedinwriter = videostaggedinbase.getWriter();
			writers.put("videostaggedin", videostaggedinwriter);
			likeswriter = likesbase.getWriter();
			writers.put("likeswriter", likeswriter);
			noteswriter=  notesbase.getWriter();
			writers.put("noteswriter", noteswriter);
			videosuploadedwriter= videosuploadedbase.getWriter();
			writers.put("videosuploadedwriter", videosuploadedwriter);
			groupswriter= groupsbase.getWriter();
			writers.put("groupswriter", groupswriter);
			photoalbumwriter=albumbase.getWriter();
			writers.put("photoalbumwriter", photoalbumwriter);
			locationwriter=locationsbase.getWriter();
			writers.put("locationwriter", locationwriter);
			userwriter=personalitybase.getWriter();
			writers.put("userwriter", userwriter);
			feedwriter=profilefeedbase.getWriter();
			writers.put("feedwriter", feedwriter);
			taggedphotoswriter= phototaggedbase.getWriter();
			writers.put("taggedphotoswriter", taggedphotoswriter);
			eventswriter= eventsbase.getWriter();
			writers.put("eventswriter", eventswriter);
			*/
			 
		} catch (Exception e) {
			try {
				writer.close(true);
				/*likeswriter.close();
				noteswriter.close();
				photoalbumwriter.close();
				eventswriter.close();
				groupswriter.close();
				taggedphotoswriter.close();
				feedwriter.close();
				videosuploadedwriter.close();
				videostaggedinwriter.close();
				userwriter.close();
				locationwriter.close();*/
			} catch (CorruptIndexException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	
		
//		new VideosTaggedInThread(userid+ " videos", userid, fbapi, videostaggedinwriter).run();
//		new NotesThread(userid +" notes",userid, fbapi, noteswriter).run();
//		new PhotosTaggedInThread(userid+" photos tagged in", userid, fbapi, taggedphotoswriter).run();
//		new UserEventsThread(userid+" events", userid, fbapi, eventswriter).run();
//		new UserGroupsThread(userid+" groups", userid, fbapi, groupswriter).run();
//		new LikesThread(userid+" likes", userid, fbapi, likeswriter).run();
//		new PhotoAlbumsThread(userid+" albums", userid, fbapi, photoalbumwriter).run();
//		new VideosUploadedThread(userid+" videos uploaded", userid, fbapi, videosuploadedwriter).run();
//		//new ProfileFeedThread(userid+" feed", userid, fbapi, feedwriter).run();
//		new UserLocationsThread(userid+" locations", userid, fbapi, locationwriter).run();
//		new PersonalityThread(userid+" personality", userid, fbapi, userwriter).run();

		
		
		
		UserSocialNetworkResult socialnetwork = new UserSocialNetworkResult(userid);
		int i=0;
		for(l3s.facebook.listresponse.friends.Data friend: friends.getData())
		{
			i++;
			
			ContactFromSocialNetwork contact= new ContactFromSocialNetwork(friend.getName(), friend.getId().toString(), 1, "facebook");
			socialnetwork.getSocialnetwork().put(friend.getId().toString(), contact);
			System.out.println("storing:"+contact.getUsername()+"     "+i);
			try {
			
		    	System.out.println("starting storage " +
		    			"................................................................................."+friend.getName());
				//new StoreFriendThread(userid+" storage thread", friend.getId().toString(), fbapi, writers).run();
		    	System.out.println("storeEvents");
		    	storeEvents(friend.getId().toString(),friend.getName(), writer, fbapi);
		    	System.out.println("storeGroups");
		    	storeGroups(friend.getId().toString(),friend.getName(), writer, fbapi);
		    	System.out.println("storeLikes");
		    	storeLikes(friend.getId().toString(),friend.getName(), writer, fbapi);
		    	System.out.println("storeNotes");
		    	storeNotes(friend.getId().toString(),friend.getName(), writer, fbapi);
		    	System.out.println("storePhotoAlbums");
		    	storePhotoAlbums(friend.getId().toString(),friend.getName(), writer, fbapi);
		    	System.out.println("storeTaggedPhotosOfUser");
		    	storeTaggedPhotosOfUser(friend.getId().toString(),friend.getName(), writer, fbapi);
		    	System.out.println("storeUserPlaces");
		    	storeUserPlaces(friend.getId().toString(),friend.getName(), writer, fbapi);
		    	System.out.println("storeUserProfile");
		    	storeUserProfile(friend.getId().toString(),friend.getName(), writer, fbapi);
		    	System.out.println("storeVideosTaggedIn");
		    	storeVideosTaggedIn(friend.getId().toString(),friend.getName(), writer, fbapi);
		    	System.out.println("storeVideosUploaded");
		    	storeVideosUploaded(friend.getId().toString(),friend.getName(), writer, fbapi);
			} catch (Exception e) {
				e.printStackTrace();
				break;
				
			}
			if(i>10)
				break;
			
			
		}
		try {
			writer.close(true);
			/*likeswriter.forceMerge(10);
			noteswriter.forceMerge(10);
			photoalbumwriter.forceMerge(10);
			eventswriter.forceMerge(10);
			groupswriter.forceMerge(10);
			taggedphotoswriter.forceMerge(10);
			feedwriter.forceMerge(10);
			videosuploadedwriter.forceMerge(10);
			videostaggedinwriter.forceMerge(10);
			userwriter.forceMerge(10);
			locationwriter.forceMerge(10);
			likeswriter.close(true);
			noteswriter.close(true);
			photoalbumwriter.close(true);
			eventswriter.close(true);
			groupswriter.close(true);
			taggedphotoswriter.close(true);
			feedwriter.close(true);
			videosuploadedwriter.close(true);
			videostaggedinwriter.close(true);
			userwriter.close(true);
			locationwriter.close(true);*/
			
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done storage. friends stored="+i);
		return socialnetwork;
	}	
	
	public void storeFriend(String userid, AuthCredentials authCredentials, String belongsToSocialNetworkOf)
	{
		
		System.out.println("done"+userid);
		
	}

	private void storeUserPlaces(String userid, 
			String name, IndexWriter writer, Facebook fbapi) {
		UserLocationObjects locations=fbapi.getUserLocationObjects(userid);
		System.out.println(userid+locations.getData().size());
		if(locations.getData().size()>0)
    	{
    		UserLocationObjects page0 = locations;
    		//severe performance degrade
    		while(page0.getPaging()!=null)
    		{
    			if(page0.getPaging().getNext()==null)
    				break;
    			page0=fbapi.getNextPage(page0.getPaging().getNext(), UserLocationObjects.class);
    			locations.getData().addAll(page0.getData());
    			if (locations.getData().size()>300)
    			{
					break;
				}
    		}
    	
        	for(Objectwithlocation location : locations.getData())
    		{
        		
    			Document doc= new Document();
    			Field field= new Field("id", location.getId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("user name", name, Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("type", location.getType(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("title", location.getPlace().getName(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("city", location.getPlace().getLocation().getCity(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("country", location.getPlace().getLocation().getCountry(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("application", location.getApplication().getName(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("created time", location.getCreatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("From id", location.getFrom().getId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("From name", location.getFrom().getName(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("user", userid, Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("document type", "locations", Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			try {
    				writer.addDocument(doc);
    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} 
    		}
    		
    	}
    		
    		
	}

	private void storeUserProfile(String userid,   String name, IndexWriter writer, Facebook fbapi)
			 {
	
		User friend=fbapi.getEntity(userid, User.class);
		System.out.println(friend.getName()+" personality");
    	Document doc= new Document();
		Field field= new Field("user", userid, Field.Store.YES, Field.Index.NOT_ANALYZED);
		doc.add(field);
		field= new Field("user name", friend.getName(), Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("gender", friend.getGender(), Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("link", friend.getLink(), Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("locale", friend.getLocale(), Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("birthday", friend.getBirthday(), Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("political",friend.getPolitical() , Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("relationship status", friend.getRelationshipStatus(), Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("updated time", friend.getUpdatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
		doc.add(field);
		
		
			
		int i=1;
		for(Education edu: friend.getEducation())
		{
			Document docedu= new Document();
			doc.setBoost(5);
			field= new Field("type", edu.getType(), Field.Store.YES, Field.Index.ANALYZED);
			docedu.add(field);
			field= new Field("user name", friend.getName(), Field.Store.YES, Field.Index.ANALYZED);
			docedu.add(field);
			field= new Field("title", edu.getSchool().getName(), Field.Store.YES, Field.Index.ANALYZED);
			docedu.add(field);
			field= new Field("concentration", edu.getConcentration().getName(), Field.Store.YES, Field.Index.ANALYZED);
			docedu.add(field);
			field= new Field("year", edu.getYear().getName(), Field.Store.YES, Field.Index.ANALYZED);
			docedu.add(field);
			
			Page schoolpage = fbapi.getEntity(edu.getSchool().getId().toString(), Page.class);
			
			field= new Field("description", schoolpage.getDescription(), Field.Store.YES, Field.Index.ANALYZED);
			docedu.add(field);
			field= new Field("about", schoolpage.getAbout(), Field.Store.YES, Field.Index.ANALYZED);
			docedu.add(field);
			field= new Field("link", schoolpage.getLink(), Field.Store.YES, Field.Index.ANALYZED);
			docedu.add(field);
			field= new Field("id", schoolpage.getId().toString(), Field.Store.YES, Field.Index.ANALYZED);
			docedu.add(field);
			i++;
			field= new Field("document type", "education", Field.Store.YES, Field.Index.ANALYZED);
			docedu.add(field);
			
			try {
				writer.addDocument(docedu);
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		i=1;
		for(Work work:friend.getWork())
		{
			Document workdoc= new Document();
			field= new Field("location", work.getLocation().getName(), Field.Store.YES, Field.Index.ANALYZED);
			workdoc.add(field);
			field= new Field("position", work.getPosition().getName(), Field.Store.YES, Field.Index.ANALYZED);
			workdoc.add(field);
			field= new Field("title", work.getEmployer().getName(), Field.Store.YES, Field.Index.ANALYZED);
			workdoc.add(field);
			field= new Field("start date", work.getStartDate(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			workdoc.add(field);
			field= new Field("end date", work.getEndDate(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			workdoc.add(field);
			field= new Field("from", work.getFrom().getName(), Field.Store.YES, Field.Index.ANALYZED);
			workdoc.add(field);
			int j=1;
			for(Projects project: work.getProjects())
			{
				field= new Field("project"+j+" name", project.getName(), Field.Store.YES, Field.Index.ANALYZED);
				workdoc.add(field);
				field= new Field("project"+j+" description", project.getDescription(), Field.Store.YES, Field.Index.ANALYZED);
				workdoc.add(field);
			}
			
			Page workpage = fbapi.getEntity(work.getEmployer().getId().toString(), Page.class);
			
			field= new Field("description", workpage.getDescription(), Field.Store.YES, Field.Index.ANALYZED);
			workdoc.add(field);
			field= new Field("about", workpage.getAbout(), Field.Store.YES, Field.Index.ANALYZED);
			workdoc.add(field);
			field= new Field("link", workpage.getLink(), Field.Store.YES, Field.Index.ANALYZED);
			workdoc.add(field);
			field= new Field("document type", "work", Field.Store.YES, Field.Index.ANALYZED);
			workdoc.add(field);
			field= new Field("user name", name, Field.Store.YES, Field.Index.ANALYZED);
			workdoc.add(field);
			
			i++;
			try {
				writer.addDocument(workdoc);
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		List<Languages> languages = friend.getLanguages();
		String languagelist="";
			for(Languages lang: languages)
			{
				languagelist+=lang.getName()+" ";
			}
		field= new Field("languages", languagelist, Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);	
		
		String athletes="";
		for(FavoriteAthletes athlete :friend.getFavoriteAthletes())
		{
			athletes+= athlete.getName()+ " ";
		}	
		String teams="";
		for(FavoriteTeams team:friend.getFavoriteTeams())
		{
			teams+=team.getName()+" ";
		}
		String sports= "";
		for(Sports sport:friend.getSports())
		{
			sports+=sport.getName()+" ";
		}
		
		field= new Field("sports", sports, Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("favourite athletes", athletes, Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("favourite teams", teams, Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("bio", friend.getBio(), Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("website", friend.getWebsite(), Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("hometown", friend.getHometown().getName(), Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("current location", friend.getLocation().getName(), Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		field= new Field("timezone", friend.getTimezone().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
		doc.add(field);
		field= new Field("quotes", friend.getQuotes().toString(), Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		
		field= new Field("document type", "personality", Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);
		
		
		
		try {
			writer.addDocument(doc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	
		
	}

	

	private void storeVideosTaggedIn(String userid, 
			String name, IndexWriter writer,Facebook fbapi) {
		Videolist videostaggedin=fbapi.getVideosUserIsTaggedIn(userid);
		System.out.println(userid+videostaggedin.getData().size());
		if(videostaggedin.getData().size()>0)
    	{
    		Videolist page0 = videostaggedin;
        	while( page0.getPaging()!=null)
    		{
        		if(page0.getPaging().getNext()==null)
    				break;
    			page0=fbapi.getNextPage(page0.getPaging().getNext(), Videolist.class);
    			videostaggedin.getData().addAll(page0.getData());
    			
    		}
    		for(Video video:videostaggedin.getData())
    		{
    			if (video.getId()==null) {
					continue;
				}
        		System.out.println(video.getName());
    			Document doc= new Document();
    			doc.setBoost(5);
    			Field field= new Field("id", video.getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("user name", name, Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("title", video.getName(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("embed html", video.getEmbedHtml(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("thumbnail", video.getPicture(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("icon", video.getIcon(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("source", video.getSource(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("description", video.getDescription(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			l3s.facebook.objects.video.Comments comments = video.getComments();
    			String commentsinstring= "unknown";
    			if(comments.getData().size()>0)
    			{
    				l3s.facebook.objects.video.Comments compage0 = comments;
    				while(compage0.getPaging().getNext()!=null)
    				{
    					compage0=fbapi.getNextPage(compage0.getPaging().getNext(), l3s.facebook.objects.video.Comments.class);
    					comments.getData().addAll(compage0.getData());
    					
    				}
    				commentsinstring = convertToCommaSeperatedList(comments);
    			}
    				
    			field= new Field("comments",commentsinstring , Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("updated time", video.getUpdatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("created time", video.getCreatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("from name", video.getFrom().getName(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("from id", video.getFrom().getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("user", userid, Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			
    			field= new Field("document type", "videos", Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			try {
    				writer.addDocument(doc);
    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} 
    			
    		}
    		
    	}
    	
		
		
	}

	private String convertToCommaSeperatedList(
			l3s.facebook.objects.video.Comments comments) {
		StringBuilder nameBuilder = new StringBuilder();

		   for( l3s.facebook.objects.video.Data d: comments.getData())
		   {
			   nameBuilder.append("'").append(d.getMessage()).append("',");
		   }
		       
		    

		    nameBuilder.deleteCharAt(nameBuilder.length() - 1);

		    return nameBuilder.toString();
			
	}

	
		       
		    


	private void storeVideosUploaded(String userid,
			String name, IndexWriter writer,Facebook fbapi) {
		 Videolist videosuploaded=fbapi.getVideosUserHasUploaded(userid);
		 System.out.println(userid+videosuploaded.getData().size());
		if(videosuploaded.getData().size()>0)
    	{
    		Videolist page0 = videosuploaded;
        	while( page0.getPaging()!=null)
    		{
        		if(page0.getPaging().getNext()==null)
    				break;
    			page0=fbapi.getNextPage(page0.getPaging().getNext(), Videolist.class);
    			videosuploaded.getData().addAll(page0.getData());
    			
    		}
        	for(Video video:videosuploaded.getData())
    		{
        		if (video.getId()==null) {
					continue;
				}
        		System.out.println(video.getName());
    			Document doc= new Document();
    			doc.setBoost(5);
    			Field field= new Field("id", video.getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("title", video.getName(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("embed html", video.getEmbedHtml(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("thumbnail", video.getPicture(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("icon", video.getIcon(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("source", video.getSource(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("description", video.getDescription(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("user name", name, Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			l3s.facebook.objects.video.Comments comments = video.getComments();
    			String commentsinstring= "unknown";
    			if(comments.getData().size()>0)
    			{
    				l3s.facebook.objects.video.Comments compage0 = comments;
    				while(compage0.getPaging().getNext()!=null)
    				{
    					compage0=fbapi.getNextPage(compage0.getPaging().getNext(), l3s.facebook.objects.video.Comments.class);
    					comments.getData().addAll(compage0.getData());
    					
    				}
    				commentsinstring = convertToCommaSeperatedList(comments);
    			}
    				
    			field= new Field("comments",commentsinstring , Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("updated time", video.getUpdatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("created time", video.getCreatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("from name", video.getFrom().getName(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("from id", video.getFrom().getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("user", userid, Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("document type", "videos uploaded", Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			try {
    				writer.addDocument(doc);
    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} 
    			
    		}
    		
    	}    	
	}

	private void storeProfileFeed(String userid, ProfileFeed profilefeed,
			IndexWriter writer,Facebook fbapi) {
		//increase if performance is good. needs read_stream extended permission to work
		if(profilefeed.getPaging()!=null)
			
		{
			ProfileFeed page1 = fbapi.getNextPage(profilefeed.getPaging().getNext(), ProfileFeed.class);
			ProfileFeed page2 = null;
			if(page1!=null)
			{
			page2 = fbapi.getNextPage(page1.getPaging().getNext(), ProfileFeed.class);
			profilefeed.getData().addAll(page1.getData());
			}
		
			if(page2!=null)
				profilefeed.getData().addAll(page2.getData());
		
		
		
			for(Feedobject feedobj:profilefeed.getData())
			{
				Document doc= new Document();
				Field field= new Field("id", feedobj.getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
				doc.add(field);
				field= new Field("title", feedobj.getName(), Field.Store.YES, Field.Index.ANALYZED);
				doc.add(field);
				field= new Field("message", feedobj.getMessage(), Field.Store.YES, Field.Index.ANALYZED);
				doc.add(field);
				field= new Field("thumbnail", feedobj.getPicture(), Field.Store.YES, Field.Index.NOT_ANALYZED);
				doc.add(field);
				field= new Field("icon", feedobj.getIcon(), Field.Store.YES, Field.Index.NOT_ANALYZED);
				doc.add(field);
				field= new Field("type", feedobj.getType(), Field.Store.YES, Field.Index.ANALYZED);
				doc.add(field);
				
				
				field= new Field("updated time", feedobj.getUpdatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
				doc.add(field);
				field= new Field("description", feedobj.getDescription(), Field.Store.YES, Field.Index.ANALYZED);
				doc.add(field);
				field= new Field("caption", feedobj.getCaption(), Field.Store.YES, Field.Index.ANALYZED);
				doc.add(field);
				field= new Field("link", feedobj.getLink(), Field.Store.YES, Field.Index.ANALYZED);
				doc.add(field);
				field= new Field("application", feedobj.getApplication().getName(), Field.Store.YES, Field.Index.ANALYZED);
				doc.add(field);
				field= new Field("created time", feedobj.getCreatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
				doc.add(field);
				field= new Field("from name", feedobj.getFrom().getName(), Field.Store.YES, Field.Index.ANALYZED);
				doc.add(field);
				field= new Field("from id", feedobj.getFrom().getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
				doc.add(field);
				field= new Field("to name", feedobj.getTo().getData().getName(), Field.Store.YES, Field.Index.ANALYZED);
				doc.add(field);
				field= new Field("to id", feedobj.getTo().getData().getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
				doc.add(field);
			try {
				writer.addDocument(doc);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			}	
			try {
			writer.forceMerge(10);
			writer.close(true);
			} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		}
		
	}

	private void storeLikes(String userid,  String name, IndexWriter writer,Facebook fbapi) {
		Likes likes=fbapi.getUserLikes(userid);
		System.out.println(userid+likes.getData().size());
		if(likes.getData().size()>0)
    	{
    		Likes page0 = likes;
    		
    		while(page0.getPaging()!=null)
    		{
    			if(page0.getPaging().getNext()==null)
    				break;
    			page0=fbapi.getNextPage(page0.getPaging().getNext(), Likes.class);
    			likes.getData().addAll(page0.getData());
//    			if(likes.getData().size()>50)
//    				break;
    			
    		}
    			for(l3s.facebook.listresponse.likes.Data likedpage :likes.getData())
    			{
    				System.out.println(likedpage.getName());
    				if(likedpage.getId()==BigInteger.ZERO)
    					continue;
    				Document doc= new Document();
    				
    				Field field= new Field("id", likedpage.getId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("created time", likedpage.getCreatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("category", likedpage.getCategory(), Field.Store.YES, Field.Index.ANALYZED);
    				doc.add(field);
    				field= new Field("title", likedpage.getName(), Field.Store.YES, Field.Index.ANALYZED);
    				doc.add(field);
    				field= new Field("user name", name, Field.Store.YES, Field.Index.ANALYZED);
        			doc.add(field);
    				Page page = fbapi.getEntity(likedpage.getId().toString(), Page.class);
    				if(page!=null)
    				{
    					field= new Field("about", page.getAbout(), Field.Store.YES, Field.Index.ANALYZED);
        				doc.add(field);
        				field= new Field("description", page.getDescription(), Field.Store.YES, Field.Index.ANALYZED);
        				doc.add(field);
        				field= new Field("cover", page.getCover().getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
        				doc.add(field);
        				field= new Field("likes", page.getLikes().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
        				doc.add(field);
        				field= new Field("link", page.getLink(), Field.Store.YES, Field.Index.ANALYZED);
        				doc.add(field);
        				field= new Field("talking about", page.getTalkingAboutCount().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
        				doc.add(field);
        				field= new Field("website", page.getWebsite(), Field.Store.YES, Field.Index.ANALYZED);
        				doc.add(field);
        				field= new Field("user", userid, Field.Store.YES, Field.Index.ANALYZED);
        				doc.add(field);
    				}
    				
    				field= new Field("document type", "likes", Field.Store.YES, Field.Index.ANALYZED);
        			doc.add(field);
    				try {
    					writer.addDocument(doc);
    				} catch (Exception e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();}
    			}
    		}
    				
    			
	}

	private void storeMusic(String userid, Music music, IndexWriter writer,Facebook fbapi) {
		// TODO Auto-generated method stub
		
	}

	private void storeTaggedPhotosOfUser(String userid,
			 String name, IndexWriter writer,Facebook fbapi) {
		Photos photostaggedin=fbapi.getPhotosOfUser(userid);
		System.out.println(userid+photostaggedin.getData().size());
		//severe performance degrade
		if(photostaggedin.getData().size()>0)
    	{
    		Photos page0 = photostaggedin;
    		//severe performance degrade
    		while(page0.getPaging()!=null)
    		{
    			if(page0.getPaging().getNext()==null)
    				break;
    			page0=fbapi.getNextPage(page0.getPaging().getNext(), Photos.class);
    			photostaggedin.getData().addAll(page0.getData());
			if(photostaggedin.getData().size()>100)
    				break;
    			
    		}
    		for(Photo photo:photostaggedin.getData())
    		{
    			if(photo.getName().equalsIgnoreCase("unknown") && photo.getPlace().getName().equalsIgnoreCase("unknown") && photo.getPlace().getLocation().getCity().equalsIgnoreCase("unknown") && photo.getPlace().getLocation().getCountry().equalsIgnoreCase("unknown") )
    			{
    				continue;
    				
    			}
    			else
    			{
    				Document doc= new Document();
    				doc.setBoost(5);
    				if(photo.getId().equalsIgnoreCase("0"))
    					continue;
    				Field field= new Field("id", photo.getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("user name", name, Field.Store.YES, Field.Index.ANALYZED);
        			doc.add(field);
    				field= new Field("title", photo.getName(), Field.Store.YES, Field.Index.ANALYZED);
    				doc.add(field);
    				field= new Field("link", photo.getLink(), Field.Store.YES, Field.Index.ANALYZED);
    				doc.add(field);
    				field= new Field("thumbnail", photo.getPicture(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("icon", photo.getIcon(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("source", photo.getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("location", photo.getPlace().getName(), Field.Store.YES, Field.Index.ANALYZED);
    				doc.add(field);
    				field= new Field("city", photo.getPlace().getLocation().getCity(), Field.Store.YES, Field.Index.ANALYZED);
    				doc.add(field);
    				field= new Field("country", photo.getPlace().getLocation().getCountry(), Field.Store.YES, Field.Index.ANALYZED);
    				doc.add(field);
    				
    				l3s.facebook.objects.photo.Comments comments = photo.getComments();
    				String commentsinstring= "unknown";
    				
    				if(comments!=null && comments.getData().size()>0)
    				{
    					Comments compage0= comments;
    					while(compage0.getPaging()!=null)
    					{
    						if(compage0.getPaging().getNext()==null)
    							break;
    						compage0=fbapi.getNextPage(compage0.getPaging().getNext(), Comments.class);
    						comments.getData().addAll(compage0.getData());
    						
    					}
    					commentsinstring = convertToCommaSeperatedList(comments);
    				}
    				
    				
    				field= new Field("comments",commentsinstring , Field.Store.YES, Field.Index.ANALYZED);
    				doc.add(field);
    				field= new Field("updated time", photo.getUpdatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("created time", photo.getCreatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("from name", photo.getFrom().getName(), Field.Store.YES, Field.Index.ANALYZED);
    				doc.add(field);
    				field= new Field("from id", photo.getFrom().getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image 0 source", photo.getImages().get(0).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image 0 height", photo.getImages().get(0).getHeight().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image 0 width", photo.getImages().get(0).getWidth().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image 1 source", photo.getImages().get(1).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image 1 height", photo.getImages().get(1).getHeight().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image 1 width", photo.getImages().get(1).getWidth().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image 2 source", photo.getImages().get(2).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image 2 height", photo.getImages().get(2).getHeight().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image 2 width", photo.getImages().get(2).getWidth().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image 3 source", photo.getImages().get(3).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image 3 height", photo.getImages().get(3).getHeight().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image 3 width", photo.getImages().get(3).getWidth().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image 4 source", photo.getImages().get(4).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image 4 height", photo.getImages().get(4).getHeight().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image 4 width", photo.getImages().get(4).getWidth().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image 5 source", photo.getImages().get(5).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image 5 height", photo.getImages().get(5).getHeight().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image 5 width", photo.getImages().get(5).getWidth().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image 6 source", photo.getImages().get(6).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image 6 height", photo.getImages().get(6).getHeight().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image 6 width", photo.getImages().get(6).getWidth().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image 7 source", photo.getImages().get(7).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image 7 height", photo.getImages().get(7).getHeight().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image 7 width", photo.getImages().get(7).getWidth().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("user", userid, Field.Store.YES, Field.Index.ANALYZED);
    				doc.add(field);
    				field= new Field("document type", "photos", Field.Store.YES, Field.Index.ANALYZED);
        			doc.add(field);
    				try {
    					writer.addDocument(doc);
    				} catch (Exception e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				} 
    			}
    			
    			
    		}
    	}
	}

	private String convertToCommaSeperatedList(l3s.facebook.objects.photo.Comments comments) {
		StringBuilder nameBuilder = new StringBuilder();

	   for(l3s.facebook.objects.photo.Data d: comments.getData())
	   {
		   nameBuilder.append("'").append(d.getMessage()).append("',");
	   }
	       
	    

	    nameBuilder.deleteCharAt(nameBuilder.length() - 1);

	    return nameBuilder.toString();
		
	}

	
	

	private void storeGroups(String userid,  String name, IndexWriter writer,Facebook fbapi) {
		Groups groups= fbapi.getGroupsUserIsMemberOf(userid);
		System.out.println(userid+groups.getData().size());
		if(groups.getData().size()>0)
    	{
    		Groups page0 = groups;
    		//severe performance degrade
    		while(page0.getPaging()!=null)
    		{
    			if(page0.getPaging().getNext()==null)
    				break;
    			page0=fbapi.getNextPage(page0.getPaging().getNext(), Groups.class);
    			groups.getData().addAll(page0.getData());
    		}
    		for(l3s.facebook.listresponse.usergroups.Data group :groups.getData())
    		{
    			if(group.getId()==null)
    				continue;
    			Document doc= new Document();
    			Field field= new Field("id", group.getId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("user name", name, Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("title", group.getName(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			
    			Group usergroup = fbapi.getEntity(group.getId().toString(), Group.class);
    			
    			field= new Field("description", usergroup.getDescription(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("email", usergroup.getEmail(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("icon", usergroup.getIcon(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("privacy", usergroup.getPrivacy(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("updated time", usergroup.getUpdatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("user", userid, Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("document type", "group", Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			
    			try {
    				writer.addDocument(doc);
    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} 
    			
    		}
    		
    		
    	}
    	
	}

	private void storeMovies(String userid, Movies movies, IndexWriter writer,Facebook fbapi) {
		for( l3s.facebook.listresponse.movies.Data movie :movies.getData())
		{
			Document doc= new Document();
			Field field= new Field("id", movie.getId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("created time", movie.getCreatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("category", movie.getCategory(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("title", movie.getName(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			
			Page moviepage = fbapi.getEntity(movie.getId().toString(), Page.class);
			
			field= new Field("about", moviepage.getAbout(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("description", moviepage.getDescription(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("cover", moviepage.getCover().getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("likes", moviepage.getLikes().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("talking about", moviepage.getTalkingAboutCount().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("user", userid, Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			try {
				writer.addDocument(doc);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		
		try {
			writer.forceMerge(10);
			writer.close(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}

	private void storeEvents(String userid,  String name, IndexWriter writer,Facebook fbapi) {
		Events events=fbapi.getEventsUserIsInvolvedIn(userid);
		System.out.println(userid+events.getData().size());
		if(events.getData().size()>0)
    	{
    		Events page0 = events;
    		//severe performance degrade
    		while(page0.getPaging()!=null)
    		{
    			if(page0.getPaging().getNext()==null)
    				break;
    			page0=fbapi.getNextPage(page0.getPaging().getNext(), Events.class);
    			events.getData().addAll(page0.getData());
    		}
    		for( l3s.facebook.listresponse.userevents.Data event :events.getData())
    		{
    			if(event.getId()==null)
    				continue;
    			Document doc= new Document();
    			Field field= new Field("id", event.getId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("title", event.getName(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("location", event.getLocation(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("end time", event.getEndTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("start time", event.getStartTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			
    			
    			field= new Field("status", event.getRsvpStatus(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("timezone", event.getTimezone(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			
    			Event eventpage = fbapi.getEntity(event.getId().toString(), Event.class);
    			
    			if(eventpage!=null)
    			{
    				field= new Field("description", new String(eventpage.getDescription().clone()), Field.Store.YES, Field.Index.ANALYZED);
        			doc.add(field);
        			field= new Field("privacy", eventpage.getPrivacy(), Field.Store.YES, Field.Index.NOT_ANALYZED);
        			doc.add(field);
        			field= new Field("owner", eventpage.getOwner().getName(), Field.Store.YES, Field.Index.ANALYZED);
        			doc.add(field);
        			field= new Field("updated time", eventpage.getUpdatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
        			doc.add(field);
        			field= new Field("user", userid, Field.Store.YES, Field.Index.ANALYZED);
        			doc.add(field);
        			field= new Field("user name", name, Field.Store.YES, Field.Index.ANALYZED);
        			doc.add(field);
        			field= new Field("document type", "event", Field.Store.YES, Field.Index.ANALYZED);
        			doc.add(field);
    			}
    			
    			
    			try {
    				writer.addDocument(doc);
    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} 
    		}
    		
    		
    		
    	}
    	
		
	}

	private void storeBooks(String userid, Books books, IndexWriter writer,Facebook fbapi) {
		for(Data book: books.getData())
		{
			Document doc= new Document();
			Field field= new Field("id", book.getId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("created time", book.getCreatedTime(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("title", book.getName(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("category", book.getCategory(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("user", userid, Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			
			try {
				writer.addDocument(doc);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		
		try {
			writer.forceMerge(10);
			writer.close(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}

	private void storePhotoAlbums(String userid, 
			String name, IndexWriter writer,Facebook fbapi) {
		Photoalbums photoalbums=fbapi.getAlbumsOfUser(userid);
		System.out.println(userid+photoalbums.getData().size());
		if(photoalbums.getData().size()>0)
		{
			Photoalbums page0 = photoalbums;
			//severe performance degrade
			while(page0.getPaging()!=null)
			{
				if(page0.getPaging().getNext()==null)
					break;
				page0=fbapi.getNextPage(page0.getPaging().getNext(), Photoalbums.class);
				photoalbums.getData().addAll(page0.getData());
			}
		
		
		//severe performance degrade
		for(Photoalbum album: photoalbums.getData())
		{
			if(album.getId()==null)
				continue;
			Document doc= new Document();
			doc.setBoost(5);
			Field field= new Field("id", album.getId(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("user name", name, Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("updated time", album.getUpdatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("created time", album.getCreatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("link", album.getLink(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("title", album.getName(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("type", album.getType(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("Cover Photo id", album.getCoverPhoto().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
		
			
			Photo photo= fbapi.getEntity(album.getCoverPhoto().toString(), Photo.class);
			if(photo!=null)
			{
				if(photo.getImages().size()>0)
				{
					field= new Field("image 0 source", photo.getImages().get(0).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
					doc.add(field);
					field= new Field("image 0 height", photo.getImages().get(0).getHeight().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
					doc.add(field);
					field= new Field("image 0 width", photo.getImages().get(0).getWidth().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
					doc.add(field);
					field= new Field("image 1 source", photo.getImages().get(1).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
					doc.add(field);
					field= new Field("image 1 height", photo.getImages().get(1).getHeight().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
					doc.add(field);
					field= new Field("image 1 width", photo.getImages().get(1).getWidth().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
					doc.add(field);
					field= new Field("image 2 source", photo.getImages().get(2).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
					doc.add(field);
					field= new Field("image 2 height", photo.getImages().get(2).getHeight().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
					doc.add(field);
					field= new Field("image 2 width", photo.getImages().get(2).getWidth().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
					doc.add(field);
					field= new Field("image 3 source", photo.getImages().get(3).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
					doc.add(field);
					field= new Field("image 3 height", photo.getImages().get(3).getHeight().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
					doc.add(field);
					field= new Field("image 3 width", photo.getImages().get(3).getWidth().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
					doc.add(field);
					field= new Field("image 4 source", photo.getImages().get(4).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
					doc.add(field);
					field= new Field("image 4 height", photo.getImages().get(4).getHeight().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
					doc.add(field);
					field= new Field("image 4 width", photo.getImages().get(4).getWidth().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
					doc.add(field);
					field= new Field("image 5 source", photo.getImages().get(5).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
					doc.add(field);
					field= new Field("image 5 height", photo.getImages().get(5).getHeight().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
					doc.add(field);
					field= new Field("image 5 width", photo.getImages().get(5).getWidth().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
					doc.add(field);
					field= new Field("image 6 source", photo.getImages().get(6).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
					doc.add(field);
					field= new Field("image 6 height", photo.getImages().get(6).getHeight().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
					doc.add(field);
					field= new Field("image 6 width", photo.getImages().get(6).getWidth().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
					doc.add(field);
					field= new Field("image 7 source", photo.getImages().get(7).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
					doc.add(field);
					field= new Field("image 7 height", photo.getImages().get(7).getHeight().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
					doc.add(field);
					field= new Field("image 7 width", photo.getImages().get(7).getWidth().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
				}
				
				
				
				
			}
			
			
			field= new Field("from id", album.getFrom().getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("from name", album.getFrom().getName(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			
			if(album.getLikes()!=null)
			{
				field= new Field("likes", ""+album.getLikes().getData().size(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
			}
			
			field= new Field("user", userid, Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			
			field= new Field("document type", "photo albums", Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			try {
				writer.addDocument(doc);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		
		
		
		}
		
		
	}

	private void storeNotes(String userid,  String name, IndexWriter writer, Facebook fbapi) {
		Notes notes=fbapi.getNotesOfUser(userid);
		
    	System.out.println(userid+notes.getData().size());
    	if(notes.getData().size()>0)
    	{
    		Notes page0 = notes;
    		//severe performance degrade
    		while(page0.getPaging()!=null)
    		{
    			if(page0.getPaging().getNext()==null)
    				break;
    			page0=fbapi.getNextPage(page0.getPaging().getNext(), Notes.class);
    			notes.getData().addAll(page0.getData());
    			if(notes.getData().size()>50)
    				break;
    			
    		}
        	for(Note n: notes.getData())
    		{
        		if(n.getId()==null)
        		continue;
        		
        		System.out.println(n.getSubject());
    			Document doc= new Document();
    			Field field= new Field("id", n.getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("user name", name, Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("message", n.getMessage(), Field.Store.NO, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("title", n.getSubject(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("updated time", n.getUpdatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("created time", n.getCreatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("From id", n.getFrom().getId(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("From name", n.getFrom().getName(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("user", userid, Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("document type", "note", Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			try {
    				writer.addDocument(doc);
    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} 
    		}
        	
    		
    	}
	}
	public QueryResult socialSearch(Query query, AuthCredentials authCredentials)
	{
		
		notNull(query, "query");
		if (!isRegistered())
		{
			try {
				throw new InterWebException("Service is not yet registered");
			} catch (InterWebException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		QueryResult qresult= new QueryResult(query);
		String userid = null;
		try {
			userid = getUserId(authCredentials);
		} catch (InterWebException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String path= "/home/singh/learnweb/FacebookIndex/index";
		//String path= "C:\\Users\\singh\\workspaceinterweb\\FacebookConnector\\FacebookIndex\\"+userid+"j";
		
		
		Lucene base= new Lucene(false, new File(path));
		String querystr = query.getQuery();
		String[] fields= { "gender","political","concentration","document type","descreption","description","title",
				"project1 name", "project1 description", "project2 name", "project2 description", "project3 name", "project3 description"
				,"languages", "sports","favourite athletes","favourite teams","bio","website","hometown","current location","quotes","location","city","country","comments","from name",
				"message","caption","type","category","about","website"};
		ArrayList<Document> hits = base.searchIndex(querystr, fields, query.getResultCount());
		
		for(Document doc: hits)
		{
			ResultItem result= new ResultItem(getName());
			result.setServiceName(doc.get("document type"));
			result.setType(Query.CT_TEXT);
			result.setId(doc.get("user"));
			result.setTitle(doc.get("title"));			
			String desc= new String();
			if(doc.get("description")!=null&&!doc.get("description").equalsIgnoreCase("unknown"))
				desc+=doc.get("description")+". ";
			if(doc.get("about")!=null&& doc.get("about").equalsIgnoreCase("unknown"))
				desc+=doc.get("about")+". ";
			if(doc.get("category")!=null && !doc.get("category").equalsIgnoreCase("unknown"))
				desc+=doc.get("category")+". ";
			result.setDescription(desc);
			//result.setDescription(doc.get("description")+" About:"+doc.get("about")+". Category:"+doc.get("catefory")+ ". Website:"+ doc.get("website"));
			result.setUrl(doc.get("link"));			
			result.setDate(doc.get("created time"));
			result.setRank(1);			
			result.setTotalResultCount(25);
			
			
//			result.setCommentCount(Integer.getInteger(doc.get("likes")));
//			
//			result.setViewCount(Integer.getInteger(doc.get("talking about")));
			
			qresult.addResultItem(result);
		}
		
//		
//		Lucene groupsbase= new Lucene(false, new File(path+"\\groupbase"));
//		Lucene notesbase= new Lucene(false, new File(path+"\\notesbase"));
//		Lucene albumbase = new Lucene(false, new File(path+"\\albumsbase"));
//		Lucene locationsbase= new Lucene(false, new File(path+"\\locationsbase"));
//		Lucene videosuploadedbase= new Lucene(false, new File(path+"\\videosuploadedbase"));
//		
//		Lucene personalitybase= new Lucene(false, new File(path+"\\personalitybase"));
//		//Lucene profilefeedbase= new Lucene(false, new File(path+"\\profilefeedbase"));
//		Lucene videostaggedinbase= new Lucene(false, new File(path+"\\videostaggedinbase"));
//		Lucene phototaggedbase= new Lucene(false, new File(path+"\\photostaggedinbase"));
//		Lucene eventsbase= new Lucene(false, new File(path+"\\eventsbase"));
//		Lucene likesbase= new Lucene(false, new File(path+"\\likesbase"));
//		
//		
//
//		ArrayList<Document> likes = likesbase.searchIndex(querystr,likes_fields,10);
//		
//		
//		for(Document doc: likes)
//		{
//			ResultItem result= new ResultItem(getName());
//			result.setServiceName("likes");
//			result.setType(Query.CT_TEXT);
//			result.setId(doc.get("user"));
//			result.setTitle(doc.get("title"));			
//			result.setDescription("default");
//			//result.setDescription(doc.get("description")+" About:"+doc.get("about")+". Category:"+doc.get("catefory")+ ". Website:"+ doc.get("website"));
//			result.setUrl(doc.get("link"));			
//			result.setDate(doc.get("created time"));
//			result.setRank(1);			
//			result.setTotalResultCount(25);
//			
//			
////			result.setCommentCount(Integer.getInteger(doc.get("likes")));
////			
////			result.setViewCount(Integer.getInteger(doc.get("talking about")));
//			
//			qresult.addResultItem(result);
//		}
//		
//		
//		ArrayList<Document> notes = notesbase.searchIndex(querystr,notes_fields,10);
//		for(Document doc: notes)
//		{
//			ResultItem result= new ResultItem(getName());
//			result.setServiceName("notes");
//			result.setType(Query.CT_TEXT);
//			result.setId(doc.get("user"));
//			result.setTitle(doc.get("title"));			
//			result.setDescription(doc.get("message"));
//			result.setUrl(doc.get("link"));			
//			result.setDate(doc.get("created time"));
//			result.setRank(1);			
//			result.setTotalResultCount(25);
//			
//			
////			result.setCommentCount(Integer.parseInt(doc.get("likes")));
////			
////			result.setViewCount(Integer.parseInt(doc.get("talking about")));
//			
//			qresult.addResultItem(result);
//		}
//		
//		
//		ArrayList<Document> personality = personalitybase.searchIndex(querystr,personality_fields,10);
//		for(Document doc: personality)
//		{
//			ResultItem result= new ResultItem(getName());
//			result.setServiceName("personality");
//			result.setType(Query.CT_TEXT);
//			result.setId(doc.get("id"));
//			result.setTitle(doc.get("name"));			
//			result.setDescription(doc.get("bio"));
//			result.setUrl(doc.get("link"));			
//			result.setDate(doc.get("created time"));
//			result.setRank(1);			
//			result.setTotalResultCount(25);
//			result.setImageUrl("https://graph.facebook.com/"+doc.get("id")+"/picture");
//			
////			result.setCommentCount(Integer.parseInt(doc.get("likes")));
////			
////			result.setViewCount(Integer.parseInt(doc.get("talking about")));
//			
//			qresult.addResultItem(result);
//		}
//		
//		ArrayList<Document> photoalbums = albumbase.searchIndex(querystr,photoalbums_fields,10);
//		ArrayList<Document> photostaggedin = phototaggedbase.searchIndex(querystr,photostaggedin_fields,10);
//		for(Document doc: photostaggedin)
//		{
//			ResultItem result= new ResultItem(getName());
//			result.setServiceName("photos");
//			result.setType(Query.CT_IMAGE);
//			result.setId(doc.get("user"));
//			result.setTitle(doc.get("title"));			
//			result.setDescription(doc.get("location"));
//			result.setUrl(doc.get("link"));			
//			result.setDate(doc.get("created time"));
//			result.setRank(1);			
//			result.setTotalResultCount(25);
//			Set<Thumbnail> thumbnails = new LinkedHashSet<Thumbnail>();
//			result.setImageUrl(doc.get("image 0 source"));
//			for(int i=0;i<8;i++)
//			{
//				Images img= new Images();
//				img.setSource(doc.get("image "+i+" source"));
//				img.setHeight(new BigInteger(doc.get("image "+i+" height")));
//				img.setWidth(new BigInteger(doc.get("image "+i+" width")));
//				thumbnails.add(new Thumbnail(img.getSource(), img.getWidth().intValue(), img.getHeight().intValue()));
//				
//				 // thumbnails are orderd by size. so the last assigned image is the largest
//				
//				if(img.getWidth().intValue() <= 100)
//					result.setEmbeddedSize1(CoreUtils.createImageCode(img.getSource(), img.getWidth().intValue(), img.getHeight().intValue(), 100, 100));
//				
//				else if(img.getWidth().intValue() <= 240)
//					result.setEmbeddedSize2("<img src=\""+ img.getSource() +"\" width=\""+ img.getWidth() +"\" height=\""+ img.getHeight() +"\" />");
//				
//				else if(img.getWidth().intValue() <= 500)
//					result.setEmbeddedSize3("<img src=\""+ img.getSource() +"\" width=\""+ img.getWidth() +"\" height=\""+ img.getHeight() +"\" />");
//				
//				else if(img.getWidth().intValue() > 500)
//					result.setEmbeddedSize4("<img src=\""+ img.getSource() +"\" width=\""+ img.getWidth() +"\" height=\""+ img.getHeight() +"\" />");
//			}
////			result.setCommentCount(Integer.getInteger(doc.get("likes")));
////			
////			result.setViewCount(Integer.getInteger(doc.get("talking about")));
//			
//			
//			
//			result.setThumbnails(thumbnails);
//			
//			
//			
//			qresult.addResultItem(result);
//		}
//		
//		
//		
//		
//		
//		//ArrayList<Document> profilefeed = profilefeedbase.searchIndex(querystr,profilefeed_fields,10);
//		ArrayList<Document> userevents = eventsbase.searchIndex(querystr,userevents_fields,10);
//		for(Document doc: userevents)
//		{
//			ResultItem result= new ResultItem(getName());
//			result.setServiceName("events");
//			result.setType(Query.CT_TEXT);
//			result.setId(doc.get("user"));
//			result.setTitle(doc.get("title"));			
//			result.setDescription(doc.get("description"));
//			result.setUrl(doc.get("link"));			
//			result.setDate(doc.get("updated time"));
//			result.setRank(1);			
//			result.setTotalResultCount(25);
//			
//			
//			
//			
//			qresult.addResultItem(result);
//		}
//		
//		
//		ArrayList<Document> usergroups = groupsbase.searchIndex(querystr,usergroups_fields,10);
//		for(Document doc: usergroups)
//		{
//			ResultItem result= new ResultItem(getName());
//			result.setServiceName("groups");
//			result.setType(Query.CT_TEXT);
//			result.setId(doc.get("user"));
//			result.setTitle(doc.get("title"));			
//			result.setDescription(doc.get("description"));
//			result.setUrl(doc.get("link"));			
//			result.setDate(doc.get("updated time"));
//			result.setRank(1);			
//			result.setTotalResultCount(25);
//			
//			
//			
//			
//			qresult.addResultItem(result);
//		}
//		
//		
//		ArrayList<Document> userlocations = locationsbase.searchIndex(querystr,userlocations_fields,10);
//		for(Document doc: userlocations)
//		{
//			ResultItem result= new ResultItem(getName());
//			result.setServiceName("locations");
//			result.setType(Query.CT_TEXT);
//			result.setId(doc.get("user"));
//			result.setTitle(doc.get("title"));			
//			result.setDescription(doc.get("city")+doc.get("country"));
//			result.setUrl(doc.get("link"));			
//			result.setDate(doc.get("created time"));
//			result.setRank(1);			
//			result.setTotalResultCount(25);
//			
//			
//			
//			
//			qresult.addResultItem(result);
//		}
//		ArrayList<Document> videostaggedin = videostaggedinbase.searchIndex(querystr,videostaggedin_fields,10);
//		for(Document doc: videostaggedin)
//		{
//			ResultItem result= new ResultItem(getName());
//			result.setServiceName("videos");
//			result.setType(Query.CT_VIDEO);
//			result.setId(doc.get("user"));
//			result.setTitle(doc.get("title"));			
//			result.setDescription(doc.get("description"));
//			result.setUrl(doc.get("source"));			
//			result.setDate(doc.get("created time"));
//			result.setRank(1);			
//			result.setTotalResultCount(25);
//			Set<Thumbnail> thumbnails = new LinkedHashSet<Thumbnail>();
//			Thumbnail t= new Thumbnail(doc.get("thumbnail"), 160, 89);
//			result.setEmbeddedSize1(CoreUtils.createImageCode(t, 100, 100));
//			result.setThumbnails(thumbnails);
//			result.setEmbeddedSize3(doc.get("embed html"));
//			result.setImageUrl(t.getUrl());
////			result.setCommentCount(Integer.getInteger(doc.get("likes")));
////			
////			result.setViewCount(Integer.getInteger(doc.get("talking about")));
//			
//			
//			
//			
//			
//			
//			
//			qresult.addResultItem(result);
//		}
//		
//		ArrayList<Document> videosuploaded = videosuploadedbase.searchIndex(querystr,videosuploaded_fields,10);
//		for(Document doc: videosuploaded)
//		{
//			ResultItem result= new ResultItem(getName());
//			result.setServiceName("videos");
//			result.setType(Query.CT_VIDEO);
//			result.setId(doc.get("user"));
//			result.setTitle(doc.get("title"));			
//			result.setDescription(doc.get("description"));
//			result.setUrl(doc.get("source"));			
//			result.setDate(doc.get("created time"));
//			result.setRank(1);			
//			result.setTotalResultCount(25);
//			Set<Thumbnail> thumbnails = new LinkedHashSet<Thumbnail>();
//			Thumbnail t= new Thumbnail(doc.get("thumbnail"), 160, 89);
//			result.setEmbeddedSize1(CoreUtils.createImageCode(t, 100, 100));
//			result.setThumbnails(thumbnails);
//			result.setEmbeddedSize3(doc.get("embed html"));
//			result.setImageUrl(t.getUrl());
////			result.setCommentCount(Integer.getInteger(doc.get("likes")));
////			
////			result.setViewCount(Integer.getInteger(doc.get("talking about")));
//			
//			
//			
//			
//			
//			
//			
//			qresult.addResultItem(result);
//		}
//		Collections.sort(qresult.getResultItems(),COMPARATOR );
		
		System.out.println("done");
		return qresult;
	}
	

	private static Comparator<ResultItem> COMPARATOR = new Comparator<ResultItem>()
				    {
				    

						@Override
						public int compare(ResultItem arg0, ResultItem arg1) {
							int dif= Integer.parseInt(arg0.getId())-Integer.parseInt(arg1.getId());
							return dif;
						}
				    };



	@Override
	public SocialSearchResult get(SocialSearchQuery query,
			AuthCredentials authCredentials) {
		SocialSearchResult result= new SocialSearchResult(query);
		SocialSearchResult res = socialSearchFB(query, authCredentials);
		result.getResultItems().addAll(res.getResultItems());
		// TODO Auto-generated method stub
		return result;
	}

	private SocialSearchResult socialSearchFB(
			SocialSearchQuery query, AuthCredentials authCredentials) {
		
		notNull(query, "query");
		if (!isRegistered())
		{
			try {
				throw new InterWebException("Service is not yet registered");
			} catch (InterWebException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		SocialSearchResult socialresult= new SocialSearchResult(query);
		String userid = null;
		try {
			userid = getUserId(authCredentials);
		} catch (InterWebException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String path= "C:\\Users\\singh\\workspaceinterweb\\FacebookConnector\\FacebookIndex\\"+userid;
		Lucene base= new Lucene(false, new File(path+"new"));
		
		String querystr = query.getQuery();
		

		ArrayList<Document> hits = base.searchIndex(querystr,likes_fields,200);
		HashMap<String, ArrayList<Document>> userdocset= new HashMap<String, ArrayList<Document>>();
		for(Document doc: hits)
		{
			if(userdocset.keySet().contains(doc.get("user")))
			{
				userdocset.get(doc.get("user")).add(doc);
			}
			else
			{
				ArrayList<Document> docs= new ArrayList<Document>();
				docs.add(doc);
				userdocset.put(doc.get("user"), docs);
			}
		}
		for(String key: userdocset.keySet())
		{
		
			SocialSearchResultItem resultItem= new SocialSearchResultItem("me", key, GRAPHAPI_URL+userid+"/picture", userdocset.get(key));
			socialresult.addResultItem(resultItem);
		}
		System.out.println("done");
		return socialresult;
	}
	
}
