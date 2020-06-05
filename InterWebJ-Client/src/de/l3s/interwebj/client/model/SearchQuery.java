package de.l3s.interwebj.client.model;

import java.io.Serializable;
import java.util.List;
import java.util.StringJoiner;

import com.google.gson.annotations.SerializedName;

public class SearchQuery implements Serializable {
    private static final long serialVersionUID = 1982483611243356485L;

    @SerializedName("id")
    private String id;
    @SerializedName("link")
    private String link;

    @SerializedName("q")
    private String query;
    @SerializedName("date_from")
    private String dateFrom;
    @SerializedName("date_till")
    private String dateTill;
    @SerializedName("language")
    private String language;

    @SerializedName("services")
    private List<String> services;
    @SerializedName("types")
    private List<String> contentTypes;
    @SerializedName("search_in")
    private List<String> searchScopes;

    @SerializedName("page")
    private Integer page;
    @SerializedName("per_page")
    private Integer perPage;
    @SerializedName("ranking")
    private String ranking;

    @SerializedName("timeout")
    private Integer timeout;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(final String link) {
        this.link = link;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(final String query) {
        this.query = query;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(final String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTill() {
        return dateTill;
    }

    public void setDateTill(final String dateTill) {
        this.dateTill = dateTill;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(final List<String> services) {
        this.services = services;
    }

    public List<String> getContentTypes() {
        return contentTypes;
    }

    public void setContentTypes(final List<String> contentTypes) {
        this.contentTypes = contentTypes;
    }

    public List<String> getSearchScopes() {
        return searchScopes;
    }

    public void setSearchScopes(final List<String> searchScopes) {
        this.searchScopes = searchScopes;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(final Integer page) {
        this.page = page;
    }

    public Integer getPerPage() {
        return perPage;
    }

    public void setPerPage(final Integer perPage) {
        this.perPage = perPage;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(final String ranking) {
        this.ranking = ranking;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(final Integer timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SearchQuery.class.getSimpleName() + "[", "]")
            .add("id='" + id + "'")
            .add("link='" + link + "'")
            .add("queryString='" + query + "'")
            .add("dateFrom='" + dateFrom + "'")
            .add("dateTill='" + dateTill + "'")
            .add("language='" + language + "'")
            .add("connectorNames=" + services)
            .add("contentTypes=" + contentTypes)
            .add("searchScopes=" + searchScopes)
            .add("page=" + page)
            .add("perPage=" + perPage)
            .add("ranking='" + ranking + "'")
            .add("timeout=" + timeout)
            .toString();
    }
}
