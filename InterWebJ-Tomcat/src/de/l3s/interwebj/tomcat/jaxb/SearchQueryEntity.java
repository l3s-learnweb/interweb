package de.l3s.interwebj.tomcat.jaxb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;

import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.util.CoreUtils;

@XmlRootElement(name = "query")
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchQueryEntity {

    @XmlAttribute(name = "id")
    protected String id;
    @XmlAttribute(name = "link")
    protected String link;
    @XmlElement(name = "user")
    protected String user;
    @XmlElement(name = "query_string")
    protected String queryString;
    @XmlElement(name = "search_in")
    protected String searchIn;
    @XmlElement(name = "media_types")
    protected String mediaTypes;
    @XmlElement(name = "date_from")
    protected String dateFrom;
    @XmlElement(name = "date_till")
    protected String dateTill;
    @XmlElement(name = "ranking")
    protected String ranking;
    @XmlElement(name = "number_of_results")
    protected int numberOfResults;
    @XmlElement(name = "updated")
    protected String updated;
    @XmlElement(name = "elapsed_time")
    protected String elapsedTime;
    @XmlElement(name = "total_results")
    protected Long totalResults;
    @XmlElement(name = "facet_sources")
    protected Map<String, Long> facetSources;
    @XmlElementWrapper(name = "results")
    @XmlElement(name = "result")
    protected List<SearchResultEntity> results;

    public SearchQueryEntity() {
        results = new ArrayList<SearchResultEntity>();
    }

    public SearchQueryEntity(Query query) {
        this();
        setId(query.getId());
        setLink(query.getLink());
        setQueryString(query.getQuery());
        String searchScopes = StringUtils.join(query.getSearchScopes(), ',');
        setSearchIn(StringUtils.lowerCase(searchScopes));
        String mediaTypes = StringUtils.join(query.getContentTypes(), ',');
        setMediaTypes(StringUtils.lowerCase(mediaTypes));
        setDateFrom(query.getParam("date_from"));
        setDateTill(query.getParam("date_till"));
        setRanking(query.getSortOrder().getName());
        setNumberOfResults(query.getResultCount());
        setUpdated(CoreUtils.formatDate(query.getUpdated()));
    }

    public void addResult(SearchResultEntity result) {
        results.add(result);
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

    public String getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(String elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

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

    public String getMediaTypes() {
        return mediaTypes;
    }

    public void setMediaTypes(String mediaTypes) {
        this.mediaTypes = mediaTypes;
    }

    public int getNumberOfResults() {
        return numberOfResults;
    }

    public void setNumberOfResults(int numberOfResults) {
        this.numberOfResults = numberOfResults;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public List<SearchResultEntity> getResults() {
        return results;
    }

    public void setResults(List<SearchResultEntity> results) {
        this.results = results;
    }

    public Long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Long totalResults) {
        this.totalResults = totalResults;
    }

    public Map<String, Long> getFacetSources() {
        return facetSources;
    }

    public void setFacetSources(Map<String, Long> facetSources) {
        this.facetSources = facetSources;
    }

    public String getSearchIn() {
        return searchIn;
    }

    public void setSearchIn(String searchIn) {
        this.searchIn = searchIn;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
