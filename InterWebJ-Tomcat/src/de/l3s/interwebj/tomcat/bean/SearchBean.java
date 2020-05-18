package de.l3s.interwebj.tomcat.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.istack.NotNull;

import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.core.Engine;
import de.l3s.interwebj.core.core.Environment;
import de.l3s.interwebj.core.core.InterWebPrincipal;
import de.l3s.interwebj.core.core.ServiceConnector;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.Query.SearchScope;
import de.l3s.interwebj.core.query.QueryFactory;
import de.l3s.interwebj.core.query.QueryResult;
import de.l3s.interwebj.core.query.QueryResultCollector;
import de.l3s.interwebj.core.query.ResultItem;
import de.l3s.interwebj.core.query.Thumbnail;
import de.l3s.interwebj.core.util.CoreUtils;
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
    private QueryResult queryResult;
    private List<String> selectedContentTypes;
    private List<String> selectedConnectorNames;
    @NotNull
    private int resultCount;
    private int timeout = 60;
    private boolean privacyUseImageFeatures = false;
    private boolean usePrivacy = false;

    public SearchBean() {
        init();
    }

    public String getConnectorBaseUrl(String connectorName) throws InterWebException {
        Engine engine = Environment.getInstance().getEngine();
        ServiceConnector connector = engine.getConnector(connectorName);
        return connector.getBaseUrl();
    }

    public List<SelectItem> getConnectorNames() throws InterWebException {
        List<SelectItem> connectorSelectItems = new ArrayList<SelectItem>();
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
        Set<String> contentTypes = new TreeSet<String>();
        for (String connectorName : selectedConnectorNames) {
            ServiceConnector connector = engine.getConnector(connectorName);
            contentTypes.addAll(connector.getContentTypes());
        }
        List<SelectItem> contentTypeSelectItems = new ArrayList<SelectItem>();
        for (String contentType : contentTypes) {
            SelectItem selectItem = new SelectItem(contentType);
            contentTypeSelectItems.add(selectItem);
        }
        return contentTypeSelectItems;
    }

    public String getImageUrl(Object obj, Long maxWidth, Long maxHeight) throws InterWebException {
        ResultItem resultItem = (ResultItem) obj;
        Thumbnail thumbnail = resultItem.getThumbnail(maxWidth.intValue(), maxHeight.intValue());
        if (thumbnail == null) {
            return "";
        }
        return thumbnail.getUrl();
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public QueryResult getQueryResult() {
        return queryResult;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public int getResultIndex(Object resultItem) {
        return queryResult.getResultItems().indexOf(resultItem);
    }

    public List<String> getSelectedConnectorNames() {
        return selectedConnectorNames;
    }

    public void setSelectedConnectorNames(List<String> selectedConnectorNames) {
        this.selectedConnectorNames = selectedConnectorNames;
    }

    public List<String> getSelectedContentTypes() {
        return selectedContentTypes;
    }

    public void setSelectedContentTypes(List<String> selectedContentTypes) {
        this.selectedContentTypes = selectedContentTypes;
    }

    public String getTags(Object obj) {
        ResultItem resultItem = (ResultItem) obj;
        String tags = resultItem.getTags();
        return (tags == null) ? null : CoreUtils.convertToUniqueList(tags).toString();
    }

    public String getTypeImageUrl(String type) {
        if (Query.CT_AUDIO.equals(type)) {
            return "music.png";
        }
        if (Query.CT_IMAGE.equals(type)) {
            return "photo.png";
        }
        if (Query.CT_TEXT.equals(type)) {
            return "script.png";
        }
        if (Query.CT_VIDEO.equals(type)) {
            return "film.png";
        }
        if (Query.CT_FRIEND.equals(type)) {
            return "user.png";
        }
        if (Query.CT_PRESENTATION.equals(type)) {
            return "pictures.png";
        }
        return null;
    }

    public boolean hasResults() {
        return queryResult != null;
    }

    public void init() {
        Engine engine = Environment.getInstance().getEngine();
        selectedConnectorNames = engine.getConnectorNames();
        selectedContentTypes = engine.getContentTypes();
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
        query.addSearchScope(SearchScope.TEXT);
        query.addSearchScope(SearchScope.TAGS);
        query.setResultCount(resultCount);
        query.setPage(page);
        query.setLanguage(language);
        query.setPrivacy(usePrivacy ? 1f : -1f);
        query.setTimeout(timeout);
        query.setPrivacyUseImageFeatures(privacyUseImageFeatures);
        QueryResult queryResult = new QueryResult(query);
        Engine engine = Environment.getInstance().getEngine();
        InterWebPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
        try {
            QueryResultCollector collector = engine.getQueryResultCollector(query, principal);
            queryResult = collector.retrieve();
        } catch (InterWebException e) {
            log.error(e);
            FacesUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e);
        }
        /* standing queries are never used
        ExpirableMap<String, Object> expirableMap = engine.getExpirableMap();
        expirableMap.put(queryResult.getQuery().getId(), queryResult);
        */
        this.queryResult = queryResult;
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

    public boolean isPrivacyUseImageFeatures() {
        return privacyUseImageFeatures;
    }

    public void setPrivacyUseImageFeatures(boolean privacyUseImageFeatures) {
        this.privacyUseImageFeatures = privacyUseImageFeatures;
    }

    public boolean isUsePrivacy() {
        return usePrivacy;
    }

    public void setUsePrivacy(boolean usePrivacy) {
        this.usePrivacy = usePrivacy;
    }
}