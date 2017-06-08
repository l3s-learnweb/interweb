package de.l3s.bingService.models;

import java.util.List;

import de.l3s.bingService.utils.UriUtils;

public class WebPagesMainHolder {

	private String webSearchUrl;

	private String webSearchUrlPingSuffix;

	private String totalEstimatedMatches;

	private List<WebPage> value;

	public String getWebSearchUrl() {
		return webSearchUrl;
	}

	public void setWebSearchUrl(String webSearchUrl) {
		this.webSearchUrl =  UriUtils.splitQuery(webSearchUrl);
	}

	public String getWebSearchUrlPingSuffix() {
		return webSearchUrlPingSuffix;
	}

	public void setWebSearchUrlPingSuffix(String webSearchUrlPingSuffix) {
		this.webSearchUrlPingSuffix = webSearchUrlPingSuffix;
	}

	public String getTotalEstimatedMatches() {
		return totalEstimatedMatches;
	}

	public void setTotalEstimatedMatches(String totalEstimatedMatches) {
		this.totalEstimatedMatches = totalEstimatedMatches;
	}

	public List<WebPage> getValue() {
		return value;
	}

	public void setValue(List<WebPage> value) {
		this.value = value;
	}
}
