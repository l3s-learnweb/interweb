package facebook.api;

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


public interface SocialNetworkInterface {
	
	public <T> T getEntity(String id, Class<T> type);
	
	public <T> T getNextPage(String next, Class<T> type);
	
	public Books getBooksUserIsInterestedIn(String id);

	public Friends getFriendsof(String id);
	
	public Likes getUserLikes(String id);
	
	public Movies getMoviesUserIsInterestedIn(String id);
	
	public Notes getNotesOfUser(String id);
	
	public Music getMusicUserIsInterestedIn(String id);
	
	public Permissions getPermissionsOfUser(String id);
	
	public Photos getPhotosOfUser(String id);
	
	public Photoalbums getAlbumsOfUser(String id);
	
	public ProfileFeed getUserProfileFeed(String id);
	
	public Events getEventsUserIsInvolvedIn(String id);
	
	public Groups getGroupsUserIsMemberOf(String id);
	
	public UserLocationObjects getUserLocationObjects(String id);
	
	public Videolist getVideosUserIsTaggedIn(String id);
	
	public Videolist getVideosUserHasUploaded(String id);
	
	public Results searchPublicPosts(String query,Integer limit);

	
}
