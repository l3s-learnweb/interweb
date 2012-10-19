package de.l3s.interwebj.connector.facebook;

import l3s.facebook.listresponse.userlocations.Objectwithlocation;
import l3s.facebook.listresponse.userlocations.UserLocationObjects;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

import facebook.api.Facebook;

class UserLocationsThread extends Thread {
	String userid;
	Facebook fbapi;
	IndexWriter writer;
	UserLocationObjects locations;
    public UserLocationsThread(String str) {
	super(str);
    }
    public UserLocationsThread(String threadname, String userid, Facebook fbapi, IndexWriter writer)
    {
    	super(threadname);
    	 this.userid= userid;
    	 this.locations= fbapi.getUserLocationObjects(userid);
    	 this.fbapi=fbapi;
    	 this.writer=writer;
    }
    public void run() {

    	System.out.println(getName()+locations.getData().size());
    	if(locations.getData().size()>0)
    	{
    		UserLocationObjects page0 = locations;
    		//severe performance degrade
    		while(page0.getPaging()!=null)
    		{
    			if(page0.getPaging().getNext()==null)
    				break;
    			page0=fbapi.getNextPage(page0.getPaging().getNext(), UserLocationObjects.class);
    			locations.getData().addAll(page0.getData());
    			if (locations.getData().size()>300)
    			{
					break;
				}
    		}
    	
        	for(Objectwithlocation location : locations.getData())
    		{
        		
    			Document doc= new Document();
    			Field field= new Field("id", location.getId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			
    			field= new Field("type", location.getType(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("title", location.getPlace().getName(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("city", location.getPlace().getLocation().getCity(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("country", location.getPlace().getLocation().getCountry(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("application", location.getApplication().getName(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("created time", location.getCreatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("From id", location.getFrom().getId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("From name", location.getFrom().getName(), Field.Store.YES, Field.Index.ANALYZED);
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