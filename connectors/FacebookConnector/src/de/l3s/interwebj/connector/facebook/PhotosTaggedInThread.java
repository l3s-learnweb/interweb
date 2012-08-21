package de.l3s.interwebj.connector.facebook;

import l3s.facebook.listresponse.photos.Photos;
import l3s.facebook.objects.photo.Comments;
import l3s.facebook.objects.photo.Photo;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

import facebook.api.Facebook;

class PhotosTaggedInThread extends Thread {
	String userid;
	Facebook fbapi;
	IndexWriter writer;
	Photos photostaggedin;
    public PhotosTaggedInThread(String str) {
	super(str);
    }
    public PhotosTaggedInThread(String threadname, String userid, Facebook fbapi, IndexWriter writer)
    {
    	super(threadname);
    	 this.userid= userid;
    	 this.photostaggedin= fbapi.getPhotosOfUser(userid);
    	 this.fbapi=fbapi;
    	 this.writer=writer;
    }
    public void run() {
    	System.out.println(getName()+photostaggedin.getData().size());
    	if(photostaggedin.getData().size()>0)
    	{
    		Photos page0 = photostaggedin;
    		//severe performance degrade
    		while(page0.getPaging()!=null)
    		{
    			if(page0.getPaging().getNext()==null)
    				break;
    			page0=fbapi.getNextPage(page0.getPaging().getNext(), Photos.class);
    			photostaggedin.getData().addAll(page0.getData());
    			if(photostaggedin.getData().size()>50)
    				break;
    			
    		}
    		for(Photo photo:photostaggedin.getData())
    		{
    			if(photo.getName().equalsIgnoreCase("unknown") && photo.getPlace().getName().equalsIgnoreCase("unknown") && photo.getPlace().getLocation().getCity().equalsIgnoreCase("unknown") && photo.getPlace().getLocation().getCountry().equalsIgnoreCase("unknown") )
    			{
    				continue;
    				
    			}
    			else
    			{
    				Document doc= new Document();
    				if(photo.getId().equalsIgnoreCase("0"))
    					continue;
    				Field field= new Field("id", photo.getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("title", photo.getName(), Field.Store.YES, Field.Index.ANALYZED);
    				doc.add(field);
    				field= new Field("link", photo.getLink(), Field.Store.YES, Field.Index.ANALYZED);
    				doc.add(field);
    				field= new Field("thumbnail", photo.getPicture(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("icon", photo.getIcon(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("source", photo.getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("location", photo.getPlace().getName(), Field.Store.YES, Field.Index.ANALYZED);
    				doc.add(field);
    				field= new Field("city", photo.getPlace().getLocation().getCity(), Field.Store.YES, Field.Index.ANALYZED);
    				doc.add(field);
    				field= new Field("country", photo.getPlace().getLocation().getCountry(), Field.Store.YES, Field.Index.ANALYZED);
    				doc.add(field);
    				
    				l3s.facebook.objects.photo.Comments comments = photo.getComments();
    				String commentsinstring= "unknown";
    				
    				if(comments!=null && comments.getData().size()>0)
    				{
    					Comments compage0= comments;
    					while(compage0.getPaging()!=null)
    					{
    						if(compage0.getPaging().getNext()==null)
    							break;
    						compage0=fbapi.getNextPage(compage0.getPaging().getNext(), Comments.class);
    						comments.getData().addAll(compage0.getData());
    						
    					}
    					commentsinstring = convertToCommaSeperatedList(comments);
    				}
    				
    				
    				field= new Field("comments",commentsinstring , Field.Store.YES, Field.Index.ANALYZED);
    				doc.add(field);
    				field= new Field("updated time", photo.getUpdatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("created time", photo.getCreatedTime(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("from name", photo.getFrom().getName(), Field.Store.YES, Field.Index.ANALYZED);
    				doc.add(field);
    				field= new Field("from id", photo.getFrom().getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image h:"+photo.getImages().get(0).getHeight()+" w:"+photo.getImages().get(0).getWidth()+" source", photo.getImages().get(0).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image h:"+photo.getImages().get(1).getHeight()+" w:"+photo.getImages().get(1).getWidth()+" source", photo.getImages().get(1).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image h:"+photo.getImages().get(2).getHeight()+" w:"+photo.getImages().get(2).getWidth()+" source", photo.getImages().get(2).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image h:"+photo.getImages().get(3).getHeight()+" w:"+photo.getImages().get(3).getWidth()+" source", photo.getImages().get(3).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image h:"+photo.getImages().get(4).getHeight()+" w:"+photo.getImages().get(4).getWidth()+" source", photo.getImages().get(4).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image h:"+photo.getImages().get(5).getHeight()+" w:"+photo.getImages().get(5).getWidth()+" source", photo.getImages().get(5).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image h:"+photo.getImages().get(6).getHeight()+" w:"+photo.getImages().get(6).getWidth()+" source", photo.getImages().get(6).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    				doc.add(field);
    				field= new Field("image h:"+photo.getImages().get(7).getHeight()+" w:"+photo.getImages().get(7).getWidth()+" source", photo.getImages().get(7).getSource(), Field.Store.YES, Field.Index.NOT_ANALYZED);
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
    		
    	}
    	

	System.out.println("DONE! " + getName());
    }
    private String convertToCommaSeperatedList(l3s.facebook.objects.photo.Comments comments) {
		StringBuilder nameBuilder = new StringBuilder();

	   for(l3s.facebook.objects.photo.Data d: comments.getData())
	   {
		   nameBuilder.append("'").append(d.getMessage()).append("',");
	   }
	       
	    

	    nameBuilder.deleteCharAt(nameBuilder.length() - 1);

	    return nameBuilder.toString();
		
	}

}