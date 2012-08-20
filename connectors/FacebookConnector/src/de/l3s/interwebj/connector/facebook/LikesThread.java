package de.l3s.interwebj.connector.facebook;

import l3s.facebook.listresponse.likes.Likes;
import l3s.facebook.listresponse.notes.Notes;
import l3s.facebook.objects.note.Note;
import l3s.facebook.objects.page.Page;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

import facebook.api.Facebook;

class LikesThread extends Thread {
	String userid;
	Facebook fbapi;
	IndexWriter writer;
	Likes likes;
    public LikesThread(String str) {
	super(str);
    }
    public LikesThread(String threadname, String userid, Facebook fbapi, IndexWriter writer)
    {
    	super(threadname);
    	 this.userid= userid;
    	 this.likes=fbapi.getUserLikes(userid);
    	 this.fbapi=fbapi;
    	 this.writer=writer;
    }
    public void run() {
	
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
    	
	System.out.println("DONE! " + getName());
    }
}