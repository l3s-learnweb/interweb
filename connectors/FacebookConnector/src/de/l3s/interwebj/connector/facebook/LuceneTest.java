package de.l3s.interwebj.connector.facebook;

import java.io.File;
import java.util.ArrayList;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.Searchable;

public class LuceneTest {
	public static void main(String[] args) {
		String userid="613652787";
		String path= "C:\\Users\\singh\\workspaceinterweb\\FacebookConnector\\FacebookIndex\\"+userid+"new";

		 String[] likes_fields= {"title", "description","category","about","website"};
		 String[] notes_fields= {"title", "message"};
		 String[] personality_fields= { "gender","political","concentration","document type","descreption","description","title",
				"project1 name", "project1 description", "project2 name", "project2 description", "project3 name", "project3 description"
				,"languages", "sports","favourite athletes","favourite teams","bio","website","hometown","current location","quotes","location","city","country","comments","from name",
				"message","caption","type","category","about","website"};
		 
		 
		 
		 
		 String[] photostaggedin_fields= {"title", "location","city","country","comments","from name"};
		 
		 
		 
		 
		 String[] profilefeed_fields= {"title", "description","message","caption","type","from name","to name","application"};
		 String[] userevents_fields= {"title", "description","location","owner","status"};
		 String[] usergroups_fields= {"title", "description"};
		 String[] userlocations_fields= {"title", "city","country","application"};
		 String[] videostaggedin_fields= {"title", "city","type","country","application","from name"};
		 String[] videosuploaded_fields= {"title", "city","type","country","application","from name"};
		 String[] photoalbums_fields= {"title", "type"};
		
		
		 Lucene base= new Lucene(false, new File(path));
			/*Lucene notesbase= new Lucene(false, new File(path+"\\notesbase"));
			Lucene albumbase = new Lucene(false, new File(path+"\\albumsbase"));
			Lucene locationsbase= new Lucene(false, new File(path+"\\locationsbase"));
			Lucene videosuploadedbase= new Lucene(false, new File(path+"\\videosuploadedbase"));
			
			Lucene personalitybase= new Lucene(false, new File(path+"\\personalitybase"));
			//Lucene profilefeedbase= new Lucene(false, new File(path+"\\profilefeedbase"));
			Lucene videostaggedinbase= new Lucene(false, new File(path+"\\videostaggedinbase"));
			Lucene phototaggedbase= new Lucene(false, new File(path+"\\photostaggedinbase"));
			Lucene eventsbase= new Lucene(false, new File(path+"\\eventsbase"));
			Lucene likesbase= new Lucene(false, new File(path+"\\likesbase"));
			
			
			ArrayList<Document> likes = likesbase.searchIndex("starbucks",likes_fields,20);
			ArrayList<Document> notes = notesbase.searchIndex("starbucks",notes_fields,20);
			ArrayList<Document> personality = personalitybase.searchIndex("starbucks",personality_fields,20);
			ArrayList<Document> photoalbums = albumbase.searchIndex("starbucks",photoalbums_fields,20);
			ArrayList<Document> photostaggedin = phototaggedbase.searchIndex("starbucks",photostaggedin_fields,20);
			//ArrayList<Document> profilefeed = profilefeedbase.searchIndex("starbucks",profilefeed_fields,20);
			ArrayList<Document> userevents = eventsbase.searchIndex("starbucks",userevents_fields,20);
			ArrayList<Document> usergroups = groupsbase.searchIndex("starbucks",usergroups_fields,20);
			ArrayList<Document> userlocations = locationsbase.searchIndex("starbucks",userlocations_fields,20);
			ArrayList<Document> videostaggedin = videostaggedinbase.searchIndex("starbucks",videostaggedin_fields,20);*/
			ArrayList<Document> docs = base.searchIndex("chennai",personality_fields,40);
		System.out.println("done");
		
	}

}
