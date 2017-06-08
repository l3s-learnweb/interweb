package de.l3s.bingService.models;

import java.util.List;

import de.l3s.bingService.utils.UriUtils;

public class ImageHolder {

	private String id;

	private String readLink;

	private String webSearchUrl;

	private String webSearchUrlPingSuffix;

	private Boolean isFamilyFriendly;

	private List<Image> values;

	private Boolean displayShoppingSourcesBadges;

	private Boolean displayRecipeSourcesBadges;

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

	public Boolean getIsFamilyFriendly() {
		return isFamilyFriendly;
	}

	public void setIsFamilyFriendly(Boolean isFamilyFriendly) {
		this.isFamilyFriendly = isFamilyFriendly;
	}

	public List<Image> getValue() {
		return values;
	}

	public void setValue(List<Image> values) {
		this.values = values;
	}

	public Boolean getDisplayShoppingSourcesBadges() {
		return displayShoppingSourcesBadges;
	}

	public void setDisplayShoppingSourcesBadges(Boolean displayShoppingSourcesBadges) {
		this.displayShoppingSourcesBadges = displayShoppingSourcesBadges;
	}

	public Boolean getDisplayRecipeSourcesBadges() {
		return displayRecipeSourcesBadges;
	}

	public void setDisplayRecipeSourcesBadges(Boolean displayRecipeSourcesBadges) {
		this.displayRecipeSourcesBadges = displayRecipeSourcesBadges;
	}

}
