package de.l3s.interwebj.connector.flickr;

import static de.l3s.interwebj.core.util.Assertions.notNull;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringJoiner;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.people.User;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.SearchParameters;
import com.flickr4java.flickr.photos.Size;
import com.flickr4java.flickr.tags.Tag;
import com.flickr4java.flickr.uploader.UploadMetaData;
import com.flickr4java.flickr.uploader.Uploader;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuth1Token;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.Parameters;
import de.l3s.interwebj.core.core.AbstractServiceConnector;
import de.l3s.interwebj.core.core.ServiceConnector;
import de.l3s.interwebj.core.query.ConnectorResults;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.Query.SearchScope;
import de.l3s.interwebj.core.query.Query.SortOrder;
import de.l3s.interwebj.core.query.ResultItem;
import de.l3s.interwebj.core.query.Thumbnail;
import de.l3s.interwebj.core.util.CoreUtils;

public class FlickrConnector extends AbstractServiceConnector implements Cloneable {
    private static final Logger log = LogManager.getLogger(FlickrConnector.class);

    private static final String MEDIA_ALL = "all";
    private static final String MEDIA_PHOTOS = "photos";
    private static final String MEDIA_VIDEOS = "videos";

    public FlickrConnector() {
        super("Flickr", "http://www.flickr.com", new TreeSet<>(Arrays.asList("image", "video")));
    }

    public FlickrConnector(AuthCredentials consumerAuthCredentials) {
        this();
        setAuthCredentials(consumerAuthCredentials);
    }

    private static Set<String> getExtras() {
        Set<String> extras = new HashSet<String>();
        extras.add("description");
        extras.add("tags");
        extras.add("date_upload");
        extras.add("views");
        extras.add("media");
        extras.add("url_t");
        extras.add("url_s");
        extras.add("url_m");
        extras.add("url_l");

        return extras;
    }

    @Override
    public Parameters authenticate(String callbackUrl, Parameters parameters) throws InterWebException {
        if (!isRegistered()) {
            throw new InterWebException("Service is not yet registered");
        }
        Parameters params = new Parameters();
        try {
            AuthCredentials consumerAuthCredentials = getAuthCredentials();
            AuthInterface authInterface = new AuthInterface(consumerAuthCredentials.getKey(), consumerAuthCredentials.getSecret(), new REST());

            OAuth1RequestToken requestToken = authInterface.getRequestToken(callbackUrl);
            params.add(Parameters.OAUTH_TOKEN_SECRET, requestToken.getTokenSecret());
            log.info("requestToken: [{} - {}]", requestToken.getToken(), requestToken.getTokenSecret());

            String requestTokenUrl = authInterface.getAuthorizationUrl(requestToken, Permission.DELETE);
            log.info("callbackUrl: [" + callbackUrl + "]");
            log.info("requestTokenUrl: [" + requestTokenUrl + "]");
            params.add(Parameters.AUTHORIZATION_URL, requestTokenUrl);
        } catch (Exception e) {
            log.error(e);
            throw new InterWebException(e);
        }
        return params;
    }

    @Override
    public ServiceConnector clone() {
        return new FlickrConnector(getAuthCredentials());
    }

    @Override
    public AuthCredentials completeAuthentication(Parameters params) throws InterWebException {
        notNull(params, "params");
        AuthCredentials authCredentials = null;
        if (!isRegistered()) {
            throw new InterWebException("Service is not yet registered");
        }
        try {
            AuthCredentials consumerAuthCredentials = getAuthCredentials();
            AuthInterface authInterface = new AuthInterface(consumerAuthCredentials.getKey(), consumerAuthCredentials.getSecret(), new REST());

            String verifier = params.get(Parameters.OAUTH_VERIFIER);
            log.info("request verifier: " + verifier);

            OAuth1RequestToken requestToken = new OAuth1RequestToken(params.get(Parameters.OAUTH_TOKEN), params.get(Parameters.OAUTH_TOKEN_SECRET));
            OAuth1Token accessToken = authInterface.getAccessToken(requestToken, verifier);
            log.info("Authentication success");

            Auth auth = authInterface.checkToken(accessToken);
            authCredentials = new AuthCredentials(auth.getToken());
        } catch (Exception e) {
            log.error(e);
            throw new InterWebException(e);
        }
        return authCredentials;
    }

    @Override
    public ConnectorResults get(Query query, AuthCredentials authCredentials) throws InterWebException {
        notNull(query, "query");
        if (!isRegistered()) {
            throw new InterWebException("Service is not yet registered");
        }

        return getMedia(query, authCredentials);
    }

    @Override
    public String getEmbedded(AuthCredentials authCredentials, String url, int maxWidth, int maxHeight) throws InterWebException {
        notNull(url, "url");
        URI uri = URI.create(url);
        URI baseUri = URI.create(getBaseUrl());
        if (!baseUri.getHost().endsWith(uri.getHost())) {
            throw new InterWebException("URL: [" + url + "] doesn't belong to connector [" + getName() + "]");
        }
        String path = uri.getPath();
        String id = path.substring(path.lastIndexOf('/') + 1);
        RequestContext requestContext = RequestContext.getRequestContext();
        if (authCredentials != null) {
            Auth auth = new Auth();
            requestContext.setAuth(auth);
            auth.setToken(authCredentials.getKey());
            auth.setPermission(Permission.READ);
        }
        try {
            Flickr flickr = createFlickrInstance();
            PhotosInterface pi = flickr.getPhotosInterface();
            Collection<Size> sizes = pi.getSizes(id);
            notNull(sizes, "sizes");
            if (sizes.size() == 0) {
                throw new InterWebException("There are no thumbnails available for an image with URL: [" + url + "]");
            }
            return createEmbeddedCode(sizes, maxWidth, maxHeight);
        } catch (FlickrException e) {
            log.error(e);
            throw new InterWebException(e);
        }
    }

    @Override
    public Parameters getRefinedCallbackParameters(Parameters parameters) {
        Parameters refinedParameters = new Parameters();
        for (String parameterName : parameters.keySet()) {
            if (parameterName.equals("extra")) {
                String query = parameters.get(parameterName);
                refinedParameters.addQueryParameters(query);
            } else {
                refinedParameters.add(parameterName, parameters.get(parameterName));
            }
        }
        return refinedParameters;
    }

    @Override
    public String getUserId(AuthCredentials authCredentials) throws InterWebException {
        try {
            Flickr flickr = createFlickrInstance();
            AuthInterface authInterface = flickr.getAuthInterface();
            Auth auth = authInterface.checkToken(authCredentials.getKey(), authCredentials.getSecret());
            User user = auth.getUser();
            return user.getId();
        } catch (FlickrException e) {
            log.error(e);
            throw new InterWebException(e);
        }
    }

    @Override
    public boolean isConnectorRegistrationDataRequired() {
        return true;
    }

    @Override
    public boolean isUserRegistrationDataRequired() {
        return false;
    }

    @Override
    public boolean isUserRegistrationRequired() {
        return true;
    }

    @Override
    public ResultItem put(byte[] data, String contentType, Parameters params, AuthCredentials authCredentials) throws InterWebException {
        notNull(data, "data");
        notNull(contentType, "contentType");
        notNull(params, "params");
        if (!isRegistered()) {
            throw new InterWebException("Service is not yet registered");
        }
        if (authCredentials == null) {
            throw new InterWebException("Upload is forbidden for non-authorized users");
        }
        if (contentType.equals(Query.CT_IMAGE)) {
            try {
                RequestContext requestContext = RequestContext.getRequestContext();
                Auth auth = new Auth();
                requestContext.setAuth(auth);
                auth.setToken(authCredentials.getKey());
                auth.setPermission(Permission.WRITE);
                Flickr flickr = createFlickrInstance();
                Uploader uploader = flickr.getUploader();
                UploadMetaData metaData = new UploadMetaData();
                metaData.setTitle(params.get(Parameters.TITLE, "No Title"));
                metaData.setDescription(params.get(Parameters.DESCRIPTION, "No Description"));
                String tags = params.get(Parameters.TAGS, "");
                metaData.setTags(CoreUtils.convertToUniqueList(tags));
                int privacy = Integer.parseInt(params.get(Parameters.PRIVACY, "0"));
                metaData.setPublicFlag(privacy == 0);
                String id = uploader.upload(data, metaData);
                Photo photo = flickr.getPhotosInterface().getPhoto(id);
                log.info(photo.getSmallUrl());
                log.info("data successfully uploaded");

                return createResultItem(photo, 0, 0);
            } catch (FlickrException e) {
                log.error(e);
                throw new InterWebException(e);
            }
        }
        return null;
    }

    private String createContentType(String media) {
        if ("photo".equals(media)) {
            return Query.CT_IMAGE;
        }
        return media;
    }

    private String createEmbeddedCode(Collection<Size> sizes, int maxWidth, int maxHeight) {
        Size size = null;
        for (Size s : sizes) {
            if (size == null || (s.getWidth() >= size.getWidth() && s.getHeight() >= size.getHeight())
                && (s.getWidth() <= maxWidth && s.getHeight() <= maxHeight)) {
                size = s;
            }
        }
        return createEmbeddedCode(size);
    }

    private String createEmbeddedCode(Size size) {
        return "<img src=\"" + size.getSource() + "\" height=\"" + size.getHeight() + "\" width=\"" + size.getWidth() + "\"/>";
    }

    private Flickr createFlickrInstance() throws InterWebException {
        if (!isRegistered()) {
            throw new InterWebException("Unable to create Flickr instance. Service is not registered");
        }

        AuthCredentials consumerAuthCredentials = getAuthCredentials();
        return new Flickr(consumerAuthCredentials.getKey(), consumerAuthCredentials.getSecret(), new REST());
    }

    private ResultItem createResultItem(Photo photo, int rank, int totalResultCount) throws FlickrException, InterWebException {

        ResultItem resultItem = new ResultItem(getName());
        resultItem.setId(photo.getId());
        resultItem.setType(createContentType(photo.getMedia()));
        resultItem.setTitle(photo.getTitle());
        resultItem.setDescription(photo.getDescription());
        resultItem.setTags(createTags(photo.getTags()));
        resultItem.setUrl(photo.getUrl());
        resultItem.setThumbnails(createThumbnails(photo));
        Date date = photo.getDatePosted();
        if (date != null) {
            resultItem.setDate(CoreUtils.formatDate(date.getTime()));
        }
        resultItem.setRank(rank);
        resultItem.setTotalResultCount(totalResultCount);
        resultItem.setCommentCount(photo.getComments());

        Size thumbnail = photo.getThumbnailSize();
        resultItem.setEmbeddedSize1(createEmbeddedCode(thumbnail));
        thumbnail = photo.getSmallSize();
        if (thumbnail != null) {
            resultItem.setEmbeddedSize2(createEmbeddedCode(thumbnail));
        }
        thumbnail = photo.getMediumSize();
        if (thumbnail != null) {
            resultItem.setEmbeddedSize3(createEmbeddedCode(thumbnail));
        }
        thumbnail = photo.getLargeSize();
        if (thumbnail != null) {
            resultItem.setEmbeddedSize4(createEmbeddedCode(thumbnail));
        }

        if (photo.getLargeUrl() != null && photo.getLargeUrl().length() > 7) {
            resultItem.setImageUrl(photo.getLargeUrl());
        } else {
            resultItem.setImageUrl(photo.getMediumUrl());
        }

        return resultItem;
    }

    private String createTags(Collection tags) {
        StringJoiner sj = new StringJoiner(",");
        for (Object obj : tags) {
            Tag tag = (Tag) obj;
            sj.add(tag.getValue());
        }
        return sj.toString();
    }

    private String[] createTags(String query) {
        return query.split("[\\W]+");
    }

    private Set<Thumbnail> createThumbnails(Photo photo) throws FlickrException {
        SortedSet<Thumbnail> thumbnails = new TreeSet<Thumbnail>();
        Size thumbnail = photo.getThumbnailSize();
        if (thumbnail != null) {
            thumbnails.add(new Thumbnail(thumbnail.getSource(), thumbnail.getWidth(), thumbnail.getHeight()));
        }
        thumbnail = photo.getSmallSize();
        if (thumbnail != null) {
            thumbnails.add(new Thumbnail(thumbnail.getSource(), thumbnail.getWidth(), thumbnail.getHeight()));
        }
        thumbnail = photo.getMediumSize();
        if (thumbnail != null) {
            thumbnails.add(new Thumbnail(thumbnail.getSource(), thumbnail.getWidth(), thumbnail.getHeight()));
        }
        thumbnail = photo.getLargeSize();
        if (thumbnail != null) {
            thumbnails.add(new Thumbnail(thumbnail.getSource(), thumbnail.getWidth(), thumbnail.getHeight()));
        }

        /*
        thumbnails.add(new Thumbnail(photo.getSmallSquareUrl(), 75, 75)); // das sind nur die maximalen breiten/höhen und somit fast nutzlos
        thumbnails.add(new Thumbnail(photo.getThumbnailUrl(), 100, 100));
        thumbnails.add(new Thumbnail(photo.getSmallUrl(), 240, 240));
        thumbnails.add(new Thumbnail(photo.getMediumUrl(), 500, 500));
        */
        return thumbnails;
    }

    private ConnectorResults getMedia(Query query, AuthCredentials authCredentials) throws InterWebException {
        ConnectorResults queryResult = new ConnectorResults(query, getName());
        if (supportContentTypes(query.getContentTypes())) {
            try {
                RequestContext requestContext = RequestContext.getRequestContext();
                if (authCredentials != null) {
                    Auth auth = new Auth();
                    auth.setToken(authCredentials.getKey());
                    auth.setTokenSecret(authCredentials.getSecret());
                    auth.setPermission(Permission.READ);
                    requestContext.setAuth(auth);
                }

                Flickr flickr = createFlickrInstance();
                PhotosInterface pi = flickr.getPhotosInterface();

                SearchParameters params = new SearchParameters();
                params.setExtras(getExtras());
                params.setMedia(getMediaType(query));

                if (query.getParam("date_from") != null) {
                    try {
                        Date dateFrom = new Date(CoreUtils.parseDate(query.getParam("date_from")));
                        params.setMinUploadDate(dateFrom);
                    } catch (Exception e) {
                        log.error(e);
                    }
                }

                if (query.getParam("date_till") != null) {
                    try {
                        Date dateTill = new Date(CoreUtils.parseDate(query.getParam("date_till")));
                        params.setMaxUploadDate(dateTill);
                    } catch (Exception e) {
                        log.error(e);
                    }
                }

                params.setSort(getSortOrder(query.getSortOrder()));

                PhotoList photoList = null;

                if (query.getQuery().startsWith("user::")) {
                    String username = query.getQuery().substring(6).trim();
                    User user = flickr.getPeopleInterface().findByUsername(username);

                    params.setUserId(user.getId());
                } else if (query.getQuery().startsWith("recent::")) {
                    photoList = pi.getRecent(getExtras(), query.getResultCount(), query.getPage());
                } else {
                    if (query.getSearchScopes().contains(SearchScope.TEXT)) {
                        params.setText(query.getQuery());

                    }
                    if (query.getSearchScopes().contains(SearchScope.TAGS)) {
                        String[] tags = createTags(query.getQuery());
                        params.setTags(tags);
                    }
                }

                if (null == photoList) {
                    photoList = pi.search(params, query.getResultCount(), query.getPage());
                }
                int rank = query.getResultCount() * (query.getPage() - 1);
                int totalResultCount = photoList.getTotal();
                queryResult.setTotalResultCount(totalResultCount);

                for (Object o : photoList) {
                    if (o instanceof Photo) {
                        Photo photo = (Photo) o;
                        ResultItem resultItem = createResultItem(photo, rank, totalResultCount);
                        queryResult.addResultItem(resultItem);
                        rank++;
                    }
                }
            } catch (FlickrException e) {
                if (e.getErrorMessage().equals("User not found")) {
                    System.err.println("Unknown user");
                } else {
                    log.error(e);
                    throw new InterWebException(e);
                }
            }
        }
        return queryResult;
    }

    private String getMediaType(Query query) {
        notNull(query, "query");
        String media = null;
        if (query.getContentTypes().contains(Query.CT_IMAGE) && query.getContentTypes().contains(Query.CT_VIDEO)) {
            media = MEDIA_ALL;
        } else if (query.getContentTypes().contains(Query.CT_IMAGE)) {
            media = MEDIA_PHOTOS;
        } else {
            media = MEDIA_VIDEOS;
        }
        return media;
    }

    private int getSortOrder(SortOrder sortOrder) {
        switch (sortOrder) {
            case RELEVANCE:
                return SearchParameters.RELEVANCE;
            case DATE:
                return SearchParameters.DATE_POSTED_DESC;
            case INTERESTINGNESS:
                return SearchParameters.INTERESTINGNESS_DESC;
            default:
                log.error("Unknown order {}", sortOrder);
        }
        return SearchParameters.RELEVANCE;
    }

    private boolean supportContentTypes(List<String> contentTypes) {
        return contentTypes.contains(Query.CT_IMAGE) || contentTypes.contains(Query.CT_VIDEO);
    }

    @Override
    public Set<String> getUsers(Set<String> tags, int maxCount) throws IOException, InterWebException {
        SearchParameters params = new SearchParameters();
        params.setExtras(Set.of("owner_name"));

        HashSet<String> users = new HashSet<String>();

        int errorCounter = 0;
        for (int page = 1; page < 8 && errorCounter < 80; page++) {
            for (String tag : tags) {
                if (tag.trim().length() < 3) { // don't use very short tags
                    continue;
                }

                String[] temp = {tag};
                params.setTags(temp);

                Flickr flickr = createFlickrInstance();
                PhotoList result;
                try {
                    result = flickr.getPhotosInterface().search(params, 500, page);
                } catch (FlickrException e) {
                    throw new RuntimeException(e);
                }

                if (result.size() == 0) {
                    errorCounter++;
                }

                Iterator<Photo> iterator = result.iterator();

                while (iterator.hasNext()) {
                    Photo f = iterator.next();
                    users.add(f.getOwner().getUsername());

                    if (users.size() == maxCount) {
                        return users;
                    }
                }
            }
        }

        return users;
    }

}
