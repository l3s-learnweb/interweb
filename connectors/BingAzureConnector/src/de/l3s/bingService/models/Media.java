package de.l3s.bingService.models;

import de.l3s.bingService.utils.UriUtils;

public class Media {

	private String name;

	private String webSearchUrl;

	private String webSearchUrlPingSuffix;

	private String thumbnailUrl;

	private String datePublished;

	private String contentUrl;

	private String hostPageUrl;

	private String hostPageUrlPingSuffix;

	private String encodingFormat;

	private String hostPageDisplayUrl;

	private String width;

	private String height;

	private Thumbnail thumbnail;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWebSearchUrl() {
		return webSearchUrl;
	}

	public void setWebSearchUrl(String webSearchUrl) {
		this.webSearchUrl = UriUtils.splitQuery(webSearchUrl);
	}

	public String getWebSearchUrlPingSuffix() {
		return webSearchUrlPingSuffix;
	}

	public void setWebSearchUrlPingSuffix(String webSearchUrlPingSuffix) {
		this.webSearchUrlPingSuffix = webSearchUrlPingSuffix;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getDatePublished() {
		return datePublished;
	}

	public void setDatePublished(String datePublished) {
		this.datePublished = datePublished;
	}

	public String getContentUrl() {
		return contentUrl;
	}

	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
	}

	public String getHostPageUrl() {
		return hostPageUrl;
	}

	public void setHostPageUrl(String hostPageUrl) {
		this.hostPageUrl = UriUtils.splitQuery(hostPageUrl);
	}

	public String getHostPageUrlPingSuffix() {
		return hostPageUrlPingSuffix;
	}

	public void setHostPageUrlPingSuffix(String hostPageUrlPingSuffix) {
		this.hostPageUrlPingSuffix = hostPageUrlPingSuffix;
	}

	public String getEncodingFormat() {
		return encodingFormat;
	}

	public void setEncodingFormat(String encodingFormat) {
		this.encodingFormat = encodingFormat;
	}

	public String getHostPageDisplayUrl() {
		return hostPageDisplayUrl;
	}

	public void setHostPageDisplayUrl(String hostPageDisplayUrl) {
		this.hostPageDisplayUrl = hostPageDisplayUrl;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public Thumbnail getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(Thumbnail thumbnail) {
		this.thumbnail = thumbnail;
	}

	@Override
	public String toString() {
		return "Media [name=" + name + ", webSearchUrl=" + webSearchUrl + ", webSearchUrlPingSuffix="
				+ webSearchUrlPingSuffix + ", thumbnailUrl=" + thumbnailUrl + ", datePublished=" + datePublished
				+ ", contentUrl=" + contentUrl + ", hostPageUrl=" + hostPageUrl + ", hostPageUrlPingSuffix="
				+ hostPageUrlPingSuffix + ", encodingFormat=" + encodingFormat + ", hostPageDisplayUrl="
				+ hostPageDisplayUrl + ", width=" + width + ", height=" + height + ", thumbnail=" + thumbnail + "]";
	}
	
	

}
