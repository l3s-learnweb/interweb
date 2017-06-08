package de.l3s.bingService.models;

import java.util.List;

import de.l3s.bingService.utils.UriUtils;

public class VideoHolder {

	private String id;

	private String readLink;

	private String webSearchUrl;

	private String webSearchUrlPingSuffix;

	private boolean isFamilyFriendly;
	
	private String scenario;

	private List<Video> value;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReadLink() {
		return readLink;
	}

	public void setReadLink(String readLink) {
		this.readLink = readLink;
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

	public boolean isFamilyFriendly() {
		return isFamilyFriendly;
	}

	public void setFamilyFriendly(boolean isFamilyFriendly) {
		this.isFamilyFriendly = isFamilyFriendly;
	}

	public List<Video> getValue() {
		return value;
	}

	public void setValue(List<Video> value) {
		this.value = value;
	}

	public String getScenario() {
		return scenario;
	}

	public void setScenario(String scenario) {
		this.scenario = scenario;
	}

}
