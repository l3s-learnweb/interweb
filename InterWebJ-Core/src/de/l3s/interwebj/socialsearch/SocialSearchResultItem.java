package de.l3s.interwebj.socialsearch;


import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.TimeZone;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.lucene.document.Document;


public class SocialSearchResultItem
    implements Serializable
{
	private String username;
	private String userid;
	private String imageurl;
	private ArrayList<Document> resultitems;
	private String story;
	private String reason;
	
	private ArrayList<String> embedhtmlofphotos;
	private ArrayList<String> embedhtmlofvideos;
	
	public SocialSearchResultItem(String username, String userid,
			String imageurl, ArrayList<Document> resultitems) {
		super();
		this.username = username;
		this.userid = userid;
		this.imageurl = imageurl;
		this.resultitems = resultitems;
		
		findembedhtml();
	}

	private void findembedhtml() {
		
		reason= new String();
		story= new String();
		embedhtmlofphotos= new ArrayList<String>();
		embedhtmlofvideos=new ArrayList<String>();
		username=resultitems.get(0).get("user name");
		Document topdoc=null;
		float score=0;
		for(Document doc: resultitems)
		{
			if(Float.valueOf(doc.get("score"))>score)
			{
				topdoc=doc;
				score=Float.valueOf(doc.get("score"));
			}
			if(doc.get("document type").equalsIgnoreCase("photos")||doc.get("document type").equalsIgnoreCase("photo albums"))
			{
				
				
				String embedphoto_2="<img src=\""+ doc.get("image 2 source") +"\" width=\""+  doc.get("image 2 width") +"\" height=\""+  doc.get("image 2 height") +"\" />";
				embedhtmlofphotos.add(embedphoto_2);
				//String embedphoto_5="<img src=\""+ doc.get("image 5 source") +"\" width=\""+  doc.get("image 5 width") +"\" height=\""+  doc.get("image 5 height") +"\" />";
				continue;
			}
			if(doc.get("document type").equalsIgnoreCase("videos")||doc.get("document type").equalsIgnoreCase("videos uploaded"))
			{
				
				embedhtmlofvideos.add(doc.get("embed html"));
				continue;
			}
			if(doc.get("document type").equalsIgnoreCase("likes"))
			{
				String res= username+ " likes "+ doc.get("title");
				reason+=res+". ";
				continue;
			}
			if(doc.get("document type").equalsIgnoreCase("locations"))
			{
				String date = GetLocalDateStringFromUTCString(doc.get("created time"));
				String res= username+ " was here "+ doc.get("title")+ "( "+doc.get("city")+", "+doc.get("country")+" )"+ "on "+date;
				reason+=res+". ";
				continue;
			}
			if(doc.get("document type").equalsIgnoreCase("groups"))
			{
				String res= username+ " is a member of "+ doc.get("title");
				reason+=res+". ";
				continue;
			}
			if(doc.get("document type").equalsIgnoreCase("events"))
			{
				DateFormat formatter= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				String dateofevent = GetLocalDateStringFromUTCString(doc.get("start time"));
				Date now= new Date();
				
				
				String res= null;
				try {
					if(now.after(formatter.parse(dateofevent)))
					{
						res=username+ " has attended "+ doc.get("title");
					}
					else
					{
						res=username+ " is attending "+ doc.get("title");
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				reason+=res+". ";
				continue;
			}
			if(doc.get("document type").equalsIgnoreCase("note"))
			{
				String res= username+ " has a note called "+ doc.get("title");
				reason+=res+". ";
				continue;
			}
			if(doc.get("document type").equalsIgnoreCase("work"))
			{
				String res= username+ " has worked at "+ doc.get("title");
				reason+=res+". ";
				continue;
			}
			if(doc.get("document type").equalsIgnoreCase("work"))
			{
				String res= username+ " has worked at "+ doc.get("title")+". Location: "+doc.get("location")+". Position: "+ doc.get("position");
				reason+=res+". ";
				continue;
			}
			if(doc.get("document type").equalsIgnoreCase("education"))
			{
				String res= username+ " has studied at "+ doc.get("title")+". Location: "+doc.get("location")+". concentration: "+ doc.get("concentration");
				reason+=res+". ";
				continue;
			}	
			if(doc.get("document type").equalsIgnoreCase("personality"))
			{
				String res= username+ " is strongly related to what you are looking for";
				reason+=res+". ";
				continue;
			}	
			
		}
		
		story= topdoc.get("description");
		
	}

	private void findreason() {
		
		
	}

	private String findstory() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getImageurl() {
		return imageurl;
	}

	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}

	public ArrayList<Document> getResultitems() {
		return resultitems;
	}

	public void setResultitems(ArrayList<Document> resultitems) {
		this.resultitems = resultitems;
	}

	public String getStory() {
		return story;
	}

	public void setStory(String story) {
		this.story = story;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	

	public ArrayList<String> getEmbedhtmlofvideos() {
		return embedhtmlofvideos;
	}

	public void setEmbedhtmlofvideos(ArrayList<String> embedhtmlofvideos) {
		this.embedhtmlofvideos = embedhtmlofvideos;
	}

	public ArrayList<String> getEmbedhtmlofphotos() {
		return embedhtmlofphotos;
	}

	public void setEmbedhtmlofphotos(ArrayList<String> embedhtmlofphotos) {
		this.embedhtmlofphotos = embedhtmlofphotos;
	}

	
	public String GetLocalDateStringFromUTCString(String utcLongDateTime) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	    String localDateString = null;

	    long when = 0;
	    try {
	        when = dateFormat.parse(utcLongDateTime).getTime();
	    } catch (ParseException e) {
	        e.printStackTrace();
	    }
	    localDateString = dateFormat.format(new Date(when + TimeZone.getDefault().getRawOffset() + (TimeZone.getDefault().inDaylightTime(new Date()) ? TimeZone.getDefault().getDSTSavings() : 0)));

	    return localDateString;
	}

	
}
