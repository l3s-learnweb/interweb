package de.l3s.interweb.core.search;

import static de.l3s.interweb.core.util.Assertions.notNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.Query;

public class SearchQuery extends Query {
    private String id;

    @NotEmpty
    @JsonProperty("q")
    private String query;
    @JsonProperty("date_from")
    private LocalDate dateFrom;
    @JsonProperty("date_till")
    private LocalDate dateTill;
    @Size(min = 2, max = 2)
    private String language = "en";

    @NotEmpty
    @JsonProperty("media_types")
    private Set<ContentType> contentTypes = new HashSet<>();
    private Set<SearchExtra> extras = new HashSet<>();

    @Min(1)
    @Max(100)
    private int page = 1;
    @Min(1)
    @Max(500)
    @JsonProperty("per_page")
    private int perPage = 10;
    @NotNull
    @JsonProperty("search_scope")
    private SearchScope searchScope = SearchScope.text;
    @NotNull
    private SearchRanking ranking = SearchRanking.relevance;

    public SearchQuery() {
    }

    public SearchQuery(String id, String query, Set<ContentType> contentTypes) {
        notNull(id, "id");
        notNull(query, "query");
        notNull(contentTypes, "contentTypes");

        this.id = id;
        this.query = query;
        this.contentTypes = contentTypes;
        this.extras = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(final String query) {
        this.query = query;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(final LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDate getDateTill() {
        return dateTill;
    }

    public void setDateTill(final LocalDate dateTill) {
        this.dateTill = dateTill;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public Set<ContentType> getContentTypes() {
        return contentTypes;
    }

    public void setContentTypes(final Set<ContentType> contentTypes) {
        this.contentTypes = contentTypes;
    }

    public void addContentType(ContentType contentType) {
        contentTypes.add(contentType);
    }

    public SearchScope getSearchScope() {
        return searchScope;
    }

    public void setSearchScope(final SearchScope searchScope) {
        this.searchScope = searchScope;
    }

    public Set<SearchExtra> getExtras() {
        return extras;
    }

    public void setExtras(final Set<SearchExtra> extras) {
        this.extras = extras;
    }

    public void addSearchExtra(SearchExtra part) {
        extras.add(part);
    }

    public int getPage() {
        return page;
    }

    public void setPage(final int page) {
        this.page = Math.max(page, 1);
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(final int perPage) {
        this.perPage = perPage;
    }

    public SearchRanking getRanking() {
        return ranking;
    }

    public void setRanking(final SearchRanking ranking) {
        this.ranking = ranking;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SearchQuery query1 = (SearchQuery) o;
        return page == query1.page
            && perPage == query1.perPage
            && searchScope == query1.searchScope
            && ranking == query1.ranking
            && Objects.equals(query, query1.query)
            && Objects.equals(dateFrom, query1.dateFrom)
            && Objects.equals(dateTill, query1.dateTill)
            && Objects.equals(language, query1.language)
            && Objects.equals(contentTypes, query1.contentTypes)
            && Objects.equals(extras, query1.extras);
    }


    @Override
    public int hashCode() {
        return Objects.hash(query, dateFrom, dateTill, language, getServices(), contentTypes, searchScope, extras, page, perPage, ranking);
    }

    public int hashCodeWithoutPage() {
        return Objects.hash(query, dateFrom, dateTill, language, getServices(), contentTypes, searchScope, extras, perPage, ranking);
    }
}
