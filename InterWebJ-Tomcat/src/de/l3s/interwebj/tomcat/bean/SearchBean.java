package de.l3s.interwebj.tomcat.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.istack.NotNull;

import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.connector.QueryResultCollector;
import de.l3s.interwebj.core.connector.ServiceConnector;
import de.l3s.interwebj.core.core.Engine;
import de.l3s.interwebj.core.core.Environment;
import de.l3s.interwebj.core.core.InterWebPrincipal;
import de.l3s.interwebj.core.query.ContentType;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.QueryFactory;
import de.l3s.interwebj.core.query.ResultItem;
import de.l3s.interwebj.core.query.SearchResults;
import de.l3s.interwebj.core.query.SearchScope;
import de.l3s.interwebj.tomcat.webutil.FacesUtils;

@Named
@ViewScoped
public class SearchBean implements Serializable {
    private static final Logger log = LogManager.getLogger(SearchBean.class);
    private static final long serialVersionUID = -4894599353026933768L;

    @NotNull
    private String query;
    private int page = 1;
    private String language = "en";
    private SearchResults searchResults;
    private Set<ContentType> selectedContentTypes;
    private Set<String> selectedConnectorNames;
    @NotNull
    private int resultCount;
    private int timeout = 60;

    public SearchBean() {
        init();
    }

    public String getConnectorBaseUrl(String connectorName) throws InterWebException {
        Engine engine = Environment.getInstance().getEngine();
        ServiceConnector connector = engine.getConnector(connectorName);
        return connector.getBaseUrl();
    }

    public List<SelectItem> getConnectorNames() throws InterWebException {
        List<SelectItem> connectorSelectItems = new ArrayList<>();
        Engine engine = Environment.getInstance().getEngine();
        for (ServiceConnector connector : engine.getConnectors()) {
            if (connector.isRegistered()) {
                SelectItem selectItem = new SelectItem(connector.getName());
                connectorSelectItems.add(selectItem);
            }
        }
        return connectorSelectItems;
    }

    public List<SelectItem> getContentTypes() throws InterWebException {
        if (selectedConnectorNames == null) {
            init();
        }

        Engine engine = Environment.getInstance().getEngine();
        Set<ContentType> contentTypes = new TreeSet<>();
        for (String connectorName : selectedConnectorNames) {
            ServiceConnector connector = engine.getConnector(connectorName);
            contentTypes.addAll(connector.getContentTypes());
        }
        List<SelectItem> contentTypeSelectItems = new ArrayList<>();
        for (ContentType contentType : contentTypes) {
            contentTypeSelectItems.add(new SelectItem(contentType));
        }
        return contentTypeSelectItems;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public SearchResults getSearchResults() {
        return searchResults;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public int getResultIndex(ResultItem resultItem) {
        return searchResults.getResultItems().indexOf(resultItem);
    }

    public Set<String> getSelectedConnectorNames() {
        return selectedConnectorNames;
    }

    public void setSelectedConnectorNames(Set<String> selectedConnectorNames) {
        this.selectedConnectorNames = selectedConnectorNames;
    }

    public Set<ContentType> getSelectedContentTypes() {
        return selectedContentTypes;
    }

    public void setSelectedContentTypes(Set<ContentType> selectedContentTypes) {
        this.selectedContentTypes = selectedContentTypes;
    }

    public String getTags(Object obj) {
        ResultItem resultItem = (ResultItem) obj;
        return StringUtils.join(resultItem.getTags(), ", ");
    }

    public String getTypeImageUrl(ContentType type) {
        if (ContentType.audio == type) {
            return "music.png";
        } else if (ContentType.image == type) {
            return "photo.png";
        } else if (ContentType.text == type) {
            return "script.png";
        } else if (ContentType.video == type) {
            return "film.png";
        } else if (ContentType.presentation == type) {
            return "pictures.png";
        } else {
            return null;
        }
    }

    public boolean hasResults() {
        return searchResults != null;
    }

    public void init() {
        Engine engine = Environment.getInstance().getEngine();
        selectedConnectorNames = new HashSet<>(engine.getConnectorNames());
        selectedContentTypes = new HashSet<>(engine.getContentTypes());
        resultCount = 10;
    }

    public void save() {
    }

    public void search() {
        QueryFactory queryFactory = new QueryFactory();
        Query query = queryFactory.createQuery(this.query, selectedContentTypes);
        query.setConnectorNames(selectedConnectorNames);
        String link = FacesUtils.getInterWebJBean().getBaseUrl() + "api/search/" + query.getId() + ".xml";
        query.setLink(link);
        query.addSearchScope(SearchScope.text);
        query.addSearchScope(SearchScope.tags);
        query.setPerPage(resultCount);
        query.setPage(page);
        query.setLanguage(language);
        query.setTimeout(timeout);

        SearchResults searchResults = new SearchResults(query);
        Engine engine = Environment.getInstance().getEngine();
        InterWebPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
        try {
            QueryResultCollector collector = engine.getQueryResultCollector(query, principal);
            searchResults = collector.retrieve();
        } catch (InterWebException e) {
            log.error(e);
            FacesUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e);
        }
        /* standing queries are never used
        ExpirableMap<String, Object> expirableMap = engine.getExpirableMap();
        expirableMap.put(queryResult.getQuery().getId(), queryResult);
        */
        this.searchResults = searchResults;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
