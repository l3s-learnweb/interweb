package de.l3s.bingService.models.query;

import java.util.List;

public class BingQuery {

    private String language;

    private String query;

    private int count = 10;
    private int offset = 0;

    private String market;

    private SafesearchParam safesearch;

    private String freshness;

    private List<ResponseFilterParam> responseFilter;

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

    public String getFreshness() {
        return freshness;
    }

    public void setFreshness(String freshness) {
        this.freshness = freshness;
    }

    public List<ResponseFilterParam> getResponseFilter() {
        return responseFilter;
    }

    public void setResponseFilter(List<ResponseFilterParam> responseFilter) {
        this.responseFilter = responseFilter;
    }

    public boolean hasResponseFilter() {
        return responseFilter != null && !responseFilter.isEmpty();
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
