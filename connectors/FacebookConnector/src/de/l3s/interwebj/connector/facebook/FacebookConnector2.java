package de.l3s.interwebj.connector.facebook;

import static de.l3s.interwebj.util.Assertions.notNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import l3s.facebook.listresponse.links.Links;
import l3s.facebook.listresponse.links.SharedLinks;
import l3s.facebook.listresponse.movies.Movies;
import l3s.facebook.listresponse.music.Music;
import l3s.facebook.listresponse.notes.Notes;
import l3s.facebook.listresponse.photos.Photos;
import l3s.facebook.listresponse.profilefeed.Feedobject;
import l3s.facebook.listresponse.profilefeed.ProfileFeed;
import l3s.facebook.listresponse.status.Statuses;

import l3s.facebook.listresponse.userevents.Events;
import l3s.facebook.listresponse.usergroups.Groups;
import l3s.facebook.listresponse.userlocations.Objectwithlocation;
import l3s.facebook.listresponse.userlocations.UserLocationObjects;
import l3s.facebook.listresponse.userphotoalbums.Photoalbums;
import l3s.facebook.listresponse.videosuploadedandtagged.Videolist;
import l3s.facebook.object.link.Sharedlink;
import l3s.facebook.objects.event.Event;
import l3s.facebook.objects.group.Group;
import l3s.facebook.objects.note.Note;
import l3s.facebook.objects.page.Page;
import l3s.facebook.objects.photo.Comments;
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

import org.apache.commons.lang.NotImplementedException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
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
import de.l3s.interwebj.db.Database;
import de.l3s.interwebj.query.ContactFromSocialNetwork;
import de.l3s.interwebj.query.Query;
import de.l3s.interwebj.query.Query.SortOrder;
import de.l3s.interwebj.query.QueryResult;
import de.l3s.interwebj.query.ResultItem;
import de.l3s.interwebj.query.Thumbnail;
import de.l3s.interwebj.query.UserSocialNetworkResult;
import de.l3s.interwebj.socialsearch.SocialSearchQuery;
import de.l3s.interwebj.socialsearch.SocialSearchResult;
import de.l3s.interwebj.socialsearch.SocialSearchResultItem;
import de.l3s.interwebj.util.CoreUtils;
import facebook.api.Facebook;
public class FacebookConnector2 extends AbstractServiceConnector
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
			"friends_actions.music,friends_actions.news,friends_actions.video,friends_games_activity,publish_actions,read_stream";
	
	 

	
	
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
		return socialSearch(query, authCredentials);

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
		
	}
	

	@Override
	public void revokeAuthentication()
	    throws InterWebException
	{
		Facebook fb = new Facebook(getAuthCredentials().getKey());
		fb.revokeAccessToken(getUserId(getAuthCredentials()));
	}

	@Override
	public UserSocialNetworkResult getUserSocialNetwork(String userid,
			AuthCredentials authCredentials) throws InterWebException {
		Facebook fb = new Facebook(authCredentials.getKey());
		Friends friends = fb.getFriendsof("me");
		
		Facebook fbapi= new Facebook(authCredentials.getKey());
		User user = fbapi.getEntity("me", User.class);
		System.out.println("user:"+userid+"->"+user.getId());
		//String path= "C:\\Users\\singh\\FacebookIndex\\"+userid+"testing";
		

		UserSocialNetworkResult socialnetwork = new UserSocialNetworkResult(userid);
		int i=0;
		int j=0;
		String values= new String();
		for(l3s.facebook.listresponse.friends.Data friend: friends.getData())
		{
			i++;
			
			ContactFromSocialNetwork contact= new ContactFromSocialNetwork(friend.getName(), friend.getId().toString(), 1, "facebook");
			socialnetwork.getSocialnetwork().put(friend.getId().toString(), contact);
			System.out.println("storing:"+contact.getUsername()+"     "+i);
			try {
			
			
		    	System.out.println("storing " +
		    			"................................................................................."+friend.getName()+"   "+friend.getId());
		    	values+="("+friend.getId()+",\'"+friend.getName()+"\',\'"+fbapi.getAccesstoken()+"\',\'"+user.getId()+"\',0,0"+"),";
				//new StoreFriendThread(userid+" storage thread", friend.getId().toString(), fbapi, writers).run();
//		    	System.out.println("storeEvents");
//		    	storeEvents(friend.getId().toString(),friend.getName(), writer, fbapi);
//		    	System.out.println("storeGroups");
//		    	storeGroups(friend.getId().toString(),friend.getName(), writer, fbapi);
//		    	System.out.println("storeLikes");
//		    	storeLikes(friend.getId().toString(),friend.getName(), writer, fbapi);
//		    	System.out.println("storeNotes");
//		    	storeNotes(friend.getId().toString(),friend.getName(), writer, fbapi);
//		    	System.out.println("storePhotoAlbums");
//		    	storePhotoAlbums(friend.getId().toString(),friend.getName(), writer, fbapi);
//		    	System.out.println("storeTaggedPhotosOfUser");
//		    	storeTaggedPhotosOfUser(friend.getId().toString(),friend.getName(), writer, fbapi);
//		    	System.out.println("storeUserPlaces");
//		    	storeUserPlaces(friend.getId().toString(),friend.getName(), writer, fbapi);
//		    	System.out.println("storeUserProfile");
//		    	storeUserProfile(friend.getId().toString(),friend.getName(), writer, fbapi);
//		    	System.out.println("storeVideosTaggedIn");
//		    	storeVideosTaggedIn(friend.getId().toString(),friend.getName(), writer, fbapi);
//		    	System.out.println("storeVideosUploaded");
//		    	storeVideosUploaded(friend.getId().toString(),friend.getName(), writer, fbapi);
//		    	System.out.println("storeStatusUpdates");
//		    	storeStatusUpdates(friend.getId().toString(),friend.getName(), writer, fbapi);
//		    	System.out.println("storeLinks");
//		    	storeLinks(friend.getId().toString(),friend.getName(), writer, fbapi);
			} catch (Exception e) {
				e.printStackTrace();
				j++;
				if(j<25)
				{
					try {
						wait(100000);
						
						continue;
						
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
				break;
				
			}
			
			
			
		
		}
		Database database = Environment.getInstance().getDatabase();
		if (values.length() > 0 && values.charAt(values.length()-1)==',') 
			values = values.substring(0, values.length()-1);
		database.saveFriendsOfUser(values.trim());
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
    			String content= new String();
    			Field field= new Field("id", location.getId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("user name", name, Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("type", location.getType(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("link", "http://www.facebook.com/"+location.getId(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("title", location.getPlace().getName(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("city", location.getPlace().getLocation().getCity(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("country", location.getPlace().getLocation().getCountry(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			content+=" "+location.getPlace().getName()+" "+location.getPlace().getLocation().getCity()+" "+location.getPlace().getLocation().getCountry();
    			field= new Field("content", content, Field.Store.YES, Field.Index.ANALYZED);
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
		if(friend!=null)
		{
			System.out.println(friend.getName()+" personality");
	    	Document doc= new Document();
	    	String content= null;
			Field field= new Field("user", userid, Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			String contentpro= new String();
			field= new Field("id", friend.getId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
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
			contentpro+= friend.getPolitical()+" "+friend.getName();
			
				
			int i=1;
			for(Education edu: friend.getEducation())
			{
				Document docedu= new Document();
				content= new String();
				
				field= new Field("type", edu.getType(), Field.Store.YES, Field.Index.ANALYZED);
				docedu.add(field);
				field= new Field("user name", friend.getName(), Field.Store.YES, Field.Index.ANALYZED);
				docedu.add(field);
				field= new Field("user", ""+friend.getId(), Field.Store.YES, Field.Index.ANALYZED);
				docedu.add(field);
				field= new Field("title", edu.getSchool().getName(), Field.Store.YES, Field.Index.ANALYZED);
				docedu.add(field);
				field= new Field("concentration", edu.getConcentration().getName(), Field.Store.YES, Field.Index.ANALYZED);
				docedu.add(field);
				field= new Field("year", edu.getYear().getName(), Field.Store.YES, Field.Index.ANALYZED);
				docedu.add(field);
				
				Page schoolpage = fbapi.getEntity(edu.getSchool().getId().toString(), Page.class);
				if(schoolpage!=null)
				{
					content+=" "+edu.getSchool().getName()+" "+edu.getConcentration().getName()+" "+schoolpage.getDescription()+" "+schoolpage.getAbout();
					field= new Field("content", schoolpage.getDescription(), Field.Store.YES, Field.Index.ANALYZED);
					docedu.add(field);
					
					field= new Field("description", schoolpage.getDescription(), Field.Store.YES, Field.Index.ANALYZED);
					docedu.add(field);
					field= new Field("about", schoolpage.getAbout(), Field.Store.YES, Field.Index.ANALYZED);
					docedu.add(field);
					field= new Field("link", schoolpage.getLink(), Field.Store.YES, Field.Index.ANALYZED);
					docedu.add(field);
					field= new Field("id", schoolpage.getId().toString(), Field.Store.YES, Field.Index.ANALYZED);
					docedu.add(field);
					i++;
					
				}
				
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
				content= new String();
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
				content+= work.getLocation().getName()+" "+work.getPosition().getName()+" "+work.getEmployer().getName();
				int j=1;
				for(Projects project: work.getProjects())
				{
					field= new Field("project"+j+" name", project.getName(), Field.Store.YES, Field.Index.ANALYZED);
					workdoc.add(field);
					field= new Field("project"+j+" description", project.getDescription(), Field.Store.YES, Field.Index.ANALYZED);
					workdoc.add(field);
					content+=" "+project.getName()+" "+project.getDescription();
				}
				
				Page workpage = fbapi.getEntity(work.getEmployer().getId().toString(), Page.class);
				
				field= new Field("description", workpage.getDescription(), Field.Store.YES, Field.Index.ANALYZED);
				workdoc.add(field);
				field= new Field("about", workpage.getAbout(), Field.Store.YES, Field.Index.ANALYZED);
				workdoc.add(field);
				content+=" "+workpage.getDescription()+" "+workpage.getAbout();
				field= new Field("link", workpage.getLink(), Field.Store.YES, Field.Index.ANALYZED);
				workdoc.add(field);
				field= new Field("document type", "work", Field.Store.YES, Field.Index.ANALYZED);
				workdoc.add(field);
				field= new Field("user name", name, Field.Store.YES, Field.Index.ANALYZED);
				workdoc.add(field);
				field= new Field("content", content, Field.Store.YES, Field.Index.ANALYZED);
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
			contentpro+=" "+languagelist;
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
			contentpro+=" "+sports;
			field= new Field("favourite athletes", athletes, Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			contentpro+=" "+athletes;
			field= new Field("favourite teams", teams, Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			contentpro+=" "+teams;
			field= new Field("bio", friend.getBio(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			contentpro+=" "+friend.getBio();
			field= new Field("website", friend.getWebsite(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("hometown", friend.getHometown().getName(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			contentpro+=" "+friend.getHometown().getName();
			field= new Field("current location", friend.getLocation().getName(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			contentpro+=" "+friend.getLocation().getName();
			field= new Field("timezone", friend.getTimezone().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("quotes", friend.getQuotes(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			contentpro+=" "+friend.getQuotes();
			field= new Field("document type", "personality", Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("content", contentpro, Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			
			
			try {
				writer.addDocument(doc);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		
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
    			String content= new String();
    			int likes=0;
				if(video.getLikes().getData().size() >0)
				{
					  l3s.facebook.objects.video.Likes  pagelikes = video.getLikes();
					  l3s.facebook.objects.video.Likes pagelikesnext = pagelikes;
					while(pagelikesnext.getPaging()!=null)
					{
						if(pagelikesnext.getPaging().getNext()==null)
							break;
						pagelikesnext = fbapi.getNextPage(pagelikesnext.getPaging().getNext(), l3s.facebook.objects.video.Likes.class );
						pagelikes.getData().addAll(pagelikesnext.getData());
						
					}
					likes=pagelikes.getData().size();
				}
				int numcomments=0;
				if(video.getComments().getData().size() >0)
				{
					 l3s.facebook.objects.video.Comments pagecomments = video.getComments();
					 l3s.facebook.objects.video.Comments pagecommentsnext= pagecomments;
					while(pagecommentsnext.getPaging()!=null)
					{
						if(pagecommentsnext.getPaging().getNext()==null)
							break;
						 pagecommentsnext = fbapi.getNextPage(pagecommentsnext.getPaging().getNext(),  l3s.facebook.objects.video.Comments.class );
						pagecomments.getData().addAll(pagecommentsnext.getData());
						
					}
					numcomments=pagecomments.getData().size();
				}
				System.out.println("comments: "+numcomments+" likes: "+likes);
				
				
				
    			Field field= new Field("id", video.getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("user name", name, Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("link", "http://www.facebook.com/"+video.getId(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
				field= new Field("number of comments", ""+numcomments, Field.Store.YES, Field.Index.NOT_ANALYZED);
				doc.add(field);
				field= new Field("number of likes", ""+likes, Field.Store.YES, Field.Index.NOT_ANALYZED);
				doc.add(field);
    			field= new Field("title", video.getName(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			content+=video.getName();
    			field= new Field("embed html", video.getEmbedHtml(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("thumbnail", video.getPicture(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("icon", video.getIcon(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("source", video.getSource(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			content+=""+video.getSource();
    			field= new Field("description", video.getDescription(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			content+=""+video.getDescription();
    			l3s.facebook.objects.video.Comments comments = video.getComments();
    			String commentsinstring= " ";
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
    			content+=" "+commentsinstring;	
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
    			field= new Field("content", content, Field.Store.YES, Field.Index.ANALYZED);
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
	
	
	
	
	private void storeStatusUpdates(String userid, 
			String name, IndexWriter writer,Facebook fbapi) {
		Statuses statuslistholder=fbapi.getStatusUpdateList(userid);
		System.out.println(userid+statuslistholder.getData().size());
		if(statuslistholder.getData().size()>0)
    	{
			Statuses page0 = statuslistholder;
        	while( page0.getPaging()!=null)
    		{
        		if(page0.getData().size()>300)
        			break;
        		if(page0.getPaging().getNext()==null)
    				break;
    			page0=fbapi.getNextPage(page0.getPaging().getNext(), Statuses.class);
    			statuslistholder.getData().addAll(page0.getData());
    			
    		}
    		for(Statusupdate status:statuslistholder.getData())
    		{
    			if (status.getId()==null) {
					continue;
				}
        		System.out.println(status.getMessage());
    			Document doc= new Document();
    			int likes=0;
    			if(status.getLikes().getContent().size() >0)
    			{
    				 l3s.facebook.objects.status.Likes pagelikes = status.getLikes();
    				 l3s.facebook.objects.status.Likes pagelikesnew = pagelikes;
    				while(pagelikesnew.getPaging()!=null)
    				{
    					if(pagelikesnew.getPaging().getNext()==null)
    						break;
    					 pagelikesnew = fbapi.getNextPage(pagelikesnew.getPaging().getNext(), l3s.facebook.objects.status.Likes.class );
    					pagelikes.getContent().addAll(pagelikesnew.getContent());
    					
    				}
    				likes=pagelikes.getContent().size();
    			}
    			int comments=0;
    			if(status.getComments().getData().size() >0)
    			{
    				  l3s.facebook.objects.status.Comments pagecomments = status.getComments();
    				  l3s.facebook.objects.status.Comments pagecommentsnew =  pagecomments;
    				while(pagecommentsnew.getPaging()!=null)
    				{
    					if(pagecommentsnew.getPaging().getNext()==null)
    						break;
    					pagecommentsnew = fbapi.getNextPage(pagecommentsnew.getPaging().getNext(),  l3s.facebook.objects.status.Comments.class );
    					pagecomments.getData().addAll(pagecommentsnew.getData());
    					
    				}
    				comments=pagecomments.getData().size();
    			}
    			
    			System.out.println("comments: "+comments+" likes: "+likes);
    			
    			Field field= new Field("id", status.getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("user name", name, Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("number of comments", ""+comments, Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("number of likes", ""+likes, Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("title",  " ", Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("link", "http://www.facebook.com/"+status.getId(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			
    			field= new Field("description", status.getMessage(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			String content=new String();
    			content+=status.getMessage();
    			if(status.getPlace()!=null)
    			{
    				field= new Field("location", status.getPlace().getName(), Field.Store.YES, Field.Index.ANALYZED);
        			doc.add(field);
        			content+=" "+status.getPlace().getName();
        			if(status.getPlace().getLocation()!=null)
        			{
        				field= new Field("city", status.getPlace().getLocation().getCity(), Field.Store.YES, Field.Index.ANALYZED);
            			doc.add(field);
            			content+=" "+status.getPlace().getLocation().getCity();
            			field= new Field("country", status.getPlace().getLocation().getCountry(), Field.Store.YES, Field.Index.ANALYZED);
            			doc.add(field);
            			content+=" "+status.getPlace().getLocation().getCountry();
        			}
    				
        			
    			}
    			
				field= new Field("content", content, Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			
    			field= new Field("updated time", status.getUpdatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			
    			field= new Field("from name", status.getFrom().getName(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("from id", status.getFrom().getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("user", userid, Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			
    			field= new Field("document type", "status", Field.Store.YES, Field.Index.ANALYZED);
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
			   if(d.getMessage()==null)
				   continue;
			   if(!d.getFrom().getName().equalsIgnoreCase("unknown"))
			   nameBuilder.append("<b>").append(d.getFrom().getName()).append("</b> :").append("'").append(d.getMessage()).append("'").append(" &lt;br");
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
    			String content=new String();
    			int likes=0;
    			if(video.getLikes().getData().size() >0)
				{
					  l3s.facebook.objects.video.Likes  pagelikes = video.getLikes();
					  l3s.facebook.objects.video.Likes pagelikesnext = pagelikes;
					while(pagelikesnext.getPaging()!=null)
					{
						if(pagelikesnext.getPaging().getNext()==null)
							break;
						pagelikesnext = fbapi.getNextPage(pagelikesnext.getPaging().getNext(), l3s.facebook.objects.video.Likes.class );
						pagelikes.getData().addAll(pagelikesnext.getData());
						
					}
					likes=pagelikes.getData().size();
				}
				int numcomments=0;
				if(video.getComments().getData().size() >0)
				{
					 l3s.facebook.objects.video.Comments pagecomments = video.getComments();
					 l3s.facebook.objects.video.Comments pagecommentsnext= pagecomments;
					while(pagecommentsnext.getPaging()!=null)
					{
						if(pagecommentsnext.getPaging().getNext()==null)
							break;
						 pagecommentsnext = fbapi.getNextPage(pagecommentsnext.getPaging().getNext(),  l3s.facebook.objects.video.Comments.class );
						pagecomments.getData().addAll(pagecommentsnext.getData());
						
					}
					numcomments=pagecomments.getData().size();
				}
				System.out.println("comments: "+numcomments+" likes: "+likes);
				
    			Field field= new Field("id", video.getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("link", "http://www.facebook.com/"+video.getId(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("number of comments", ""+numcomments, Field.Store.YES, Field.Index.NOT_ANALYZED);
				doc.add(field);
				field= new Field("number of likes", ""+likes, Field.Store.YES, Field.Index.NOT_ANALYZED);
				doc.add(field);
    			field= new Field("title", video.getName(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			content+=video.getName();
    			field= new Field("embed html", video.getEmbedHtml(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("thumbnail", video.getPicture(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("icon", video.getIcon(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("source", video.getSource(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			content+=" "+video.getSource()+" "+video.getDescription();
    			field= new Field("description", video.getDescription(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("user name", name, Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			l3s.facebook.objects.video.Comments comments = video.getComments();
    			String commentsinstring= " ";
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
    			content+=" "+commentsinstring;
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
    			field= new Field("content", content, Field.Store.YES, Field.Index.ANALYZED);
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
    				String content= new String();
    				Field field= new Field("id", likedpage.getId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("created time", likedpage.getCreatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("category", likedpage.getCategory(), Field.Store.YES, Field.Index.ANALYZED);
    				doc.add(field);
    				field= new Field("title", likedpage.getName(), Field.Store.YES, Field.Index.ANALYZED);
    				doc.add(field);
    				content+=likedpage.getName();
    				field= new Field("user name", name, Field.Store.YES, Field.Index.ANALYZED);
        			doc.add(field);
        			if(likedpage.getId()!=BigInteger.ZERO)
        			{
        				Page page = fbapi.getEntity(likedpage.getId().toString(), Page.class);
        				if(page!=null)
        				{
        					field= new Field("about", page.getAbout(), Field.Store.YES, Field.Index.ANALYZED);
            				doc.add(field);
            				field= new Field("description", page.getDescription(), Field.Store.YES, Field.Index.ANALYZED);
            				doc.add(field);
            				content+=" "+page.getAbout()+" "+page.getDescription();
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
            				field= new Field("content", content, Field.Store.YES, Field.Index.ANALYZED);
            				doc.add(field);
        				}
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
			if(photostaggedin.getData().size()>200)
    				break;
    			
    		}
    		for(Photo photo:photostaggedin.getData())
    		{
    			
    			if(photo.getName().equalsIgnoreCase("unknown") && photo.getPlace().getName().equalsIgnoreCase("unknown") && photo.getPlace().getLocation().getCity().equalsIgnoreCase("unknown") && photo.getPlace().getLocation().getCountry().equalsIgnoreCase("unknown") && photo.getComments()!=null)
    			{
    				continue;
    				
    			}
    			else
    			{
    				Document doc= new Document();
    				String content= new String();
    				if(photo.getId().equalsIgnoreCase("0"))
    					continue;
    				int likes=0;
    				if(photo.getLikes().getData().size() >0)
    				{
    					  l3s.facebook.objects.photo.Likes pagelikes = photo.getLikes();
    					  l3s.facebook.objects.photo.Likes pagelikesnext = pagelikes;
    					while(pagelikesnext.getPaging()!=null)
    					{
    						if(pagelikesnext.getPaging().getNext()==null)
    							break;
    						pagelikesnext = fbapi.getNextPage(pagelikesnext.getPaging().getNext(), l3s.facebook.objects.photo.Likes.class );
    						pagelikes.getData().addAll(pagelikesnext.getData());
    						
    					}
    					likes=pagelikes.getData().size();
    				}
    				int numcomments=0;
    				
    				
    				
    				
    				
    				Field field= new Field("id", photo.getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				
    				field= new Field("number of likes", ""+likes, Field.Store.YES, Field.Index.NOT_ANALYZED);
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
    				String commentsinstring= " ";
    				
    				if(comments!=null && comments.getData().size()>0)
    				{
    					Comments compage0= comments;
    					while(compage0.getPaging()!=null)
    					{
    						if(compage0.getPaging().getNext()==null)
    							break;
    						try{
    							compage0=fbapi.getNextPage(compage0.getPaging().getNext(), Comments.class);
        						comments.getData().addAll(compage0.getData());
    						}
    						catch (Exception e) {
								// TODO: handle exception
							}
    						
    						
    					}
    					commentsinstring = convertToCommaSeperatedList(comments);
    				}
    				numcomments=comments.getData().size();
    				System.out.println("comments: "+numcomments+" likes: "+likes);
    				content+=comments+" "+ photo.getName();
    				field= new Field("comments",commentsinstring , Field.Store.YES, Field.Index.ANALYZED);
    				doc.add(field);
    				field= new Field("number of comments", ""+numcomments, Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("content",content , Field.Store.YES, Field.Index.ANALYZED);
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
		   if(!d.getFrom().getName().equalsIgnoreCase("unknown"))
		   nameBuilder.append("<b>"+d.getFrom().getName()).append("</b> : ").append("'").append(d.getMessage()).append("'").append("    &lt;br");
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
    			String content= new String();
    			Field field= new Field("id", group.getId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("user name", name, Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("title", group.getName(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			content+=group.getName();
    			if(group.getId()!=BigInteger.ZERO)
    			{
    				Group usergroup = fbapi.getEntity(group.getId().toString(), Group.class);
        			if(usergroup!=null)
        			{	
        				
        				content+=" "+usergroup.getDescription();
            			field= new Field("link", "www.facebook.com/"+group.getId().toString(), Field.Store.YES, Field.Index.ANALYZED);
            			doc.add(field);
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
            			
        			}
        			
    			}
    			
    			field= new Field("content", content, Field.Store.YES, Field.Index.ANALYZED);
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
    			String content= new String();
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
    			content+=event.getName()+" . "+event.getLocation()+" . ";
    			
    			field= new Field("status", event.getRsvpStatus(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("timezone", event.getTimezone(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("document type", "event", Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			
    			if(event.getId()!=BigInteger.ZERO)
    			{
    				Event eventpage = fbapi.getEntity(event.getId().toString(), Event.class);
        			
        			if(eventpage!=null)
        			{
        				field= new Field("description", new String(eventpage.getDescription()), Field.Store.YES, Field.Index.ANALYZED);
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
            			
            			content+=" "+eventpage.getDescription();
        			}
    			}
    			
    			field= new Field("content", content, Field.Store.YES, Field.Index.ANALYZED);
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
			int likes=0;
			if(album.getLikes().getData().size() >0)
			{
				 l3s.facebook.objects.photoalbum.Likes pagelikes = album.getLikes();
				 l3s.facebook.objects.photoalbum.Likes pagelikesnew= pagelikes;
				while(pagelikesnew.getPaging()!=null)
				{
					if(pagelikesnew.getPaging().getNext()==null)
						break;
					pagelikesnew = fbapi.getNextPage(pagelikesnew.getPaging().getNext(), l3s.facebook.objects.photoalbum.Likes.class );
					
					pagelikes.getData().addAll(pagelikesnew.getData());
					
				}
				likes=pagelikes.getData().size();
			}
			int comments=0;
			if(album.getComments().getData().size() >0)
			{
				 l3s.facebook.objects.photoalbum.Comments pagecomments = album.getComments();
				 l3s.facebook.objects.photoalbum.Comments pagecommentsnew =pagecomments;
				while(pagecommentsnew.getPaging()!=null)
				{
					if(pagecommentsnew.getPaging().getNext()==null)
						break;
					pagecommentsnew = fbapi.getNextPage(pagecommentsnew.getPaging().getNext(),  l3s.facebook.objects.photoalbum.Comments.class );
					pagecomments.getData().addAll(pagecommentsnew.getData());
					
				}
				comments=pagecomments.getData().size();
			}
			System.out.println("comments: "+comments+" likes: "+likes);
			Field field= new Field("id", album.getId(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("number of comments", ""+comments, Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("number of likes", ""+likes, Field.Store.YES, Field.Index.NOT_ANALYZED);
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
			
			field= new Field("content", album.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
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
			
			Photo photo=null;
			try {
				
				photo= fbapi.getEntity(album.getCoverPhoto().toString(), Photo.class);
			} catch (Exception e) {
				System.out.println("no cover photo");
				continue;
			}
			
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
			
			
			try {
				writer.addDocument(doc);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		
		
		
		}
		
		
	}
	
	
	private void storeLinks(String userid, 
			String name, IndexWriter writer,Facebook fbapi) {
		SharedLinks links=fbapi.getLinksSharedBy(userid);
		System.out.println(userid+links.getLinks().getData().size());
		if(links.getLinks().getData().size()>0)
		{
			Links page0 = links.getLinks();
			//severe performance degrade
			while(page0.getPaging()!=null)
			{
				if(page0.getPaging().getNext()==null)
					break;
				page0=fbapi.getNextPage(page0.getPaging().getNext(), Links.class);
				links.getLinks().getData().addAll(page0.getData());
			}
		
		
		//severe performance degrade
		for(Sharedlink link: links.getLinks().getData())
		{
			if(link.getId()==null)
				continue;
			Document doc= new Document();
			int likes=0;
			String commentscontent= new String();
			int comments=0;
			if(link.getComments().getData().size() >0)
			{
				  l3s.facebook.object.link.Comments pagecomments = link.getComments();
				  l3s.facebook.object.link.Comments pagecommentsnew = pagecomments;
				while(pagecommentsnew.getPaging()!=null)
				{
					if(pagecommentsnew.getPaging().getNext()==null)
						break;
					pagecommentsnew = fbapi.getNextPage(pagecommentsnew.getPaging().getNext(),  l3s.facebook.object.link.Comments.class );
					pagecomments.getData().addAll(pagecommentsnew.getData());
					
				}
				comments=pagecomments.getData().size();
				for(l3s.facebook.object.link.Data d: pagecomments.getData())
				{
					commentscontent+=" "+d.getMessage()+" ";
				}
			}
			System.out.println("comments: "+comments+" likes: "+likes);
			Field field= new Field("id", link.getId(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("number of comments", ""+comments, Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("comments", commentscontent, Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("number of likes", ""+likes, Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("user name", name, Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			
			
			field= new Field("created time", link.getCreatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("link", link.getLink(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("title", link.getName(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			field= new Field("message", link.getMessage(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			
			
			field= new Field("content", link.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("from id", link.getFrom().getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			doc.add(field);
			field= new Field("from name", link.getFrom().getName(), Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			
			
			
			field= new Field("user", userid, Field.Store.YES, Field.Index.ANALYZED);
			doc.add(field);
			
			field= new Field("document type", "shared links", Field.Store.YES, Field.Index.ANALYZED);
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
    			String content= new String();
    			Field field= new Field("id", n.getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("user name", name, Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("url", "http://www.facebook.com/"+n.getId(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("message", n.getMessage(), Field.Store.YES, Field.Index.ANALYZED);
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
    			content+=n.getSubject()+" "+n.getMessage();
    			field= new Field("content", content, Field.Store.YES, Field.Index.ANALYZED);
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
		String path= "/home/singh/learnweb/FacebookIndex/crawltestfinalwithlinks2";
		//String path= "C:\\Users\\singh\\FacebookIndex\\"+"testnew";
		
		
		Lucene base= new Lucene(false, new File(path));
		String querystr = query.getQuery()+" AND user:"+userid;
		String[] fields= { "gender","political","concentration","document type","descreption","description","title",
				"project1 name", "project1 description", "project2 name", "project2 description", "project3 name", "project3 description"
				,"languages", "sports","favourite athletes","favourite teams","bio","website","hometown","current location","quotes","location","city","country","comments","from name",
				"message","caption","type","category","about","website","source","concentration"};
		
		ArrayList<Document> hits = base.searchIndex(querystr, fields, query.getResultCount()*5);
		
		//List<Document> hitspage = hits.subList((query.getPage()-1)*query.getResultCount(), query.getPage()*query.getResultCount());
		for(Document doc: hits)
		{
			if(doc.get("user")==null)
				continue;
			ResultItem result= new ResultItem(getName());
			result.setServiceName("facebook");
			result.setType(Query.CT_TEXT);
			if(doc.get("user id")!=null)
			result.setId(doc.get("user id"));
			else
				result.setId(doc.get("user"));
			String title=doc.get("user name")+"'s "+ doc.get("document type");	
			if(doc.get("title")!=null)
			{if(!doc.get("title").equalsIgnoreCase("unknown") && !doc.get("title").equalsIgnoreCase("null"))
			{title+= ": "+ doc.get("title");}}
			
			result.setTitle(title);
			String desc= new String();
			if(doc.get("message")!=null&&!doc.get("message").equalsIgnoreCase("unknown"))
				desc+=doc.get("message")+". ";
			
			if(doc.get("category")!=null && !doc.get("category").equalsIgnoreCase("unknown"))
				desc+="Category: "+doc.get("category")+". ";
			

			if(doc.get("about")!=null&& !doc.get("about").equalsIgnoreCase("unknown"))
				desc+="About: "+doc.get("about")+". ";
			
			if(doc.get("description")!=null&&!doc.get("description").equalsIgnoreCase("unknown"))
				desc+=doc.get("description")+". ";
			result.setTags(doc.get("user"));
			
			//result.setDescription(doc.get("description")+" About:"+doc.get("about")+". Category:"+doc.get("catefory")+ ". Website:"+ doc.get("website"));
			result.setUrl(doc.get("link"));
			if(doc.get("created time")!=null)
			{
				Date d = GetLocalDateStringFromUTCString(doc.get("created time"));
				String date = timeAgoInWords(d);
				result.setDate(date);
			}
			if(doc.get("document type").equalsIgnoreCase("status"))
			{
				Date d = GetLocalDateStringFromUTCString(doc.get("updated time"));
				String date = timeAgoInWords(d);
				result.setDate(date);
				desc+="\r\n";
				if(doc.get("location")!=null && !doc.get("location").equalsIgnoreCase("unknown"))
					desc+=" Location: "+doc.get("location");
				
				if(doc.get("city")!=null && !doc.get("city").equalsIgnoreCase("unknown"))
					desc+=". City: "+doc.get("city");
				
				if(doc.get("country")!=null && !doc.get("country").equalsIgnoreCase("unknown"))
					desc+=". Country: "+doc.get("country");
			}
			
			result.setRank(1);			
			result.setTotalResultCount(hits.size());
			
			if(doc.get("number of likes")!=null && !doc.get("number of likes").equalsIgnoreCase("unknown"))
				result.setViewCount(Integer.parseInt(doc.get("number of likes")));
			
			if(doc.get("number of comments")!=null && !doc.get("number of comments").equalsIgnoreCase("unknown"))
				result.setPrivacy(Integer.parseInt(doc.get("number of comments")));
			
			if(doc.get("document type").equalsIgnoreCase("photos") || doc.get("document type").equalsIgnoreCase("photo albums"))
			{
				result.setType(Query.CT_IMAGE);
				Set<Thumbnail> thumbnails = new LinkedHashSet<Thumbnail>();
				result.setImageUrl(doc.get("image 0 source"));
				if(doc.get("comments")!=null && !doc.get("comments").equalsIgnoreCase("null")  && !doc.get("comments").equalsIgnoreCase("unknown") )
				desc+=doc.get("comments");
				
				for(int i=0;i<8;i++)
				{
					Images img= new Images();
					if(doc.get("image "+i+" source")== null || doc.get("image "+i+" height")==null || doc.get("image "+i+" width")== null )
						continue;
				img.setSource(doc.get("image "+i+" source"));
				img.setHeight(new BigInteger(doc.get("image "+i+" height")));
				img.setWidth(new BigInteger(doc.get("image "+i+" width")));
				thumbnails.add(new Thumbnail(img.getSource(), img.getWidth().intValue(), img.getHeight().intValue()));
				
				 // thumbnails are orderd by size. so the last assigned image is the largest
				
				if(img.getWidth().intValue() <= 100)
					result.setEmbeddedSize1(CoreUtils.createImageCode(img.getSource(), img.getWidth().intValue(), img.getHeight().intValue(), 100, 100));
				
				else if(img.getWidth().intValue() <= 240 && img.getHeight().intValue() >= 300)
					result.setEmbeddedSize2("<img src=\""+ img.getSource() +"\" width=\""+ img.getWidth() +"\" height=\""+ img.getHeight() +"\" />");
				
				else if(img.getWidth().intValue() <= 500)
					result.setEmbeddedSize3("<img src=\""+ img.getSource() +"\" width=\""+ img.getWidth() +"\" height=\""+ img.getHeight() +"\" />");
				
				else if(img.getWidth().intValue() > 500)
					result.setEmbeddedSize4("<img src=\""+ img.getSource() +"\" width=\""+ img.getWidth() +"\" height=\""+ img.getHeight() +"\" />");
				}
				
				desc+="\r\n";
				if(doc.get("location")!=null && !doc.get("location").equalsIgnoreCase("unknown"))
					desc+=" Location: "+doc.get("location");
				
				if(doc.get("city")!=null && !doc.get("city").equalsIgnoreCase("unknown"))
					desc+=". City: "+doc.get("city");
				
				if(doc.get("country")!=null && !doc.get("country").equalsIgnoreCase("unknown"))
					desc+=". Country: "+doc.get("country");
				desc+="\r\n";
				
			result.setThumbnails(thumbnails);
			
			}
			
			if(doc.get("document type").equalsIgnoreCase("videos") || doc.get("document type").equalsIgnoreCase("videos uploaded"))
			{
					
				result.setType(Query.CT_VIDEO);			
				
				Set<Thumbnail> thumbnails = new LinkedHashSet<Thumbnail>();
				Thumbnail t= new Thumbnail(doc.get("thumbnail"), 160, 89);
				result.setEmbeddedSize1(CoreUtils.createImageCode(t, 100, 100));
				result.setThumbnails(thumbnails);
				result.setEmbeddedSize3(doc.get("embed html"));
				result.setImageUrl(t.getUrl());
			}
			
			if(doc.get("document type").equalsIgnoreCase("locations") || doc.get("document type").equalsIgnoreCase("events") )
			{
				if(doc.get("location")!=null && !doc.get("location").equalsIgnoreCase("unknown"))
					desc+="Location: "+doc.get("location");
				if(doc.get("city")!=null && !doc.get("city").equalsIgnoreCase("unknown"))
					desc+=". City: "+doc.get("city");
				if(doc.get("country")!=null && !doc.get("country").equalsIgnoreCase("unknown"))
					desc+=". Country: "+doc.get("country");
				
				if(doc.get("start time")!=null && !doc.get("start time").equalsIgnoreCase("unknown"))
					desc+="Start time: "+doc.get("start time");
				if(doc.get("end time")!=null && !doc.get("end time").equalsIgnoreCase("unknown"))
					desc+="End time: "+doc.get("end time");
				
			}
			
			
			
			if(doc.get("document type").equalsIgnoreCase("personality") )
			{
				if(doc.get("hometown")!=null && !doc.get("hometown").equalsIgnoreCase("unknown"))
					desc+=" hometown: "+doc.get("hometown");
				if(doc.get("current location")!=null && !doc.get("current location").equalsIgnoreCase("unknown"))
					desc+=" current location: "+doc.get("current location");
				if(doc.get("bio")!=null && !doc.get("bio").equalsIgnoreCase("unknown"))
					desc+=" About: "+doc.get("bio");
				
			}
			desc.replace("unknown", " ");
			desc.replace("\'unknown\'", " ");
			String encdesc=null;
			try {
				encdesc= new String(desc.getBytes(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			desc.replaceAll("unknown", " ");
			System.out.println(desc);
			System.out.println(encdesc);
			result.setDescription(encdesc);
			qresult.addResultItem(result);
		}
		

		
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


				    
	public Date GetLocalDateStringFromUTCString(String utcLongDateTime) {
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
	
	public static String timeAgoInWords(Date from) {
	    Date now = new Date();
	    long difference = now.getTime() - from.getTime();
	    long distanceInMin = difference / 60000;

	    if ( 0 <= distanceInMin && distanceInMin <= 1 ) {
	        return "Less than 1 minute ago";
	    } else if ( 1 <= distanceInMin && distanceInMin <= 45 ) {
	        return distanceInMin + " minutes ago";
	    } else if ( 45 <= distanceInMin && distanceInMin <= 89 ) {
	        return "About 1 hour ago";
	    } else if ( 90 <= distanceInMin && distanceInMin <= 1439 ) {
	        return "About " + (distanceInMin / 60) + " hours ago";
	    } else if ( 1440 <= distanceInMin && distanceInMin <= 2529 ) {
	        return "1 day";
	    } else if ( 2530 <= distanceInMin && distanceInMin <= 43199 ) {
	        return (distanceInMin / 1440) + "days ago";
	    } else if ( 43200 <= distanceInMin && distanceInMin <= 86399 ) {
	        return "About 1 month ago";
	    } else if ( 86400 <= distanceInMin && distanceInMin <= 525599 ) {
	        return "About " + (distanceInMin / 43200) + " months ago";
	    } else {
	        long distanceInYears = distanceInMin / 525600;
	        if(distanceInYears>1)
	        return "About " + distanceInYears + " years ago";
	        else
	        return "About " + distanceInYears + " year ago";
	    }
	}

	@Override
	public SocialSearchResult get(SocialSearchQuery query,
			AuthCredentials authCredentials) {
		SocialSearchResult result= new SocialSearchResult(query);
		SocialSearchResult res = socialSearchFB(query, authCredentials);
		result.getResultItems().addAll(res.getResultItems());
		// TODO Auto-generated method stub
		return result;
	}

	public SocialSearchResult socialSearchFB(
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
		String path= "/home/singh/learnweb/FacebookIndex/index";
		Lucene base= new Lucene(true, new File(path));
		
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
