package de.l3s.interweb.connector.google.serper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class SearchResponse {
    private SearchRequest searchParameters;
    private KnowledgeGraph knowledgeGraph;
    private List<OrganicResult> organic;
    private List<VideoResult> videos;
    private List<ImageResult> images;
    private List<NewsResult> news;
    private List<PeopleAlsoAsk> peopleAlsoAsk;
    private List<RelatedSearch> relatedSearches;
    private Integer credits;

    public SearchRequest getSearchParameters() {
        return searchParameters;
    }

    public void setSearchParameters(SearchRequest searchParameters) {
        this.searchParameters = searchParameters;
    }

    public KnowledgeGraph getKnowledgeGraph() {
        return knowledgeGraph;
    }

    public void setKnowledgeGraph(KnowledgeGraph knowledgeGraph) {
        this.knowledgeGraph = knowledgeGraph;
    }

    public List<OrganicResult> getOrganic() {
        return organic;
    }

    public void setOrganic(List<OrganicResult> organic) {
        this.organic = organic;
    }

    public List<VideoResult> getVideos() {
        return videos;
    }

    public void setVideos(List<VideoResult> videos) {
        this.videos = videos;
    }

    public List<ImageResult> getImages() {
        return images;
    }

    public void setImages(List<ImageResult> images) {
        this.images = images;
    }

    public List<NewsResult> getNews() {
        return news;
    }

    public void setNews(List<NewsResult> news) {
        this.news = news;
    }

    public List<PeopleAlsoAsk> getPeopleAlsoAsk() {
        return peopleAlsoAsk;
    }

    public void setPeopleAlsoAsk(List<PeopleAlsoAsk> peopleAlsoAsk) {
        this.peopleAlsoAsk = peopleAlsoAsk;
    }

    public List<RelatedSearch> getRelatedSearches() {
        return relatedSearches;
    }

    public void setRelatedSearches(List<RelatedSearch> relatedSearches) {
        this.relatedSearches = relatedSearches;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public Integer getCredits() {
        return credits;
    }
}
