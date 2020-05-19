package de.l3s.interwebj.tomcat.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.l3s.interwebj.core.query.QueryResult;
import de.l3s.interwebj.core.query.ResultItem;

@XmlRootElement(name = "rsp")
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchResponse extends XMLResponse {

    @XmlElement(name = "query")
    protected SearchQueryEntity query;

    public SearchResponse() {
    }

    public SearchResponse(QueryResult queryResult) {
        SearchQueryEntity searchQueryEntity = new SearchQueryEntity(queryResult.getQuery());
        searchQueryEntity.setElapsedTime(String.valueOf(queryResult.getElapsedTime()));
        searchQueryEntity.setTotalResults(queryResult.getTotalResultCount());
        searchQueryEntity.setFacetSources(queryResult.getFacetResults());

        for (ResultItem resultItem : queryResult.getResultItems()) {
            SearchResultEntity iwSearchResult = new SearchResultEntity(resultItem);
            searchQueryEntity.addResult(iwSearchResult);
        }

        query = searchQueryEntity;
    }

    public SearchQueryEntity getQuery() {
        return query;
    }

    public void setQuery(SearchQueryEntity query) {
        this.query = query;
    }
}
