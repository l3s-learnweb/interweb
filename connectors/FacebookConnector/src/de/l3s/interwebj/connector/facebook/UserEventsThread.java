package de.l3s.interwebj.connector.facebook;

import l3s.facebook.listresponse.userevents.Events;
import l3s.facebook.listresponse.userlocations.Objectwithlocation;
import l3s.facebook.listresponse.userlocations.UserLocationObjects;
import l3s.facebook.objects.event.Event;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

import facebook.api.Facebook;

class UserEventsThread extends Thread {
	String userid;
	Facebook fbapi;
	IndexWriter writer;
	Events events;
    public UserEventsThread(String str) {
	super(str);
    }
    public UserEventsThread(String threadname, String userid, Facebook fbapi, IndexWriter writer)
    {
    	super(threadname);
    	 this.userid= userid;
    	 this.events= fbapi.getEventsUserIsInvolvedIn(userid);
    	 this.fbapi=fbapi;
    	 this.writer=writer;
    }
    public void run() {
    	
    	System.out.println(getName()+events.getData().size());
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
    			Field field= new Field("id", event.getId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("end time", event.getEndTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("start time", event.getStartTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("location", event.getLocation(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("title", event.getName(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("status", event.getRsvpStatus(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("timezone", event.getTimezone(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			
    			Event eventpage = fbapi.getEntity(event.getId().toString(), Event.class);
    			
    			
    			field= new Field("description", eventpage.getDescription().toString(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("privacy", eventpage.getPrivacy(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("owner", eventpage.getOwner().getName(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("updated time", eventpage.getUpdatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
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
    		
    		
    		
    	}
    	
		
	System.out.println("DONE! " + getName());
    }
}