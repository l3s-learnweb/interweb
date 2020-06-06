package de.l3s.interwebj.core.query;

import static de.l3s.interwebj.core.util.Assertions.notNull;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement(name = "query")
@XmlAccessorType(XmlAccessType.FIELD)
public class Query implements Serializable {
    private static final long serialVersionUID = 3955897587724588474L;

    @XmlAttribute(name = "id")
    private String id;
    @XmlAttribute(name = "link")
    private String link;

    @XmlElement(name = "q")
    private String query;
    @XmlElement(name = "date_from")
    private String dateFrom;
    @XmlElement(name = "date_till")
    private String dateTill;
    @XmlElement(name = "language")
    private String language = "en";

    @JsonProperty("services")
    @XmlElementWrapper(name = "services")
    @XmlElement(name = "service")
    private Set<String> connectorNames;
    @JsonProperty("types")
    @XmlElementWrapper(name = "types")
    @XmlElement(name = "type")
    private Set<ContentType> contentTypes;
    @JsonProperty("search_in")
    @XmlElementWrapper(name = "search_in")
    @XmlElement(name = "scope")
    private Set<SearchScope> searchScopes;

    @XmlElement(name = "page")
    private int page = 1;
    @XmlElement(name = "per_page")
    private int perPage = 10;
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
        this.connectorNames = new HashSet<>();
        this.contentTypes = contentTypes;
        this.searchScopes = new HashSet<>();
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

    public Set<String> getConnectorNames() {
        return connectorNames;
    }

    public void setConnectorNames(final Set<String> connectorNames) {
        this.connectorNames = connectorNames;
    }

    public void addConnectorName(String connectorName) {
        connectorNames.add(connectorName);
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

    public Set<SearchScope> getSearchScopes() {
        return searchScopes;
    }

    public void setSearchScopes(final Set<SearchScope> searchScopes) {
        this.searchScopes = searchScopes;
    }

    public void addSearchScope(SearchScope searchScope) {
        searchScopes.add(searchScope);
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
            && ranking == query1.ranking
            && Objects.equals(query, query1.query)
            && Objects.equals(dateFrom, query1.dateFrom)
            && Objects.equals(dateTill, query1.dateTill)
            && Objects.equals(language, query1.language)
            && Objects.equals(connectorNames, query1.connectorNames)
            && Objects.equals(contentTypes, query1.contentTypes)
            && Objects.equals(searchScopes, query1.searchScopes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, dateFrom, dateTill, language, connectorNames, contentTypes, searchScopes, page, perPage, ranking);
    }

    public int hashCodeWithoutPage() {
        return Objects.hash(query, dateFrom, dateTill, language, connectorNames, contentTypes, searchScopes, perPage, ranking);
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
            .append("connectorNames", connectorNames)
            .append("contentTypes", contentTypes)
            .append("searchScopes", searchScopes)
            .append("page", page)
            .append("perPage", perPage)
            .append("ranking", ranking)
            .append("timeout", timeout)
            .toString();
    }
}
