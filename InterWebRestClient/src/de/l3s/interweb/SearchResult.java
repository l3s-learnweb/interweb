package de.l3s.interweb;

import java.io.Serializable;
import java.net.URL;

/** This is just a container class */
public class SearchResult implements Serializable
{
	private static final long serialVersionUID = -7316636719057626210L;

	/* fields have package scope and no setters for better performance */
	protected String serviceName; /** Interweb.BLOGGER ... Interweb.YOUTUBE */
	protected String idAtService;
	protected String type;
	protected String title;
	protected String description;
	protected URL url;
	protected URL imageURL;
	protected String date;
	protected String tags;
/*
	public String getAddUrl()
	{
		
		return "/resources/add?url="+Base64.encode(url.toString())
		+"&title="+Base64.encode(title)+"&source="+Base64.encode(""+service);
	
	}*/
	public String getServiceName() {
		return serviceName;
	}
	
	public String getIdAtService() {
		return idAtService;
	}

	public String getType() {
		return type;
	}

	public String getTitle() {
		return title==null||title.trim().length()==0?"-----------------":title;
	}

	public String getDescription() {
		return description;
	}

	public URL getUrl() {
		return url;
	}
	
	public URL getImageURL() {
		return imageURL;
	}
		
	public String getDate() {
		return date;
	}
	
	public String getTags() {
		return tags;
	}
}
