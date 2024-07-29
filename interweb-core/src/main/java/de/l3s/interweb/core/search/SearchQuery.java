package de.l3s.interweb.core.search;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.validation.constraints.*;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import de.l3s.interweb.core.Query;

@RegisterForReflection
@JsonPropertyOrder({"id", "query", "services", "content_types", "extras", "date_from", "date_to", "page", "per_page", "lang", "sort", "timeout"})
@JsonIgnoreProperties(ignoreUnknown = false)
public class SearchQuery extends Query {
    private String id;

    @NotEmpty
    private String query;
    @JsonProperty("date_from")
    private LocalDate dateFrom;
    @JsonProperty("date_to")
    private LocalDate dateTo;
    @Size(min = 2, max = 2)
    @JsonProperty("lang")
    private String language = "en";

    @NotEmpty
    @JsonProperty("content_types")
    private Set<ContentType> contentTypes = new HashSet<>();
    @JsonProperty("extras")
    private Set<SearchExtra> extras = new HashSet<>();

    @Min(1)
    @Max(100)
    private Integer page;
    @Min(1)
    @Max(500)
    @JsonProperty("per_page")
    private Integer perPage;
    @NotNull
    private SearchSort sort = SearchSort.relevance;

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

    public LocalDate getDateTo() {
        return dateTo;
    }

    public void setDateTo(final LocalDate dateTo) {
        this.dateTo = dateTo;
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

    @JsonIgnore
    public void setContentTypes(ContentType... contentTypes) {
        if (contentTypes == null) this.contentTypes = new HashSet<>();
        else this.contentTypes = Set.of(contentTypes);
    }

    public Set<SearchExtra> getExtras() {
        return extras;
    }

    public void setExtras(final Set<SearchExtra> extras) {
        this.extras = extras;
    }

    @JsonIgnore
    public void setExtras(final SearchExtra... extras) {
        if (extras == null) this.extras = new HashSet<>();
        else this.extras = Set.of(extras);
    }

    public int getPage() {
        if (page == null) return 1;
        return page;
    }

    public void setPage(final Integer page) {
        this.page = page;
    }

    /**
     * @return a valid perPage value, but no more than fallback value or fallback value if perPage is null
     */
    public int getPerPage(int fallback) {
        return perPage == null ? fallback : Math.min(perPage, fallback);
    }

    public Integer getPerPage() {
        return perPage;
    }

    /**
     * @param perPage a desired number of results per page, the actual number of results per page may be less depending on the service.
     *                Prefer to use bigger values if you need second page, as it reduces API quotas usage. No value fallbacks to max value per service.
     */
    public void setPerPage(final Integer perPage) {
        this.perPage = perPage;
    }

    @JsonIgnore
    public int getOffset() {
        if (page == null || perPage == null) {
            return 0;
        }

        return (page - 1) * perPage;
    }

    public int getOffset(int fallbackPerPage) {
        if (page == null || page <= 1) {
            return 0;
        }

        return (page - 1) * getPerPage(fallbackPerPage);
    }

    public SearchSort getSort() {
        return sort;
    }

    public void setSort(final SearchSort sort) {
        this.sort = Objects.requireNonNullElse(sort, SearchSort.relevance);
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
        return Objects.equals(page, query1.page)
            && Objects.equals(perPage, query1.perPage)
            && Objects.equals(sort, query1.sort)
            && Objects.equals(query, query1.query)
            && Objects.equals(dateFrom, query1.dateFrom)
            && Objects.equals(dateTo, query1.dateTo)
            && Objects.equals(language, query1.language)
            && Objects.equals(contentTypes, query1.contentTypes)
            && Objects.equals(extras, query1.extras);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, dateFrom, dateTo, language, contentTypes, extras, page, perPage, sort, getServices());
    }

    public int hashCodeWithoutPage() {
        return Objects.hash(query, dateFrom, dateTo, language, contentTypes, extras, perPage, sort, getServices());
    }
}
