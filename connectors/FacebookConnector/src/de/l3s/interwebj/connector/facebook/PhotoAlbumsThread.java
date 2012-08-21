package de.l3s.interwebj.connector.facebook;

import l3s.facebook.listresponse.userphotoalbums.Photoalbums;
import l3s.facebook.objects.photoalbum.Photoalbum;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

import facebook.api.Facebook;

class PhotoAlbumsThread extends Thread {
	String userid;
	Facebook fbapi;
	IndexWriter writer;
	Photoalbums photoalbums;
    public PhotoAlbumsThread(String str) {
	super(str);
    }
    public PhotoAlbumsThread(String threadname, String userid, Facebook fbapi, IndexWriter writer)
    {
    	super(threadname);
    	 this.userid= userid;
    	 this.photoalbums= fbapi.getAlbumsOfUser(userid);
    	 this.fbapi=fbapi;
    	 this.writer=writer;
    }
    public void run() {
	
    		System.out.println(getName()+photoalbums.getData().size());
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
    			Field field= new Field("id", album.getId(), Field.Store.YES, Field.Index.ANALYZED);
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
    			field= new Field("from id", album.getFrom().getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("from name", album.getFrom().getName(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("number of photos", album.getCount().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);if(album.getLikes()!=null)
    			{
    				field= new Field("likes", ""+album.getLikes().getData().size(), Field.Store.YES, Field.Index.NOT_ANALYZED);
        			doc.add(field);
    			}
    			
    			field= new Field("user", userid, Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			try {
    				writer.addDocument(doc);
    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} 
    		}
    		
    		
    		
    		
    		}
			
		
	System.out.println("DONE! " + getName());
    }
}