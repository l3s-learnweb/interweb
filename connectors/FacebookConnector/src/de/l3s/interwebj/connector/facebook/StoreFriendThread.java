package de.l3s.interwebj.connector.facebook;

import java.util.HashMap;

import l3s.facebook.objects.user.User;

import org.apache.lucene.index.IndexWriter;

import facebook.api.Facebook;

class StoreFriendThread extends Thread {
	String userid;
	Facebook fbapi;
	HashMap<String,IndexWriter> writers;
	User friend;
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
	
		new VideosTaggedInThread(userid+ " videos", userid, fbapi, writers.get("videostaggedin")).start();
		new NotesThread(userid +" notes",userid, fbapi, writers.get("noteswriter")).start();
		new PhotosTaggedInThread(userid+" photos tagged in", userid, fbapi, writers.get("taggedphotoswriter")).start();
		new UserEventsThread(userid+" events", userid, fbapi, writers.get("eventswriter")).start();
		new UserGroupsThread(userid+" groups", userid, fbapi, writers.get("groupswriter")).start();
		new LikesThread(userid+" likes", userid, fbapi, writers.get("likeswriter")).start();
		new PhotoAlbumsThread(userid+" albums", userid, fbapi, writers.get("photoalbumwriter")).start();
		new VideosUploadedThread(userid+" videos uploaded", userid, fbapi, writers.get("videosuploadedwriter")).start();
		//new ProfileFeedThread(userid+" feed", userid, fbapi, feedwriter).start();
		new UserLocationsThread(userid+" locations", userid, fbapi, writers.get("locationwriter")).start();
		new PersonalityThread(userid+" personality", userid, fbapi, writers.get("userwriter")).start();
    	
	System.out.println("DONE! " + getName());
    }
}