package de.l3s.interwebj.core.query;

import static de.l3s.interwebj.core.util.Assertions.notNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Query implements Serializable {
    public static final int DEFAULT_RESULT_COUNT = 10;
    public static final String CT_TEXT = "text";
    public static final String CT_VIDEO = "video";
    public static final String CT_IMAGE = "image";
    public static final String CT_AUDIO = "audio";
    public static final String CT_PRESENTATION = "presentation";
    public static final String CT_FRIEND = "friend";
    private static final Logger log = LogManager.getLogger(Query.class);
    private static final long serialVersionUID = 3955897587724588474L;
    private String id;
    private String link;
    private String query;
    private List<String> connectorNames;
    private List<String> contentTypes;
    private int resultCount;
    private int page = 1;
    private String language = "en";
    private float privacy = -1f;
    private boolean privacyUseImageFeatures = false;
    private long updated;
    private SortOrder sortOrder;
    private Set<SearchScope> searchScopes;
    private Map<String, String> params;
    private int timeout = 30;

    public Query(String id, String query, List<String> contentTypes, Map<String, String> params) {
        notNull(id, "id");
        notNull(query, "query");
        notNull(contentTypes, "contentTypes");
        notNull(params, "params");

        this.id = id;
        this.query = query;
        connectorNames = new ArrayList<String>();
        this.contentTypes = contentTypes;
        this.params = params;
        resultCount = DEFAULT_RESULT_COUNT;
        sortOrder = SortOrder.RELEVANCE;
        searchScopes = new HashSet<Query.SearchScope>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Query query1 = (Query) o;
        return resultCount == query1.resultCount
            && page == query1.page
            && query.equalsIgnoreCase(query1.query)
            && Objects.equals(contentTypes, query1.contentTypes)
            && Objects.equals(language, query1.language);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query.toLowerCase(), connectorNames, contentTypes, resultCount, page, language);
    }

    public void addConnectorName(String connectorName) {
        connectorNames.add(connectorName);
    }

    public void addContentType(String contentType) {
        contentTypes.add(contentType);
    }

    public void addParam(String key, String value) {
        params.put(key, value);
    }

    public void addSearchScope(SearchScope searchScope) {
        searchScopes.add(searchScope);
    }

    public List<String> getConnectorNames() {
        return connectorNames;
    }

    public void setConnectorNames(List<String> connectorNames) {
        this.connectorNames = connectorNames;
    }

    public List<String> getContentTypes() {
        return contentTypes;
    }

    public void setContentTypes(List<String> contentTypes) {
        this.contentTypes = contentTypes;
    }

    public String getId() {
        return id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getParam(String name) {
        return params.get(name);
    }

    public String getParam(String name, String defaultValue) {
        return params.containsKey(name) ? getParam(name) : defaultValue;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getQuery() {
        return query;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public Set<SearchScope> getSearchScopes() {
        return searchScopes;
    }

    public void setSearchScopes(Set<SearchScope> searchScopes) {
        this.searchScopes = new HashSet<Query.SearchScope>(searchScopes);
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page > 1 ? page : 1;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public float getPrivacy() {
        return privacy;
    }

    public void setPrivacy(float privacy) {
        this.privacy = privacy;
    }

    public boolean isPrivacyUseImageFeatures() {
        return privacyUseImageFeatures;
    }

    public void setPrivacyUseImageFeatures(boolean privacyUseImageFeatures) {
        this.privacyUseImageFeatures = privacyUseImageFeatures;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Query [");
        if (id != null) {
            builder.append("id=");
            builder.append(id);
            builder.append(", ");
        }
        if (link != null) {
            builder.append("link=");
            builder.append(link);
            builder.append(", ");
        }
        if (query != null) {
            builder.append("query=");
            builder.append(query);
            builder.append(", ");
        }
        if (contentTypes != null) {
            builder.append("contentTypes=");
            builder.append(contentTypes);
            builder.append(", ");
        }
        if (connectorNames != null) {
            builder.append("connectorNames=");
            builder.append(connectorNames);
            builder.append(", ");
        }
        builder.append("resultCount=");
        builder.append(resultCount);
        builder.append(", ");
        if (sortOrder != null) {
            builder.append("sortOrder=");
            builder.append(sortOrder);
            builder.append(", ");
        }
        if (searchScopes != null) {
            builder.append("searchScopes=");
            builder.append(searchScopes);
            builder.append(", ");
        }
        if (params != null) {
            builder.append("params=");
            builder.append(params);
        }
        builder.append("]");
        return builder.toString();
    }

    public enum SearchScope {
        TEXT,
        TAGS;

        public static SearchScope find(String name) {
            try {
                return SearchScope.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        public String getName() {
            return name().toLowerCase();
        }
    }

    public enum SortOrder {
        RELEVANCE,
        DATE,
        INTERESTINGNESS;

        public static SortOrder find(String name) {
            try {
                return SortOrder.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        public String getName() {
            return name().toLowerCase();
        }
    }

}
