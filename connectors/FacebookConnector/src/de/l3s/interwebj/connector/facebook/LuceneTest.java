package de.l3s.interwebj.connector.facebook;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.index.TermVectorMapper;
import org.apache.lucene.index.TermVectorOffsetInfo;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.Searchable;

public class LuceneTest {
	public static void main(String[] args) throws SQLException, IOException {
		String userid="613652787";
		String path= "C:\\Users\\singh\\FacebookIndex\\testnew";

		 String[] likes_fields= {"title", "description","category","about","website"};
		 String[] notes_fields= {"title", "message"};
		 String[] personality_fields= { "gender","political","concentration","document type","descreption","description","title",
				"project1 name", "project1 description", "project2 name", "project2 description", "project3 name", "project3 description"
				,"languages", "sports","favourite athletes","favourite teams","bio","website","hometown","current location","quotes","location","city","country","comments","from name",
				"message","caption","type","category","about","website"};
		 
		 String[] fields= { "gender","political","concentration","document type","descreption","description","title",
					"project1 name", "project1 description", "project2 name", "project2 description", "project3 name", "project3 description"
					,"languages", "sports","favourite athletes","favourite teams","bio","website","hometown","current location","quotes","location","city","country","comments","from name",
					"message","caption","type","category","about","website","source","concentration"};
		 
		 
		 String[] photostaggedin_fields= {"title", "location","city","country","comments","from name"};
		 
		 
		 
		 
		 String[] profilefeed_fields= {"title", "description","message","caption","type","from name","to name","application"};
		 String[] userevents_fields= {"title", "description","location","owner","status"};
		 String[] usergroups_fields= {"title", "description"};
		 String[] userlocations_fields= {"title", "city","country","application"};
		 String[] videostaggedin_fields= {"title", "city","type","country","application","from name"};
		 String[] videosuploaded_fields= {"title", "city","type","country","application","from name"};
		 String[] photoalbums_fields= {"title", "type"};
		
		
		 Lucene base= new Lucene(false, new File(path));
		HashMap<Integer, String> qbuckets= new HashMap<Integer, String>();
		 
		 ResultSet rs = null;
		 IndexSearcher searcher =null;
		try {
			IndexReader reader= IndexReader.open(base.getIndex());
			searcher = new IndexSearcher(reader);
				System.out.println("connecting to DB.....");
				java.sql.Connection dbConnection = DriverManager.getConnection("jdbc:mysql://mysql.l3s.uni-hannover.de/flickrcrawl?characterEncoding=utf8",
				       "zerr",
				        "aGSbXmmJuseznzm7");
				System.out.println("querying db");
				PreparedStatement queryselect = dbConnection.prepareStatement("SELECT query,count FROM study_queryset WHERE 1");
				rs = queryselect.executeQuery();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//HashSet<String> queriesselected= new HashSet<String>();
			while(rs.next())
			{
				Term t= new Term("content", rs.getString(1));
				int count = searcher.docFreq(t);
				if(count>0)
				{
					qbuckets.put(rs.getInt(2), rs.getString(1));
				}
					
				
			}
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
			//ArrayList<Document> docs = base.searchIndex("chennai",personality_fields,40);
		System.out.println("done");
		
	}

}
