package de.l3s.interwebj.connector.facebook;

import l3s.facebook.listresponse.notes.Notes;
import l3s.facebook.objects.note.Note;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

import facebook.api.Facebook;

class NotesThread extends Thread {
	String userid;
	Facebook fbapi;
	IndexWriter writer;
	Notes notes;
    public NotesThread(String str) {
	super(str);
    }
    public NotesThread(String threadname, String userid, Facebook fbapi, IndexWriter writer)
    {
    	super(threadname);
    	 this.userid= userid;
    	 this.notes= fbapi.getNotesOfUser(userid);
    	 this.fbapi=fbapi;
    	 this.writer=writer;
    }
    public void run() {
	
    	System.out.println(getName()+notes.getData().size());
    	if(notes.getData().size()>0)
    	{
    		Notes page0 = notes;
    		//severe performance degrade
    		while(page0.getPaging()!=null)
    		{
    			if(page0.getPaging().getNext()==null)
    				break;
    			page0=fbapi.getNextPage(page0.getPaging().getNext(), Notes.class);
    			notes.getData().addAll(page0.getData());
    			if(notes.getData().size()>50)
    				break;
    			
    		}
        	for(Note n: notes.getData())
    		{
        		if(n.getId()==null)
        		continue;
        		
        		System.out.println(n.getSubject());
    			Document doc= new Document();
    			Field field= new Field("id", n.getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			
    			field= new Field("message", n.getMessage(), Field.Store.NO, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("title", n.getSubject(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("updated time", n.getUpdatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("created time", n.getCreatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("From id", n.getFrom().getId(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("From name", n.getFrom().getName(), Field.Store.YES, Field.Index.ANALYZED);
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