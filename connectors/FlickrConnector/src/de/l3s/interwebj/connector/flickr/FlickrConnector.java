package de.l3s.interwebj.connector.flickr;

import static de.l3s.interwebj.core.util.Assertions.notNull;

import java.net.URI;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
import de.l3s.interwebj.core.connector.ConnectorSearchResults;
import de.l3s.interwebj.core.connector.ServiceConnector;
import de.l3s.interwebj.core.query.ContentType;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.ResultItem;
import de.l3s.interwebj.core.query.SearchRanking;
import de.l3s.interwebj.core.query.SearchScope;
import de.l3s.interwebj.core.query.Thumbnail;
import de.l3s.interwebj.core.util.CoreUtils;

/**
 * Flickr is an American image hosting and video hosting service, as well as an online community.
 * TODO missing search implementations: extras, language.
 *
 * @see <a href="https://www.flickr.com/services/api/flickr.photos.search.html">Flickr Search API</a>
 */
public class FlickrConnector extends ServiceConnector {
    private static final Logger log = LogManager.getLogger(FlickrConnector.class);

    private static final String MEDIA_ALL = "all";
    private static final String MEDIA_PHOTOS = "photos";
    private static final String MEDIA_VIDEOS = "videos";

    public FlickrConnector() {
        super("Flickr", "https://www.flickr.com/", ContentType.image, ContentType.video);
    }

    public FlickrConnector(AuthCredentials consumerAuthCredentials) {
        this();
        setAuthCredentials(consumerAuthCredentials);
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
            log.info("callbackUrl: [{}]", callbackUrl);
            log.info("requestTokenUrl: [{}]", requestTokenUrl);
            params.add(Parameters.AUTHORIZATION_URL, requestTokenUrl);
        } catch (Exception e) {
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
        AuthCredentials authCredentials;
        if (!isRegistered()) {
            throw new InterWebException("Service is not yet registered");
        }
        try {
            AuthCredentials consumerAuthCredentials = getAuthCredentials();
            AuthInterface authInterface = new AuthInterface(consumerAuthCredentials.getKey(), consumerAuthCredentials.getSecret(), new REST());

            String verifier = params.get(Parameters.OAUTH_VERIFIER);
            log.info("request verifier: {}", verifier);

            OAuth1RequestToken requestToken = new OAuth1RequestToken(params.get(Parameters.OAUTH_TOKEN), params.get(Parameters.OAUTH_TOKEN_SECRET));
            OAuth1Token accessToken = authInterface.getAccessToken(requestToken, verifier);
            log.info("Authentication success");

            Auth auth = authInterface.checkToken(accessToken);
            authCredentials = new AuthCredentials(auth.getToken());
        } catch (Exception e) {
            throw new InterWebException(e);
        }
        return authCredentials;
    }

    @Override
    public ConnectorSearchResults get(Query query, AuthCredentials authCredentials) throws InterWebException {
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

            Size size = null;
            for (Size s : sizes) {
                if (size == null || (s.getWidth() >= size.getWidth() && s.getHeight() >= size.getHeight())
                    && (s.getWidth() <= maxWidth && s.getHeight() <= maxHeight)) {
                    size = s;
                }
            }

            return "<img src=\"" + size.getSource() + "\" height=\"" + size.getHeight() + "\" width=\"" + size.getWidth() + "\"/>";
        } catch (FlickrException e) {
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
            throw new InterWebException(e);
        }
    }

    @Override
    public boolean isUserRegistrationRequired() {
        return true;
    }

    @Override
    public ResultItem put(byte[] data, ContentType contentType, Parameters params, AuthCredentials authCredentials) throws InterWebException {
        notNull(data, "data");
        notNull(contentType, "contentType");
        notNull(params, "params");

        if (!isRegistered()) {
            throw new InterWebException("Service is not yet registered");
        }
        if (authCredentials == null) {
            throw new InterWebException("Upload is forbidden for non-authorized users");
        }

        if (contentType == ContentType.image) {
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

                return createResultItem(photo, 0);
            } catch (FlickrException e) {
                throw new InterWebException(e);
            }
        }
        return null;
    }

    private ContentType createContentType(String media) {
        if ("photo".equals(media)) {
            return ContentType.image;
        } else {
            return ContentType.video;
        }
    }

    private Thumbnail createThumbnail(Size size) {
        if (size != null) {
            return new Thumbnail(size.getSource(), size.getWidth(), size.getHeight());
        }

        return null;
    }

    private Flickr createFlickrInstance() throws InterWebException {
        if (!isRegistered()) {
            throw new InterWebException("Unable to create Flickr instance. Service is not registered");
        }

        AuthCredentials consumerAuthCredentials = getAuthCredentials();
        return new Flickr(consumerAuthCredentials.getKey(), consumerAuthCredentials.getSecret(), new REST());
    }

    private ConnectorSearchResults getMedia(Query query, AuthCredentials authCredentials) throws InterWebException {
        ConnectorSearchResults queryResult = new ConnectorSearchResults(query, getName());
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

                if (query.getDateFrom() != null) {
                    try {
                        params.setMinUploadDate(Date.from(CoreUtils.parseDate(query.getDateFrom()).toInstant()));
                    } catch (DateTimeParseException e) {
                        log.error("Failed to parse date {}", query.getDateFrom(), e);
                    }
                }

                if (query.getDateTill() != null) {
                    try {
                        params.setMaxUploadDate(Date.from(CoreUtils.parseDate(query.getDateTill()).toInstant()));
                    } catch (DateTimeParseException e) {
                        log.error("Failed to parse date {}", query.getDateTill(), e);
                    }
                }

                params.setSort(convertRanking(query.getRanking()));

                PhotoList<Photo> photoList = null;

                if (query.getQuery().startsWith("user::")) {
                    String username = query.getQuery().substring(6).trim();
                    User user = flickr.getPeopleInterface().findByUsername(username);

                    params.setUserId(user.getId());
                } else if (query.getQuery().startsWith("recent::")) {
                    photoList = pi.getRecent(getExtras(), query.getPerPage(), query.getPage());
                } else {
                    if (query.getSearchScope() == SearchScope.text) {
                        params.setText(query.getQuery());
                    } else if (query.getSearchScope() == SearchScope.tags) {
                        params.setTags(createTags(query.getQuery()));
                    }
                }

                if (null == photoList) {
                    photoList = pi.search(params, query.getPerPage(), query.getPage());
                }
                int rank = query.getPerPage() * (query.getPage() - 1);
                int totalResultCount = photoList.getTotal();
                queryResult.setTotalResultCount(totalResultCount);

                for (Object o : photoList) {
                    if (o instanceof Photo) {
                        Photo photo = (Photo) o;
                        ResultItem resultItem = createResultItem(photo, rank);
                        queryResult.addResultItem(resultItem);
                        rank++;
                    }
                }
            } catch (FlickrException e) {
                if (e.getErrorMessage().equals("User not found")) {
                    System.err.println("Unknown user");
                } else {
                    throw new InterWebException(e);
                }
            }
        }
        return queryResult;
    }

    private ResultItem createResultItem(Photo photo, int rank) {
        ResultItem resultItem = new ResultItem(getName(), rank);
        resultItem.setId(photo.getId());
        resultItem.setType(createContentType(photo.getMedia()));
        resultItem.setTitle(photo.getTitle());
        resultItem.setDescription(photo.getDescription());
        resultItem.setUrl(photo.getUrl());
        resultItem.setWidth(photo.getOriginalWidth());
        resultItem.setHeight(photo.getOriginalHeight());

        if (photo.getOwner() != null) {
            resultItem.setAuthor(photo.getOwner().getUsername());
            resultItem.setAuthorUrl("https://www.flickr.com/photos/" + photo.getOwner().getId());
        }

        if (photo.getDatePosted() != null) {
            resultItem.setDate(CoreUtils.formatDate(photo.getDatePosted().getTime()));
        }
        if (photo.getTags() != null) {
            for (Tag tag : photo.getTags()) {
                resultItem.getTags().add(tag.getValue());
            }
        }
        resultItem.setCommentCount((long) photo.getComments());

        resultItem.setThumbnailSmall(createThumbnail(photo.getSmallSize()));
        resultItem.setThumbnailMedium(createThumbnail(photo.getMediumSize()));
        resultItem.setThumbnailLarge(createThumbnail(photo.getLargeSize()));
        resultItem.setThumbnailOriginal(createThumbnail(photo.getOriginalSize()));

        return resultItem;
    }

    private String[] createTags(String query) {
        return query.split("[\\W]+");
    }

    private String getMediaType(Query query) {
        notNull(query, "query");

        if (query.getContentTypes().contains(ContentType.image) && query.getContentTypes().contains(ContentType.video)) {
            return MEDIA_ALL;
        } else if (query.getContentTypes().contains(ContentType.image)) {
            return MEDIA_PHOTOS;
        } else {
            return MEDIA_VIDEOS;
        }
    }

    private static int convertRanking(SearchRanking ranking) {
        switch (ranking) {
            case date:
                return SearchParameters.DATE_POSTED_DESC;
            case interestingness:
                return SearchParameters.INTERESTINGNESS_DESC;
            case relevance:
            default:
                return SearchParameters.RELEVANCE;
        }
    }

    private boolean supportContentTypes(Set<ContentType> contentTypes) {
        return contentTypes.contains(ContentType.image) || contentTypes.contains(ContentType.video);
    }

    @Override
    public Set<String> getUsers(Set<String> tags, int maxCount) throws InterWebException {
        SearchParameters params = new SearchParameters();
        params.setExtras(Set.of("owner_name"));

        HashSet<String> users = new HashSet<>();

        int errorCounter = 0;
        for (int page = 1; page < 8 && errorCounter < 80; page++) {
            for (String tag : tags) {
                if (tag.trim().length() < 3) { // don't use very short tags
                    continue;
                }

                String[] temp = {tag};
                params.setTags(temp);

                try {
                    Flickr flickr = createFlickrInstance();
                    PhotoList<Photo> result = flickr.getPhotosInterface().search(params, 500, page);

                    if (result.size() == 0) {
                        errorCounter++;
                    }

                    for (Photo photo : result) {
                        users.add(photo.getOwner().getUsername());

                        if (users.size() == maxCount) {
                            return users;
                        }
                    }
                } catch (FlickrException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return users;
    }

    private static Set<String> getExtras() {
        return Set.of("description", "tags", "owner_name", "date_upload", "views", "media", "url_t", "url_s", "url_m", "url_l");
    }

}
