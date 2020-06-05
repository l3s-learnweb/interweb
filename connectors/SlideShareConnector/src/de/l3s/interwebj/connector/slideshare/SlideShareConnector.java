package de.l3s.interwebj.connector.slideshare;

import static de.l3s.interwebj.core.util.Assertions.notNull;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.hash.Hashing;

import de.l3s.interwebj.connector.slideshare.jaxb.SearchResultEntity;
import de.l3s.interwebj.connector.slideshare.jaxb.SlideShareResponse;
import de.l3s.interwebj.connector.slideshare.jaxb.TagsResponse;
import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.Parameters;
import de.l3s.interwebj.core.core.ServiceConnector;
import de.l3s.interwebj.core.query.ConnectorResults;
import de.l3s.interwebj.core.query.ContentType;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.ResultItem;
import de.l3s.interwebj.core.query.SearchRanking;
import de.l3s.interwebj.core.query.SearchScope;
import de.l3s.interwebj.core.query.Thumbnail;
import de.l3s.interwebj.core.util.CoreUtils;

public class SlideShareConnector extends ServiceConnector implements Cloneable {
    private static final Logger log = LogManager.getLogger(SlideShareConnector.class);

    public SlideShareConnector() {
        super("SlideShare", "https://www.slideshare.net", ContentType.text, ContentType.presentation, ContentType.video);
    }

    public SlideShareConnector(AuthCredentials consumerAuthCredentials) {
        this();
        setAuthCredentials(consumerAuthCredentials);
    }

    @Override
    public Parameters authenticate(String callbackUrl, Parameters parameters) {
        Parameters params = new Parameters();
        params.add(Parameters.AUTHORIZATION_URL, callbackUrl);
        return params;
    }

    @Override
    public ServiceConnector clone() {
        return new SlideShareConnector(getAuthCredentials());
    }

    @Override
    public AuthCredentials completeAuthentication(Parameters params) {
        notNull(params, "params");
        String key = params.get(Parameters.USER_KEY);
        String secret = params.get(Parameters.USER_SECRET);
        return new AuthCredentials(key, secret);
    }

    @Override
    public ConnectorResults get(Query query, AuthCredentials authCredentials) throws InterWebException {
        notNull(query, "query");
        ConnectorResults queryResult = new ConnectorResults(query, getName());

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("https://www.slideshare.net/api/2/search_slideshows")
            .queryParam("q", query.getQuery())
            .queryParam("lang", query.getLanguage())
            .queryParam("page", Integer.toString(query.getPage()))
            .queryParam("items_per_page", Integer.toString(query.getPerPage()))
            // .queryParam("detailed", "1")
            .queryParam("sort", createSortOrder(query.getRanking()));

        String searchScope = createSearchScope(query.getSearchScopes());
        if (searchScope != null) {
            target = target.queryParam("what", searchScope);
        }

        String fileType = createFileType(query.getContentTypes());
        if (fileType == null) {
            return queryResult;
        }
        target = target.queryParam("file_type", fileType);

        Response response = postQuery(target);

        SlideShareResponse sr;
        try {
            // macht oft probleme. womöglich liefert slideshare einen fehler im html format oder jersey spinnt
            sr = response.readEntity(SlideShareResponse.class);
        } catch (Exception e) {
            log.error(e);
            return queryResult;
        }

        queryResult.setTotalResultCount(sr.getMeta().getTotalResults());
        int count = sr.getMeta().getResultOffset() - 1;
        List<SearchResultEntity> searchResults = sr.getSearchResults();
        if (searchResults == null) {
            return queryResult;
        }

        for (SearchResultEntity sre : searchResults) {
            ResultItem resultItem = new ResultItem(getName(), count++);
            resultItem.setType(createType(sre.getSlideshowType()));
            resultItem.setId(Integer.toString(sre.getId()));
            resultItem.setTitle(sre.getTitle());
            resultItem.setDescription(sre.getDescription());
            resultItem.setUrl(sre.getUrl());
            resultItem.setDate(CoreUtils.formatDate(parseDate(sre.getUpdated())));

            // slideshare api return always the same wrong thumbnail size
            //String[] size = sre.getThumbnailSize().substring(1, sre.getThumbnailSize().length()-1).split(",");

            int width = 170; // Integer.parseInt(size[0]);
            int height = fileType.equals("documents") ? 220 : 128; // Integer.parseInt(size[1]);

            resultItem.setThumbnailSmall(new Thumbnail(sre.getThumbnailURL(), width, height));
            resultItem.setEmbeddedCode(getEmbeddedCode(sre.getEmbed()));

            queryResult.addResultItem(resultItem);
        }
        return queryResult;
    }

    @Override
    public String getEmbedded(AuthCredentials authCredentials, String url, int maxWidth, int maxHeight) throws InterWebException {
        notNull(url, "url");
        URI uri = URI.create(url);
        URI baseUri = URI.create(getBaseUrl());
        if (!baseUri.getHost().endsWith(uri.getHost())) {
            throw new InterWebException("URL: [" + url + "] doesn't belong to connector");
        }
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("https://www.slideshare.net/api/2/get_slideshow");
        target = target.queryParam("slideshow_url", url);

        Response response = postQuery(target);
        if (response.getStatus() != 200) {
            throw new InterWebException("URL: [" + url + "] doesn't belong to connector [" + getName() + "]");
        }
        SearchResultEntity sre = response.readEntity(SearchResultEntity.class);
        String embedded = sre.getEmbed();
        embedded = embedded.replaceAll("width:\\d+px", "width:" + maxWidth + "px");
        embedded = embedded.replaceAll("width=\"\\d+\"", "width=\"" + maxWidth + "\"");
        embedded = embedded.replaceAll("height=\"\\d+\"", "height=\"" + maxHeight + "\"");
        embedded = embedded.replaceAll("<strong.+</strong>", "");
        return embedded;
    }

    @Override
    public String getUserId(AuthCredentials authCredentials) throws InterWebException {
        notNull(authCredentials, "authCredentials");
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("https://www.slideshare.net/api/2/get_user_tags");
        target = target.queryParam("username", authCredentials.getKey());
        target = target.queryParam("password", authCredentials.getSecret());

        Response response = getQuery(target);
        try {
            response.readEntity(TagsResponse.class);
        } catch (Exception e) {
            throw new InterWebException("User authentication failed on SlideShare");
        }
        return authCredentials.getKey();
    }

    @Override
    public boolean isConnectorRegistrationDataRequired() {
        return true;
    }

    @Override
    public boolean isUserRegistrationDataRequired() {
        return true;
    }

    @Override
    public boolean isUserRegistrationRequired() {
        return true;
    }

    private String createFileType(Set<ContentType> contentTypes) {
        List<String> fileTypes = new ArrayList<>();

        if (contentTypes.contains(ContentType.presentation)) {
            fileTypes.add("presentations");
        }
        if (contentTypes.contains(ContentType.text)) {
            fileTypes.add("documents");
        }
        if (contentTypes.contains(ContentType.video)) {
            fileTypes.add("videos");
        }

        if (fileTypes.size() == 0) {
            return null;
        }
        if (fileTypes.size() == 1) {
            return fileTypes.get(0);
        }
        return "all";
    }

    private String createSearchScope(Set<SearchScope> searchScopes) {
        if (!searchScopes.contains(SearchScope.text)) {
            return "tag";
        }
        return null;
    }

    private String createSortOrder(SearchRanking searchRanking) {
        switch (searchRanking) {
            case relevance:
                return "relevance";
            case date:
                return "latest";
            case interestingness:
                return "mostviewed";
            default:
                return "relevance";
        }
    }

    private ContentType createType(int slideshowType) {
        switch (slideshowType) {
            case 0:
                return ContentType.presentation;
            case 1:
                return ContentType.text;
            case 2:
                return ContentType.image;
            case 3:
                return ContentType.video;
            default:
                log.error("Unknown type {}", slideshowType);
        }
        return null;
    }

    private Response getQuery(WebTarget target) {
        AuthCredentials authCredentials = getAuthCredentials();
        long timestamp = System.currentTimeMillis() / 1000;
        String toHash = authCredentials.getSecret() + timestamp;
        target = target.queryParam("api_key", authCredentials.getKey());
        target = target.queryParam("ts", Long.toString(timestamp));
        target = target.queryParam("hash", getHash(toHash));
        return target.request().get();
    }

    private Date parseDate(String dateString) throws InterWebException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            log.error(e);
            throw new InterWebException("dateString: [" + dateString + "] " + e.getMessage());
        }
    }

    private Response postQuery(WebTarget target) {
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        return postQuery(target, params);
    }

    private Response postQuery(WebTarget target, MultivaluedMap<String, String> params) {
        AuthCredentials authCredentials = getAuthCredentials();
        long timestamp = System.currentTimeMillis() / 1000;
        String toHash = authCredentials.getSecret() + timestamp;
        params.add("api_key", authCredentials.getKey());
        params.add("ts", Long.toString(timestamp));
        params.add("hash", getHash(toHash));
        return target.request().post(Entity.form(params));
    }

    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    private String getHash(String str) {
        return Hashing.sha1().hashString(str, StandardCharsets.UTF_8).toString();
    }

    /**
     * Remove spam from the embedded code.
     */
    private static String getEmbeddedCode(String embed) {
        Pattern pattern = Pattern.compile("(<object.*</object>)");
        Matcher matcher = pattern.matcher(embed);

        if (matcher.find()) {
            return matcher.group(0);
        } else {
            return CoreUtils.cleanupEmbedHtml(embed);
        }
    }
}
