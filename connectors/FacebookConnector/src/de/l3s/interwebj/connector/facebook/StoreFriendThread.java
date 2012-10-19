package de.l3s.interwebj.connector.facebook;

import java.util.HashMap;

import l3s.facebook.listresponse.likes.Likes;
import l3s.facebook.listresponse.notes.Notes;
import l3s.facebook.listresponse.photos.Photos;
import l3s.facebook.listresponse.profilefeed.ProfileFeed;
import l3s.facebook.listresponse.userevents.Events;
import l3s.facebook.listresponse.usergroups.Groups;
import l3s.facebook.listresponse.userlocations.UserLocationObjects;
import l3s.facebook.listresponse.userphotoalbums.Photoalbums;
import l3s.facebook.listresponse.videosuploadedandtagged.Videolist;
import l3s.facebook.objects.user.User;

import org.apache.lucene.index.IndexWriter;

import facebook.api.Facebook;

class StoreFriendThread extends Thread {
	String userid;
	Facebook fbapi;
	HashMap<String,IndexWriter> writers;
	User friend;

	Photos photostaggedin;
    public StoreFriendThread(String str) {
	super(str);
    }
    public StoreFriendThread(String threadname, String userid, Facebook fbapi, HashMap<String,IndexWriter> writers)
    {
    	super(threadname);
    	
    	 this.userid= userid;
    	 this.friend= fbapi.getEntity(userid, User.class);
    	 this.fbapi=fbapi;
    	 this.writers=writers;
    	
    	 
    }
    public void run() {
	
    	
    	System.out.println("runing lucene threads");
		new VideosTaggedInThread(userid+ " videos", userid, fbapi, writers.get("videostaggedin")).run();
		new NotesThread(userid +" notes",userid, fbapi, writers.get("noteswriter")).run();
		new PhotosTaggedInThread(userid+" photos tagged in", userid, fbapi, writers.get("taggedphotoswriter")).run();
		new UserEventsThread(userid+" events", userid, fbapi, writers.get("eventswriter")).run();
		new UserGroupsThread(userid+" groups", userid, fbapi, writers.get("groupswriter")).run();
		new LikesThread(userid+" likes", userid, fbapi, writers.get("likeswriter")).run();
		new PhotoAlbumsThread(userid+" albums", userid, fbapi, writers.get("photoalbumwriter")).run();
		new VideosUploadedThread(userid+" videos uploaded", userid, fbapi, writers.get("videosuploadedwriter")).run();
		//new ProfileFeedThread(userid+" feed", userid, fbapi, feedwriter).run();
		new UserLocationsThread(userid+" locations", userid, fbapi, writers.get("locationwriter")).run();
		new PersonalityThread(userid+" personality", userid, fbapi, writers.get("userwriter")).run();
    	
	System.out.println("DONE! " + getName());
    }
}