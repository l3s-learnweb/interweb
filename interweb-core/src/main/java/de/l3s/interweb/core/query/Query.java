package de.l3s.interweb.core.query;

import static de.l3s.interweb.core.util.Assertions.notNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "query")
@XmlAccessorType(XmlAccessType.FIELD)
public class Query implements Serializable {
    @Serial
    private static final long serialVersionUID = 3955897587724588474L;

    @XmlAttribute(name = "id")
    private String id;
    @XmlAttribute(name = "link")
    private String link;

    @JsonbProperty("q")
    @XmlElement(name = "q")
    private String query;
    @JsonbProperty("date_from")
    @XmlElement(name = "date_from")
    private String dateFrom;
    @JsonbProperty("date_till")
    @XmlElement(name = "date_till")
    private String dateTill;
    @XmlElement(name = "language")
    private String language = "en";

    @JsonbProperty("services")
    @XmlElementWrapper(name = "services")
    @XmlElement(name = "service")
    private Set<String> services;
    @JsonbProperty("media_types")
    @XmlElementWrapper(name = "media_types")
    @XmlElement(name = "type")
    private Set<ContentType> contentTypes;
    @JsonbProperty("extras")
    @XmlElementWrapper(name = "extras")
    @XmlElement(name = "extra")
    private Set<SearchExtra> extras;

    @XmlElement(name = "page")
    private int page = 1;
    @JsonbProperty("per_page")
    @XmlElement(name = "per_page")
    private int perPage = 10;
    @JsonbProperty("search_in")
    @XmlElement(name = "search_in")
    private SearchScope searchScope = SearchScope.text;
    @XmlElement(name = "ranking")
    private SearchRanking ranking = SearchRanking.relevance;

    @XmlElement(name = "timeout")
    private int timeout = 30;

    public Query() {
    }

    public Query(String id, String query, Set<ContentType> contentTypes) {
        notNull(id, "id");
        notNull(query, "query");
        notNull(contentTypes, "contentTypes");

        this.id = id;
        this.query = query;
        this.services = new HashSet<>();
        this.contentTypes = contentTypes;
        this.extras = new HashSet<>();
    }

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

    public Set<String> getServices() {
        return services;
    }

    public void setServices(final Collection<String> services) {
        this.services = new HashSet<>(services);
    }

    public void addConnectorName(String connectorName) {
        services.add(connectorName);
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

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(final int timeout) {
        this.timeout = timeout;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Query query1 = (Query) o;
        return page == query1.page
            && perPage == query1.perPage
            && searchScope == query1.searchScope
            && ranking == query1.ranking
            && Objects.equals(query, query1.query)
            && Objects.equals(dateFrom, query1.dateFrom)
            && Objects.equals(dateTill, query1.dateTill)
            && Objects.equals(language, query1.language)
            && Objects.equals(services, query1.services)
            && Objects.equals(contentTypes, query1.contentTypes)
            && Objects.equals(extras, query1.extras);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, dateFrom, dateTill, language, services, contentTypes, searchScope, extras, page, perPage, ranking);
    }

    public int hashCodeWithoutPage() {
        return Objects.hash(query, dateFrom, dateTill, language, services, contentTypes, searchScope, extras, perPage, ranking);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("id", id)
            .append("link", link)
            .append("query", query)
            .append("dateFrom", dateFrom)
            .append("dateTill", dateTill)
            .append("language", language)
            .append("services", services)
            .append("contentTypes", contentTypes)
            .append("searchExtra", extras)
            .append("page", page)
            .append("perPage", perPage)
            .append("searchScopes", searchScope)
            .append("ranking", ranking)
            .append("timeout", timeout)
            .toString();
    }
}
