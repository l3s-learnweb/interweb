package de.l3s.interwebj.clientjson.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import com.google.gson.annotations.SerializedName;

public class SearchQuery implements Serializable {
    private static final long serialVersionUID = 1982483611243356485L;

    @SerializedName("id")
    private String id;
    @SerializedName("link")
    private String link;
    @SerializedName("user")
    private String user;
    @SerializedName("query_string")
    private String queryString;
    @SerializedName("search_in")
    private String searchIn;
    @SerializedName("media_types")
    private String mediaTypes;
    @SerializedName("date_from")
    private String dateFrom;
    @SerializedName("date_till")
    private String dateTill;
    @SerializedName("ranking")
    private String ranking;
    @SerializedName("number_of_results")
    private Integer numberOfResults;
    @SerializedName("updated")
    private String updated;
    @SerializedName("elapsed_time")
    private String elapsedTime;
    @SerializedName("total_results")
    private Integer totalResults;
    @SerializedName("facet_sources")
    private Map<String, Integer> facetSources;
    @SerializedName("result")
    private List<SearchResult> results = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getSearchIn() {
        return searchIn;
    }

    public void setSearchIn(String searchIn) {
        this.searchIn = searchIn;
    }

    public String getMediaTypes() {
        return mediaTypes;
    }

    public void setMediaTypes(String mediaTypes) {
        this.mediaTypes = mediaTypes;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTill() {
        return dateTill;
    }

    public void setDateTill(String dateTill) {
        this.dateTill = dateTill;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public Integer getNumberOfResults() {
        return numberOfResults;
    }

    public void setNumberOfResults(Integer numberOfResults) {
        this.numberOfResults = numberOfResults;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(String elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public Map<String, Integer> getFacetSources() {
        return facetSources;
    }

    public void setFacetSources(Map<String, Integer> facetSources) {
        this.facetSources = facetSources;
    }

    public List<SearchResult> getResults() {
        return results;
    }

    public void setResults(List<SearchResult> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SearchQuery.class.getSimpleName() + "[", "]")
            .add("id='" + id + "'")
            .add("link='" + link + "'")
            .add("user='" + user + "'")
            .add("queryString='" + queryString + "'")
            .add("searchIn='" + searchIn + "'")
            .add("mediaTypes='" + mediaTypes + "'")
            .add("dateFrom='" + dateFrom + "'")
            .add("dateTill='" + dateTill + "'")
            .add("ranking='" + ranking + "'")
            .add("numberOfResults=" + numberOfResults)
            .add("updated='" + updated + "'")
            .add("elapsedTime='" + elapsedTime + "'")
            .add("totalResults=" + totalResults)
            .add("facetSources=" + facetSources)
            .add("results=" + results)
            .toString();
    }
}
