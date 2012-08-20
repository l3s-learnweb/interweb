package facebook.api;

import java.io.UnsupportedEncodingException;
import java.util.TreeMap;

import javax.ws.rs.core.MediaType;

import l3s.facebook.listresponse.books.Books;
import l3s.facebook.listresponse.friends.Friends;
import l3s.facebook.listresponse.likes.Likes;
import l3s.facebook.listresponse.movies.Movies;
import l3s.facebook.listresponse.music.Music;
import l3s.facebook.listresponse.notes.Notes;
import l3s.facebook.listresponse.permissions.Permissions;
import l3s.facebook.listresponse.photos.Photos;
import l3s.facebook.listresponse.profilefeed.ProfileFeed;
import l3s.facebook.listresponse.userevents.Events;
import l3s.facebook.listresponse.usergroups.Groups;
import l3s.facebook.listresponse.userlocations.UserLocationObjects;
import l3s.facebook.listresponse.userphotoalbums.Photoalbums;
import l3s.facebook.listresponse.videosuploadedandtagged.Videolist;
import l3s.facebook.search.response.Results;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;


public class Facebook implements SocialNetworkInterface {
	
	private String graphAPI;
	private String accesstokenString;
	private String accesstoken;
	private Client FacebookClient;
	
	public Facebook( String accessToken) {
		
		graphAPI= "https://graph.facebook.com/";
		accesstokenString="?access_token=";
		setAccesstoken(accessToken);
		accesstokenString+=accessToken;
		ClientConfig clientConfig = new DefaultClientConfig();
		FacebookClient = Client.create(clientConfig);
	}
	
	public Facebook() {
		graphAPI= "https://graph.facebook.com/";
		accesstokenString=null;
		setAccesstoken(null);
		ClientConfig clientConfig = new DefaultClientConfig();
		FacebookClient = Client.create(clientConfig);
		
	}

	

	public String getGraphAPI() {
		return graphAPI;
	}

	public void setGraphAPI(String graphAPI) {
		this.graphAPI = graphAPI;
	}

	@Override
	public <T> T getEntity(String id, Class<T> type) {
		WebResource resource = FacebookClient.resource(graphAPI+id+accesstokenString);

		TreeMap<String, String> params = new TreeMap<String, String>();

		for (String key : params.keySet()) {
			String value = params.get(key);
			resource = resource.queryParam(key, value);
		}
	
		T objectResponse= null;
		try{
			objectResponse= resource.accept(MediaType.APPLICATION_JSON).get(type);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return objectResponse;
	}

	

	@Override
	public Books getBooksUserIsInterestedIn(String id) {
		WebResource resource = FacebookClient.resource(graphAPI+id+"/books"+accesstokenString);

		TreeMap<String, String> params = new TreeMap<String, String>();

		for (String key : params.keySet()) {
			String value = params.get(key);
			resource = resource.queryParam(key, value);
		}
	
		
		
		Books objectResponse= resource.accept(MediaType.APPLICATION_JSON).get(Books.class);
		return objectResponse;
	}

	@Override
	public Friends getFriendsof(String id) {
		WebResource resource = FacebookClient.resource(graphAPI+id+"/friends"+accesstokenString);

		TreeMap<String, String> params = new TreeMap<String, String>();

		for (String key : params.keySet()) {
			String value = params.get(key);
			resource = resource.queryParam(key, value);
		}
	
		 
		
		Friends objectResponse= resource.accept(MediaType.APPLICATION_JSON).get(Friends.class);
		return objectResponse;
	}

	@Override
	public Likes getUserLikes(String id) {
		WebResource resource = FacebookClient.resource(graphAPI+id+"/likes"+accesstokenString);

		TreeMap<String, String> params = new TreeMap<String, String>();

		for (String key : params.keySet()) {
			String value = params.get(key);
			resource = resource.queryParam(key, value);
		}
	
		
		Likes objectResponse= resource.accept(MediaType.APPLICATION_JSON).get(Likes.class);
		return objectResponse;
	}

	@Override
	public Movies getMoviesUserIsInterestedIn(String id) {
		WebResource resource = FacebookClient.resource(graphAPI+id+"/movies"+accesstokenString);

		TreeMap<String, String> params = new TreeMap<String, String>();

		for (String key : params.keySet()) {
			String value = params.get(key);
			resource = resource.queryParam(key, value);
		}
	
		
		Movies objectResponse= resource.accept(MediaType.APPLICATION_JSON).get(Movies.class);
		return objectResponse;
	}

	@Override
	public Notes getNotesOfUser(String id) {
		WebResource resource = FacebookClient.resource(graphAPI+id+"/notes"+accesstokenString);

		TreeMap<String, String> params = new TreeMap<String, String>();

		for (String key : params.keySet()) {
			String value = params.get(key);
			resource = resource.queryParam(key, value);
		}
	
		
		Notes objectResponse= resource.accept(MediaType.APPLICATION_JSON).get(Notes.class);
		return objectResponse;
	}

	@Override
	public Music getMusicUserIsInterestedIn(String id) {
		WebResource resource = FacebookClient.resource(graphAPI+id+"/music"+accesstokenString);

		TreeMap<String, String> params = new TreeMap<String, String>();

		for (String key : params.keySet()) {
			String value = params.get(key);
			resource = resource.queryParam(key, value);
		}
	
		
		Music objectResponse= resource.accept(MediaType.APPLICATION_JSON).get(Music.class);
		return objectResponse;
	}

	@Override
	public Permissions getPermissionsOfUser(String id) {
		WebResource resource = FacebookClient.resource(graphAPI+id+"/permissions"+accesstokenString);

		TreeMap<String, String> params = new TreeMap<String, String>();

		for (String key : params.keySet()) {
			String value = params.get(key);
			resource = resource.queryParam(key, value);
		}
	
		
		Permissions objectResponse= resource.accept(MediaType.APPLICATION_JSON).get(Permissions.class);
		return objectResponse;
	}

	@Override
	public Photos getPhotosOfUser(String id) {
		WebResource resource = FacebookClient.resource(graphAPI+id+"/photos"+accesstokenString);

		TreeMap<String, String> params = new TreeMap<String, String>();

		for (String key : params.keySet()) {
			String value = params.get(key);
			resource = resource.queryParam(key, value);
		}
	
		
		Photos objectResponse= resource.accept(MediaType.APPLICATION_JSON).get(Photos.class);
		return objectResponse;
	}

	@Override
	public Photoalbums getAlbumsOfUser(String id) {
		WebResource resource = FacebookClient.resource(graphAPI+id+"/albums"+accesstokenString);

		TreeMap<String, String> params = new TreeMap<String, String>();

		for (String key : params.keySet()) {
			String value = params.get(key);
			resource = resource.queryParam(key, value);
		}
	
		
		Photoalbums objectResponse= resource.accept(MediaType.APPLICATION_JSON).get(Photoalbums.class);
		return objectResponse;
	}

	@Override
	public ProfileFeed getUserProfileFeed(String id) {
		WebResource resource = FacebookClient.resource(graphAPI+id+"/feed"+accesstokenString);

		TreeMap<String, String> params = new TreeMap<String, String>();

		for (String key : params.keySet()) {
			String value = params.get(key);
			resource = resource.queryParam(key, value);
		}
	
		
		ProfileFeed objectResponse= resource.accept(MediaType.APPLICATION_JSON).get(ProfileFeed.class);
		return objectResponse;
	}

	@Override
	public Events getEventsUserIsInvolvedIn(String id) {
		WebResource resource = FacebookClient.resource(graphAPI+id+"/events"+accesstokenString);

		TreeMap<String, String> params = new TreeMap<String, String>();

		for (String key : params.keySet()) {
			String value = params.get(key);
			resource = resource.queryParam(key, value);
		}
	
		
		Events objectResponse= resource.accept(MediaType.APPLICATION_JSON).get(Events.class);
		return objectResponse;
	}

	@Override
	public Groups getGroupsUserIsMemberOf(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserLocationObjects getUserLocationObjects(String id) {
		WebResource resource = FacebookClient.resource(graphAPI+id+"/locations"+accesstokenString);

		TreeMap<String, String> params = new TreeMap<String, String>();

		for (String key : params.keySet()) {
			String value = params.get(key);
			resource = resource.queryParam(key, value);
		}
	
		
		UserLocationObjects objectResponse= resource.accept(MediaType.APPLICATION_JSON).get(UserLocationObjects.class);
		return objectResponse;
	}

	@Override
	public Videolist getVideosUserIsTaggedIn(String id) {
		WebResource resource = FacebookClient.resource(graphAPI+id+"/videos"+accesstokenString);

		TreeMap<String, String> params = new TreeMap<String, String>();

		for (String key : params.keySet()) {
			String value = params.get(key);
			resource = resource.queryParam(key, value);
		}
	
		
		Videolist objectResponse= resource.accept(MediaType.APPLICATION_JSON).get(Videolist.class);
		return objectResponse;
	}

	@Override
	public Videolist getVideosUserHasUploaded(String id) {
		WebResource resource = FacebookClient.resource(graphAPI+id+"/videos/uploaded"+accesstokenString);

		TreeMap<String, String> params = new TreeMap<String, String>();

		for (String key : params.keySet()) {
			String value = params.get(key);
			resource = resource.queryParam(key, value);
		}
	
		
		Videolist objectResponse= resource.accept(MediaType.APPLICATION_JSON).get(Videolist.class);
		return objectResponse;
	}

	

	public String getAccesstoken() {
		return accesstoken;
	}

	public void setAccesstoken(String accesstoken) {
		this.accesstoken = accesstoken;
	}

	@Override
	public Results searchPublicPosts(String query,Integer limit) {
		String encodedquery=null;
		try {
			encodedquery = new String(query.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
			
		}
		WebResource resource = FacebookClient.resource(graphAPI+"search?");
		resource = resource.queryParam("q", encodedquery);
		resource = resource.queryParam("limit", ""+limit);
		resource = resource.queryParam("access_token", accesstoken);
		TreeMap<String, String> params = new TreeMap<String, String>();

		for (String key : params.keySet()) {
			String value = params.get(key);
			resource = resource.queryParam(key, value);
		}
	
		
		Results objectResponse= resource.accept(MediaType.APPLICATION_JSON).get(Results.class);
		return objectResponse;
	}

	@Override
	public <T> T getNextPage(String next, Class<T> type) {
		
		if(next==null)
			return null;
		WebResource resource = FacebookClient.resource(next);

		TreeMap<String, String> params = new TreeMap<String, String>();

		for (String key : params.keySet()) {
			String value = params.get(key);
			resource = resource.queryParam(key, value);
		}
	
		
		T objectResponse= null;
		try{
			objectResponse= resource.accept(MediaType.APPLICATION_JSON).get(type);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return objectResponse;
	}

}
