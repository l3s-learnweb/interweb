package de.l3s.interwebj.query;


import java.io.*;
import java.util.*;

import org.apache.commons.lang.*;


public class ResultItem
    implements Serializable
{
	
	private static final int MAX_TITLE_LENGTH = 256;
	private static final int MAX_DESCRIPTION_LENGTH = 1024;
	public static final int DEFAULT_EMBEDDED_WIDTH = 500;
	public static final int DEFAULT_EMBEDDED_HEIGHT = 375;
	
	private static final long serialVersionUID = 9111067008513145675L;
	
	private String id;
	private String title;
	private String type;
	private String description;
	private String connectorName;
	private String serviceName;
	private String tags;
	private String url;
	private Set<Thumbnail> thumbnails;
	private String date;
	private int rank = -1;
	private long totalResultCount = -1;
	private int viewCount = -1;
	private int commentCount = -1;
	private double privacy;
	private int privacyConfidence;
	private int duration;
	private String snippet;
	private String embeddedSize1;
	private String embeddedSize2;
	private String embeddedSize3;
	private String embeddedSize4;
	
	private String imageUrl;

	public ResultItem(String connectorName)
	{
		this.connectorName = connectorName;
		serviceName = connectorName;
		thumbnails = new TreeSet<Thumbnail>();
	}
	

	public int getCommentCount()
	{
		return commentCount;
	}
	

	public String getConnectorName()
	{
		return connectorName;
	}
	

	public String getDate()
	{
		return date;
	}
	

	public String getDescription()
	{
		return description;
	}
	
	public String getId()
	{
		return id;
	}
	

	public int getRank()
	{
		return rank;
	}
	

	public String getServiceName()
	{
		return serviceName;
	}
	

	public String getTags()
	{
		return tags;
	}
	
	public String getSnippet(){
		return snippet;
	}

	public Thumbnail getThumbnail(int maxWidth, int maxHeight)
	{
		Set<Thumbnail> thumbnails = getThumbnails();
		if (thumbnails == null)
		{
			return null;
		}
		Thumbnail thumbnail = null;
		for (Thumbnail t : thumbnails)
		{
			if (thumbnail == null || t.getWidth() <= maxWidth
			    && t.getHeight() <= maxHeight)
			{
				thumbnail = t;
			}
		}
		return thumbnail;
	}
	

	public Set<Thumbnail> getThumbnails()
	{
		return thumbnails;
	}
	

	public String getTitle()
	{
		return title;
	}
	

	public long getTotalResultCount()
	{
		return totalResultCount;
	}
	

	public String getType()
	{
		return type;
	}
	

	public String getUrl()
	{
		return url;
	}
	

	public int getViewCount()
	{
		return viewCount;
	}
	

	public void setCommentCount(int commentCount)
	{
		this.commentCount = commentCount;
	}
	

	public void setDate(String date)
	{
		this.date = date;
	}
	

	public void setDescription(String description)
	{
		if (description == null)
		{
			return;
		}
		description = description.trim();
		description = unescape(description);
		if (description.length() > MAX_DESCRIPTION_LENGTH)
		{
			int cutIndex = description.lastIndexOf(' ', MAX_DESCRIPTION_LENGTH);
			if (cutIndex == -1)
			{
				cutIndex = MAX_DESCRIPTION_LENGTH;
			}
			description = description.substring(0, cutIndex) + "...";
		}
		this.description = description;
	}
	
	public void setSnippet(String snippet){
		if(snippet==null){
			return;
		}
		
		this.snippet=snippet;
		
	}

	/**
	 * html code, could be flash
	 * max width and max height 500px
	 * @param embedded
	 */
	public void setEmbeddedSize3(String embedded)
	{
		this.embeddedSize3 = embedded;
	}	

	/**
	 * html code, only image or text
	 * max width and max height 100px
	 * @return
	 */
	public String getEmbeddedSize1() {
		return embeddedSize1;
	}

	/**
	 * html code, only image or text
	 * max width and max height 100px
	 */
	public void setEmbeddedSize1(String embeddedSize1) {
		this.embeddedSize1 = embeddedSize1;
	}

	/**
	 * html code, only image or text
	 * max width and max height 240px
	 */
	public String getEmbeddedSize2() {
		return embeddedSize2;
	}

	/**
	 * html code, only image or text
	 * max width and max height 240px
	 */
	public void setEmbeddedSize2(String embeddedSize2) {
		this.embeddedSize2 = embeddedSize2;
	}

	/**
	 * html code, could be flash
	 * max width and max height 500px
	 * @return
	 */
	public String getEmbeddedSize3()
	{
		return embeddedSize3;
	}
	
	/**
	 * html code, could be flash
	 * max width and max height 100%
	 */
	public String getEmbeddedSize4() {
		return embeddedSize4;
	}

	/**
	 * html code, could be flash
	 * max width and max height 100%
	 */
	public void setEmbeddedSize4(String embeddedSize4) {
		this.embeddedSize4 = embeddedSize4;
	}


	/**
	 * Url to the best (high resolution) available preview image
	 * @return
	 */
	public String getImageUrl() {
		return imageUrl;
	}

	/**
	 * Url to the best (high resolution) available preview image
	 * @param imageUrl
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}


	public void setId(String id)
	{
		this.id = id;
	}
	

	public void setRank(int rank)
	{
		this.rank = rank;
	}
	

	public void setServiceName(String serviceName)
	{
		this.serviceName = serviceName;
	}
	

	public void setTags(String tags)
	{
		this.tags = tags;
	}
	

	public void setThumbnails(Set<Thumbnail> thumbnails)
	{
		this.thumbnails = thumbnails;
	}
	

	public void setTitle(String title)
	{
		if (title == null)
		{
			return;
		}
		title = title.trim();
		title = unescape(title);
		if (title.length() > MAX_TITLE_LENGTH)
		{
			int cutIndex = title.lastIndexOf(' ', MAX_TITLE_LENGTH);
			if (cutIndex == -1)
			{
				cutIndex = MAX_TITLE_LENGTH;
			}
			title = title.substring(0, cutIndex) + "...";
		}
		this.title = title;
	}
	

	public void setTotalResultCount(long totalResultCount)
	{
		this.totalResultCount = totalResultCount;
	}
	

	public void setType(String type)
	{
		this.type = type;
	}
	

	public void setUrl(String url)
	{
		this.url = url;
	}
	

	public void setViewCount(int viewCount)
	{
		this.viewCount = viewCount;
	}
	
	
	public double getPrivacy() {
		return privacy;
	}


	public void setPrivacy(double privacy) {
		this.privacy = privacy;
	}


	public int getPrivacyConfidence() {
		return privacyConfidence;
	}


	public void setPrivacyConfidence(int privacyConfidence) {
		this.privacyConfidence = privacyConfidence;
	}


	private String unescape(String encoded)
	{
		if (encoded == null)
		{
			return null;
		}
		String decoded;
		while (!(decoded = StringEscapeUtils.unescapeHtml(encoded)).equals(encoded))
		{
			encoded = decoded;
		}
		return decoded;
	}


	public int getDuration() {
		return duration;
	}


	public void setDuration(int duration) {
		this.duration = duration;
	}
	
}
