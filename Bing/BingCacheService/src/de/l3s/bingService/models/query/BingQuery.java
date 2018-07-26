package de.l3s.bingService.models.query;

import de.l3s.bingService.models.Entity;

public class BingQuery extends Entity {

	private String query;
	private String market;
	private int count = 50;
	private int offset = 0;
	private String language;
	private SafesearchParam safesearch;
	private FreshnessParam freshness;

	private ResponseFilterParam responseFilter;

	public BingQuery() {
		super();
	}

	public BingQuery(String query, String mkt, String lang, int offset, String freshness, String safeSearch) {
		this.query = query;

		this.market = mkt;

		if (offset > 0) {
			this.offset = offset;
		}

		this.language = lang;

		if (safeSearch != null) {
			try {
				safesearch = SafesearchParam.valueOf(safeSearch);
			} catch (IllegalArgumentException e) {
				safesearch = null;
			}

		} else {
			safesearch = null;
		}

		if (freshness != null) {
			try {
				this.freshness = FreshnessParam.valueOf(freshness);
			} catch (IllegalArgumentException e) {
				this.freshness = null;
			}

		} else {
			this.freshness = null;
		}

	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query.replaceAll("_", "+");
	}

	public boolean hasMarket() {
		return market != null;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String mkt) {
		this.market = mkt;
	}

	public boolean hasSafesearch() {
		return safesearch != null;
	}

	public SafesearchParam getSafesearch() {
		return safesearch;
	}

	public void setSafesearch(SafesearchParam safesearch) {
		this.safesearch = safesearch;
	}

	public boolean hasFreshness() {
		return freshness != null;
	}

	public FreshnessParam getFreshness() {
		return freshness;
	}

	public void setFreshness(FreshnessParam freshness) {
		this.freshness = freshness;
	}

	public ResponseFilterParam getResponseFilter() {
		return responseFilter;
	}

	public boolean hasResponseFilter() {
		return responseFilter != null;
	}

	public void setResponseFilter(ResponseFilterParam responseFilter) {
		this.responseFilter = responseFilter;
	}

	public boolean hasLanguage() {
		return language != null;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

}
