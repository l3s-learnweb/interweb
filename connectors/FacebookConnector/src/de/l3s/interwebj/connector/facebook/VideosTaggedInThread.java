package de.l3s.interwebj.connector.facebook;

import l3s.facebook.listresponse.videosuploadedandtagged.Videolist;
import l3s.facebook.objects.video.Comments;
import l3s.facebook.objects.video.Video;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

import facebook.api.Facebook;

class VideosTaggedInThread extends Thread {
	String userid;
	Facebook fbapi;
	IndexWriter writer;
	Videolist videostaggedin;
    public VideosTaggedInThread(String str) {
	super(str);
    }
    public VideosTaggedInThread(String threadname, String userid, Facebook fbapi, IndexWriter writer )
    {
    	super(threadname);
    	 this.userid= userid;
    	 this.videostaggedin= fbapi.getVideosUserIsTaggedIn(userid);
    	 this.fbapi=fbapi;
    	 this.writer=writer;
    }
    public void run() {
    	
    	System.out.println(getName()+videostaggedin.getData().size());
    	if(videostaggedin.getData().size()>0)
    	{
    		Videolist page0 = videostaggedin;
        	while( page0.getPaging()!=null)
    		{
        		if(page0.getPaging().getNext()==null)
    				break;
    			page0=fbapi.getNextPage(page0.getPaging().getNext(), Videolist.class);
    			videostaggedin.getData().addAll(page0.getData());
    			
    		}
    		for(Video video:videostaggedin.getData())
    		{
    			if (video.getId()==null) {
					continue;
				}
        		System.out.println(video.getName());
    			Document doc= new Document();
    			Field field= new Field("id", video.getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("title", video.getName(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("embed html", video.getEmbedHtml(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("thumbnail", video.getPicture(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("icon", video.getIcon(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("source", video.getSource(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("description", video.getDescription(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			l3s.facebook.objects.video.Comments comments = video.getComments();
    			String commentsinstring= "unknown";
    			if(comments.getData().size()>0)
    			{
    				Comments compage0 = comments;
    				while(compage0.getPaging().getNext()!=null)
    				{
    					compage0=fbapi.getNextPage(compage0.getPaging().getNext(), Comments.class);
    					comments.getData().addAll(compage0.getData());
    					
    				}
    				commentsinstring = convertToCommaSeperatedList(comments);
    			}
    				
    			field= new Field("comments",commentsinstring , Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("updated time", video.getUpdatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("created time", video.getCreatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    			doc.add(field);
    			field= new Field("from name", video.getFrom().getName(), Field.Store.YES, Field.Index.ANALYZED);
    			doc.add(field);
    			field= new Field("from id", video.getFrom().getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
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
	private String convertToCommaSeperatedList(Comments comments) {
		StringBuilder nameBuilder = new StringBuilder();

		   for( l3s.facebook.objects.video.Data d: comments.getData())
		   {
			   nameBuilder.append("'").append(d.getMessage()).append("',");
		   }
		       
		    

		    nameBuilder.deleteCharAt(nameBuilder.length() - 1);

		    return nameBuilder.toString();
	}
}