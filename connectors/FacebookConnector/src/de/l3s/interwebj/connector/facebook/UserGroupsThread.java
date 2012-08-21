package de.l3s.interwebj.connector.facebook;

import l3s.facebook.listresponse.usergroups.Groups;
import l3s.facebook.listresponse.userlocations.Objectwithlocation;
import l3s.facebook.listresponse.userlocations.UserLocationObjects;
import l3s.facebook.objects.group.Group;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

import facebook.api.Facebook;

class UserGroupsThread extends Thread {
	String userid;
	Facebook fbapi;
	IndexWriter writer;
	Groups groups;
    public UserGroupsThread(String str) {
	super(str);
    }
    public UserGroupsThread(String threadname, String userid, Facebook fbapi, IndexWriter writer)
    {
    	super(threadname);
    	 this.userid= userid;
    	 this.groups= fbapi.getGroupsUserIsMemberOf(userid);
    	 this.fbapi=fbapi;
    	 this.writer=writer;
    }
    public void run() {
	
    	System.out.println(getName()+groups.getData().size());
    	if(groups.getData().size()>0)
    	{
    		Groups page0 = groups;
    		//severe performance degrade
    		while(page0.getPaging()!=null)
    		{
    			if(page0.getPaging().getNext()==null)
    				break;
    			page0=fbapi.getNextPage(page0.getPaging().getNext(), Groups.class);
    			groups.getData().addAll(page0.getData());
    		}
    		for(l3s.facebook.listresponse.usergroups.Data group :groups.getData())
    		{
    			if(group.getId()==null)
    				continue;
    			Document doc= new Document();
    			Field field= new Field("id", group.getId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			
    			field= new Field("title", group.getName(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			
    			Group usergroup = fbapi.getEntity(group.getId().toString(), Group.class);
    			
    			field= new Field("description", usergroup.getDescription(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("email", usergroup.getEmail(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("icon", usergroup.getIcon(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("privacy", usergroup.getPrivacy(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("updated time", usergroup.getUpdatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
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