package de.l3s.interwebj.connector.facebook;

import l3s.facebook.listresponse.profilefeed.Feedobject;
import l3s.facebook.listresponse.profilefeed.ProfileFeed;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

import facebook.api.Facebook;

class ProfileFeedThread extends Thread {
	String userid;
	Facebook fbapi;
	IndexWriter writer;
	ProfileFeed profilefeed;
    public ProfileFeedThread(String str) {
	super(str);
    }
    public ProfileFeedThread(String threadname, String userid, Facebook fbapi, IndexWriter writer)
    {
    	super(threadname);
    	 this.userid= userid;
    	 this.profilefeed= fbapi.getUserProfileFeed(userid);
    	 this.fbapi=fbapi;
    	 this.writer=writer;
    }
    public void run() {
    	ProfileFeed page0 = profilefeed;
    	while( page0.getPaging()!=null)
		{
    		if(page0.getPaging().getNext()==null)
				break;
			page0=fbapi.getNextPage(page0.getPaging().getNext(), ProfileFeed.class);
			profilefeed.getData().addAll(page0.getData());
			if(profilefeed.getData().size()>300)
				break;
			
		}

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
		
	
	
		
	System.out.println("DONE! " + getName());
    }
	}