package de.l3s.interwebj.connector.facebook;

import static de.l3s.interwebj.util.Assertions.notNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashSet;
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
import l3s.facebook.objects.link.Sharedlink;
import l3s.facebook.objects.note.Note;
import l3s.facebook.objects.page.Page;
import l3s.facebook.objects.photo.Images;
import l3s.facebook.objects.photo.Photo;
import l3s.facebook.objects.photoalbum.Photoalbum;
import l3s.facebook.objects.status.Statusupdate;
import l3s.facebook.objects.user.Education;
import l3s.facebook.objects.user.FavoriteAthletes;
import l3s.facebook.objects.user.FavoriteTeams;
import l3s.facebook.objects.user.Languages;
import l3s.facebook.objects.user.Projects;
import l3s.facebook.objects.user.Sports;
import l3s.facebook.objects.user.User;
import l3s.facebook.objects.user.Work;
import l3s.facebook.objects.video.Video;
import l3s.facebook.search.publicposts.Publicpostresults;
import l3s.facebook.search.response.Results;

import org.apache.commons.lang.NotImplementedException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.util.Version;

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
import de.l3s.interwebj.query.Thumbnail;
import de.l3s.interwebj.query.UserSocialNetworkResult;
import de.l3s.interwebj.util.CoreUtils;
import facebook.api.Facebook;
public class FacebookConnector2 extends AbstractServiceConnector
{			

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
	
	
	
	public FacebookConnector2(Configuration configuration)
	{
		this(configuration, null);
		
	}	

	public FacebookConnector2(Configuration configuration, AuthCredentials consumerAuthCredentials)
	{
		super(configuration);
		setAuthCredentials(consumerAuthCredentials);
		
	}

	@Override
	public ServiceConnector clone()
	{
		return new FacebookConnector2(getConfiguration(), getAuthCredentials());
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
		if (!isRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		QueryResult queryResult = new QueryResult(query);
		Facebook fbapi= null;
		if(authCredentials==null)
			{
				fbapi= new Facebook(null);
				Publicpostresults results = fbapi.searchPublicPostsWithoutAuth(query.getQuery(), query.getResultCount());
				for(l3s.facebook.search.publicposts.Data data: results.getData())
				{
					ResultItem resultItem = new ResultItem(getName());
					if(data.getType().equalsIgnoreCase("photo") && query.getContentTypes().contains("image"))
					{
						resultItem.setType(Query.CT_IMAGE);
						
					}
						
					else if(data.getType().equalsIgnoreCase("status") && query.getContentTypes().contains("text"))
					{
						resultItem.setType(Query.CT_TEXT);
						
					}
					else if(data.getType().equalsIgnoreCase("link") && query.getContentTypes().contains("text"))
					{
						resultItem.setType(Query.CT_TEXT);
					}
					else
					{
						continue;
					}
					resultItem.setId(data.getId());
					resultItem.setTitle(data.getName());			
					resultItem.setDescription(data.getCaption()+"Message:"+data.getMessage());
					resultItem.setUrl(data.getLink());			
					resultItem.setDate(CoreUtils.formatDate(GetLocalDateFromUTCString(data.getCreatedTime())));
					resultItem.setRank(1);			
					resultItem.setTotalResultCount(25);
					int num=0;
					
					resultItem.setCommentCount(num);
					
					resultItem.setViewCount(0);
					
//					if(resultItem.getType()==Query.CT_IMAGE)
//					{
//						Set<Thumbnail> thumbnails = new LinkedHashSet<Thumbnail>();
//						
//							
//							thumbnails.add(new Thumbnail(data.getPicture(), 100, 100));
//							
//							resultItem.setImageUrl(data.getPicture()); // thumbnails are orderd by size. so the last assigned image is the largest
//							
//							resultItem.setEmbeddedSize1(CoreUtils.createImageCode(data.getPicture(), image.getWidth().intValue(), image.getHeight().intValue(), 100, 100));
//							
//							else if(image.getWidth().intValue() <= 240)
//								resultItem.setEmbeddedSize2("<img src=\""+ image.getSource() +"\" width=\""+ image.getWidth() +"\" height=\""+ image.getHeight() +"\" />");
//							
//							else if(image.getWidth().intValue() <= 500)
//								resultItem.setEmbeddedSize3("<img src=\""+ image.getSource() +"\" width=\""+ image.getWidth() +"\" height=\""+ image.getHeight() +"\" />");
//							
//							else if(image.getWidth().intValue() > 500)
//								resultItem.setEmbeddedSize4("<img src=\""+ image.getSource() +"\" width=\""+ image.getWidth() +"\" height=\""+ image.getHeight() +"\" />");
//						}	
//
//						resultItem.setThumbnails(thumbnails);
//					}
					
					
					queryResult.addResultItem(resultItem);
				}
				
			}
		else
		{
			fbapi= new Facebook(authCredentials.getKey());
			
			Results resultlist = fbapi.searchPublicPosts(query.getQuery(),query.getResultCount());
			
			for(l3s.facebook.search.response.Data data: resultlist.getData())
			{
				
				System.out.println(data.getObjectId()+data.getType());
				
				Photo photo= null;
				Statusupdate status= null;
				Sharedlink link = null;
				Number commentcount =  0;
				int likes=0;
				ResultItem resultItem = new ResultItem(getName());
				if(data.getType().equalsIgnoreCase("photo") && data.getObjectId()!=null && query.getContentTypes().contains("image"))
				{
					resultItem.setType(Query.CT_IMAGE);
					photo=fbapi.getEntity(""+data.getObjectId(), Photo.class);
					/*if(photo.getComments()!=null)
						commentcount=photo.getComments().getData().size();
					if(photo.getLikes()!=null)
						likes=photo.getLikes().getContent().size();*/
				}
					
				else if(data.getType().equalsIgnoreCase("status") && data.getObjectId()!=null && query.getContentTypes().contains("text"))
				{
					resultItem.setType(Query.CT_TEXT);
					status=fbapi.getEntity(""+data.getObjectId(), Statusupdate.class);
					/*if(status.getComments()!=null)
						commentcount= status.getComments().getCount();
					if(status.getLikes()!=null)
						status.getLikes().getContent().size();*/
				}
				else if(data.getType().equalsIgnoreCase("link") && query.getContentTypes().contains("text"))
				{
					resultItem.setType(Query.CT_TEXT);
				}
				else
				{
					continue;
				}
				resultItem.setId(data.getId());
				resultItem.setTitle(data.getName());			
				resultItem.setDescription(data.getCaption()+"Message:"+data.getMessage());
				resultItem.setUrl(data.getLink());			
				resultItem.setDate(CoreUtils.formatDate(GetLocalDateFromUTCString(data.getUpdatedTime())));
				resultItem.setRank(1);			
				resultItem.setTotalResultCount(25);
				int num=0;
				
				resultItem.setCommentCount(num+commentcount.intValue());
				
				resultItem.setViewCount(likes);
				if(photo!=null)
				{
					Set<Thumbnail> thumbnails = new LinkedHashSet<Thumbnail>();
					for(Images image: photo.getImages())
					{
						
						thumbnails.add(new Thumbnail(image.getSource(), image.getWidth().intValue(), image.getHeight().intValue()));
						
						resultItem.setImageUrl(image.getSource()); // thumbnails are orderd by size. so the last assigned image is the largest
						
						if(image.getWidth().intValue() <= 100)
							resultItem.setEmbeddedSize1(CoreUtils.createImageCode(image.getSource(), image.getWidth().intValue(), image.getHeight().intValue(), 100, 100));
						
						else if(image.getWidth().intValue() <= 240)
							resultItem.setEmbeddedSize2("<img src=\""+ image.getSource() +"\" width=\""+ image.getWidth() +"\" height=\""+ image.getHeight() +"\" />");
						
						else if(image.getWidth().intValue() <= 500)
							resultItem.setEmbeddedSize3("<img src=\""+ image.getSource() +"\" width=\""+ image.getWidth() +"\" height=\""+ image.getHeight() +"\" />");
						
						else if(image.getWidth().intValue() > 500)
							resultItem.setEmbeddedSize4("<img src=\""+ image.getSource() +"\" width=\""+ image.getWidth() +"\" height=\""+ image.getHeight() +"\" />");
					}	

					resultItem.setThumbnails(thumbnails);
				}
				
				
				queryResult.addResultItem(resultItem);
			}
		}	
		
		/*	
		WebResource resource = createWebResource("vimeo.videos.search", getAuthCredentials(), null);
		
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
		}*/
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
		String path= "C:\\Users\\singh\\workspaceinterweb\\FacebookConnector\\FacebookIndex\\"+getUserId(authCredentials);

		

		Lucene groupsbase= new Lucene(false, new File(path+"\\groupbase"));
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
		IndexWriter locationwriter=null;
		try {
			videostaggedinwriter = videostaggedinbase.getWriter();
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
			
			 
		} catch (Exception e) {
			try {
				likeswriter.close();
				noteswriter.close();
				photoalbumwriter.close();
				eventswriter.close();
				groupswriter.close();
				taggedphotoswriter.close();
				feedwriter.close();
				videosuploadedwriter.close();
				videostaggedinwriter.close();
				userwriter.close();
				locationwriter.close();
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
		for(l3s.facebook.listresponse.friends.Data friend: friends.getData())
		{
			ContactFromSocialNetwork contact= new ContactFromSocialNetwork(friend.getName(), friend.getId().toString(), 1, "facebook");
			socialnetwork.getSocialnetwork().put(friend.getId().toString(), contact);
			
			new StoreFriendThread(userid+" storage thread", friend.getId().toString(), fbapi, writers).run();
		}
		try {
			likeswriter.forceMerge(10);
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
			locationwriter.close(true);
			
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return socialnetwork;
	}	
	
	public void storeFriend(String userid, AuthCredentials authCredentials, String belongsToSocialNetworkOf)
	{
		
		System.out.println("done"+userid);
		
	}

	private void storeUserPlaces(String userid, UserLocationObjects locations,
			IndexWriter writer, Facebook fbapi) {
		for(Objectwithlocation location : locations.getData())
		{
			Document doc= new Document();
			Field field= new Field("id", location.getId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
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

	private void storeUserProfile(String userid, User friend,  IndexWriter writer, Facebook fbapi)
			 {
		Document doc= new Document();
		Field field= new Field("id", userid, Field.Store.YES, Field.Index.NOT_ANALYZED);
		doc.add(field);
		field= new Field("name", friend.getName(), Field.Store.YES, Field.Index.ANALYZED);
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
			field= new Field("education"+i+" type", edu.getType(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("education"+i+" school", edu.getSchool().getName(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("education"+i+" concentration", edu.getConcentration().getName(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("education"+i+" year", edu.getYear().getName(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			
			Page schoolpage = fbapi.getEntity(edu.getSchool().getId().toString(), Page.class);
			
			field= new Field("education"+i+" school descreption", schoolpage.getDescription(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("education"+i+" school about", schoolpage.getAbout(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("education"+i+"school link", schoolpage.getLink(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("education"+i+"school id", schoolpage.getId().toString(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			i++;
		}
		i=1;
		for(Work work:friend.getWork())
		{
			field= new Field("work"+i+" location", work.getLocation().getName(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("work"+i+" position", work.getPosition().getName(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("work"+i+" employer", work.getEmployer().getName(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("work"+i+" start date", work.getStartDate(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("work"+i+" end date", work.getEndDate(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("work"+i+" from", work.getFrom().getName(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			int j=1;
			for(Projects project: work.getProjects())
			{
				field= new Field("work"+i+" project"+j+" name", project.getName(), Field.Store.YES, Field.Index.ANALYZED);
				doc.add(field);
				field= new Field("work"+i+" project"+j+" description", project.getDescription(), Field.Store.YES, Field.Index.ANALYZED);
				doc.add(field);
			}
			
			Page workpage = fbapi.getEntity(work.getEmployer().getId().toString(), Page.class);
			
			field= new Field("work"+i+" employer descreption", workpage.getDescription(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("work"+i+" employer about", workpage.getAbout(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("work"+i+"employer link", workpage.getLink(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			i++;
		}
		List<Languages> languages = friend.getLanguages();
		String languagelist=null;
			for(Languages lang: languages)
			{
				languagelist+=lang.getName()+" ";
			}
		field= new Field("languages", languagelist, Field.Store.YES, Field.Index.ANALYZED);
		doc.add(field);	
		
		String athletes=null;
		for(FavoriteAthletes athlete :friend.getFavoriteAthletes())
		{
			athletes+= athlete.getName()+ " ";
		}	
		String teams=null;
		for(FavoriteTeams team:friend.getFavoriteTeams())
		{
			teams+=team.getName()+" ";
		}
		String sports= null;
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
		try {
			writer.addDocument(doc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	
	try {
		writer.forceMerge(10);
		writer.close(true);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	
		
	}

	

	private void storeVideosTaggedIn(String userid, Videolist videostaggedin,
			IndexWriter writer,Facebook fbapi) {
		for(Video video:videostaggedin.getData())
		{
			Document doc= new Document();
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
			l3s.facebook.objects.video.Comments comments = video.getComments();
			String commentsinstring= "";
			if(comments!=null)
				commentsinstring = convertToCommaSeperatedList(comments);
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

	
		       
		    


	private void storeVideosUploaded(String userid, Videolist videosuploaded,
			IndexWriter writer,Facebook fbapi) {
		for(Video video:videosuploaded.getData())
		{
			Document doc= new Document();
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
			l3s.facebook.objects.video.Comments comments = video.getComments();
			String commentsinstring= "";
			if(comments!=null)
				commentsinstring = convertToCommaSeperatedList(comments);
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

	private void storeLikes(String userid, Likes likes, IndexWriter writer,Facebook fbapi) {
		
		Likes page0 = likes;
		
		while(page0.getPaging().getNext()!=null)
		{
			page0=fbapi.getNextPage(page0.getPaging().getNext(), Likes.class);
			likes.getData().addAll(page0.getData());
			
		}
			for(l3s.facebook.listresponse.likes.Data likedpage :likes.getData())
			{
				Document doc= new Document();
				Field field= new Field("id", likedpage.getId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
				doc.add(field);
				field= new Field("created time", likedpage.getCreatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
				doc.add(field);
				field= new Field("category", likedpage.getCategory(), Field.Store.YES, Field.Index.ANALYZED);
				doc.add(field);
				field= new Field("title", likedpage.getName(), Field.Store.YES, Field.Index.ANALYZED);
				doc.add(field);
				Page page = fbapi.getEntity(likedpage.getId().toString(), Page.class);
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

	private void storeMusic(String userid, Music music, IndexWriter writer,Facebook fbapi) {
		// TODO Auto-generated method stub
		
	}

	private void storeTaggedPhotosOfUser(String userid,
			Photos photostaggedin, IndexWriter writer,Facebook fbapi) {
		Photos page0 = photostaggedin;
		//severe performance degrade
		while(page0.getPaging().getNext()!=null)
		{
			page0=fbapi.getNextPage(page0.getPaging().getNext(), Photos.class);
			photostaggedin.getData().addAll(page0.getData());
			
		}
		for(Photo photo:photostaggedin.getData())
		{
			Document doc= new Document();
			Field field= new Field("id", photo.getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
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
			l3s.facebook.objects.photo.Comments comments = photo.getComments();
			String commentsinstring= "";
			if(comments!=null)
				commentsinstring = convertToCommaSeperatedList(comments);
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
			field= new Field("image h:"+photo.getImages().get(0).getHeight()+" w:"+photo.getImages().get(0).getWidth()+" source", photo.getImages().get(0).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("image h:"+photo.getImages().get(1).getHeight()+" w:"+photo.getImages().get(1).getWidth()+" source", photo.getImages().get(1).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("image h:"+photo.getImages().get(2).getHeight()+" w:"+photo.getImages().get(2).getWidth()+" source", photo.getImages().get(2).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("image h:"+photo.getImages().get(3).getHeight()+" w:"+photo.getImages().get(3).getWidth()+" source", photo.getImages().get(3).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("image h:"+photo.getImages().get(4).getHeight()+" w:"+photo.getImages().get(4).getWidth()+" source", photo.getImages().get(4).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("image h:"+photo.getImages().get(5).getHeight()+" w:"+photo.getImages().get(5).getWidth()+" source", photo.getImages().get(5).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("image h:"+photo.getImages().get(6).getHeight()+" w:"+photo.getImages().get(6).getWidth()+" source", photo.getImages().get(6).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("image h:"+photo.getImages().get(7).getHeight()+" w:"+photo.getImages().get(7).getWidth()+" source", photo.getImages().get(7).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
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

	private String convertToCommaSeperatedList(l3s.facebook.objects.photo.Comments comments) {
		StringBuilder nameBuilder = new StringBuilder();

	   for(l3s.facebook.objects.photo.Data d: comments.getData())
	   {
		   nameBuilder.append("'").append(d.getMessage()).append("',");
	   }
	       
	    

	    nameBuilder.deleteCharAt(nameBuilder.length() - 1);

	    return nameBuilder.toString();
		
	}

	

	private void storeGroups(String userid, Groups groups, IndexWriter writer,Facebook fbapi) {
		Groups page0 = groups;
		//severe performance degrade
		while(page0.getPaging()!=null)
		{
			page0=fbapi.getNextPage(page0.getPaging().getNext(), Groups.class);
			groups.getData().addAll(page0.getData());
		}
		for(l3s.facebook.listresponse.usergroups.Data group :groups.getData())
		{
			Document doc= new Document();
			Field field= new Field("id", group.getId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("admin", group.isAdministrator().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
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
			field= new Field("owner", usergroup.getOwner().getName(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("updated time", usergroup.getUpdatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
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

	private void storeEvents(String userid, Events events, IndexWriter writer,Facebook fbapi) {
		Events page0 = events;
		//severe performance degrade
		while(page0.getPaging().getNext()!=null)
		{
			page0=fbapi.getNextPage(page0.getPaging().getNext(), Events.class);
			events.getData().addAll(page0.getData());
		}
		for( l3s.facebook.listresponse.userevents.Data event :events.getData())
		{
			Document doc= new Document();
			Field field= new Field("id", event.getId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("end time", event.getEndTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("start time", event.getStartTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("location", event.getLocation(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("title", event.getName(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("status", event.getRsvpStatus(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("timezone", event.getTimezone(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			
			Event eventpage = fbapi.getEntity(event.getId().toString(), Event.class);
			
			
			field= new Field("description", eventpage.getDescription().toString(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("privacy", eventpage.getPrivacy(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("owner", eventpage.getOwner().getName(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("updated time", eventpage.getUpdatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
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

	private void storePhotoAlbums(String userid, Photoalbums photoalbums,
			IndexWriter writer,Facebook fbapi) {
		
		if(photoalbums.getPaging()!=null)
		{
			Photoalbums page0 = photoalbums;
			//severe performance degrade
			while(page0.getPaging().getNext()!=null)
			{
				page0=fbapi.getNextPage(page0.getPaging().getNext(), Photoalbums.class);
				photoalbums.getData().addAll(page0.getData());
			}
		}
		
		//severe performance degrade
		for(Photoalbum album: photoalbums.getData())
		{
			Document doc= new Document();
			Field field= new Field("id", album.getId(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("updated time", album.getUpdatedTime(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("created time", album.getCreatedTime(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("link", album.getLink(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("title", album.getName(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("type", album.getType(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("Cover Photo id", album.getCoverPhoto().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("from id", album.getFrom().getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("from name", album.getFrom().getName(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("number of photos", album.getCount().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("likes", ""+album.getLikes().getData().size(), Field.Store.YES, Field.Index.NOT_ANALYZED);
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

	private void storeNotes(String userid, Notes notes, IndexWriter writer, Facebook fbapi) {
		for(Note n: notes.getData())
		{
			Document doc= new Document();
			Field field= new Field("id", n.getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
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
