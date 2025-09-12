package de.l3s.interweb.connector.google.serper;

import java.util.List;

public class SearchResponse {
    private SearchRequest searchParameters;
    private KnowledgeGraph knowledgeGraph;
    private List<OrganicResult> organic;
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
