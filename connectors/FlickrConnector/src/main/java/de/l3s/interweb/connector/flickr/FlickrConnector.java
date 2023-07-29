package de.l3s.interweb.connector.flickr;

import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import jakarta.enterprise.context.Dependent;

import org.jboss.logging.Logger;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.people.User;
import com.flickr4java.flickr.photos.*;
import com.flickr4java.flickr.tags.Tag;

import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.search.*;
import de.l3s.interweb.core.util.Assertions;
import de.l3s.interweb.core.util.DateUtils;

/**
 * Flickr is an American image hosting and video hosting service, as well as an online community.
 * TODO missing search implementations: extras, language.
 *
 * @see <a href="https://www.flickr.com/services/api/flickr.photos.search.html">Flickr Search API</a>
 */
@Dependent
public class FlickrConnector implements SearchConnector {
    private static final Logger log = Logger.getLogger(FlickrConnector.class);

    private static final String MEDIA_ALL = "all";
    private static final String MEDIA_PHOTOS = "photos";
    private static final String MEDIA_VIDEOS = "videos";

    @Override
    public String getName() {
        return "Flickr";
    }

    @Override
    public String getBaseUrl() {
        return "https://www.flickr.com/";
    }

    @Override
    public ContentType[] getSearchTypes() {
        return new ContentType[]{ContentType.image, ContentType.video};
    }

    @Override
    public SearchConnectorResults search(SearchQuery query, AuthCredentials credentials) throws ConnectorException {
        Assertions.notNull(query, "query");

        return getMedia(query, credentials);
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

    private Flickr createFlickrInstance(AuthCredentials authCredentials) throws ConnectorException {
        return new Flickr(authCredentials.getKey(), authCredentials.getSecret(), new REST());
    }

    private SearchConnectorResults getMedia(SearchQuery query, AuthCredentials authCredentials) throws ConnectorException {
        SearchConnectorResults queryResult = new SearchConnectorResults();
        try {
            Flickr flickr = createFlickrInstance(authCredentials);
            PhotosInterface pi = flickr.getPhotosInterface();

            SearchParameters params = new SearchParameters();
            params.setExtras(getExtras());
            params.setMedia(getMediaType(query));

            if (query.getDateFrom() != null) {
                params.setMinUploadDate(Date.from(query.getDateFrom().atStartOfDay().toInstant(ZoneOffset.UTC)));
            }

            if (query.getDateTill() != null) {
                params.setMaxUploadDate(Date.from(query.getDateTill().atStartOfDay().toInstant(ZoneOffset.UTC)));
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
            queryResult.setTotalResults(totalResultCount);

            for (Object o : photoList) {
                if (o instanceof Photo photo) {
                    SearchItem resultItem = createResultItem(photo, rank);
                    queryResult.addResultItem(resultItem);
                    rank++;
                }
            }
        } catch (FlickrException e) {
            if (e.getErrorMessage().equals("User not found")) {
                System.err.println("Unknown user");
            } else {
                throw new ConnectorException(e);
            }
        }
        return queryResult;
    }

    private SearchItem createResultItem(Photo photo, int rank) {
        SearchItem resultItem = new SearchItem(rank);
        resultItem.setId(photo.getId());
        resultItem.setType(createContentType(photo.getMedia()));
        resultItem.setTitle(photo.getTitle());
        resultItem.setDescription(photo.getDescription());
        resultItem.setUrl(photo.getUrl());

        if (photo.getOriginalWidth() != 0) {
            resultItem.setWidth(photo.getOriginalWidth());
            resultItem.setHeight(photo.getOriginalHeight());
        } else if (photo.getLargeSize() != null) {
            resultItem.setWidth(photo.getLargeSize().getWidth());
            resultItem.setHeight(photo.getLargeSize().getHeight());
        } else if (!photo.getSizes().isEmpty()) {
            Collection<Size> sizes = photo.getSizes();
            for (Size size : sizes) {
                if (size != null && (resultItem.getWidth() == null || size.getWidth() > resultItem.getWidth())) {
                    resultItem.setWidth(size.getWidth());
                    resultItem.setHeight(size.getHeight());
                }
            }
        }

        if (photo.getOwner() != null) {
            resultItem.setAuthor(photo.getOwner().getUsername());
            resultItem.setAuthorUrl("https://www.flickr.com/photos/" + photo.getOwner().getId());
        }

        if (photo.getDatePosted() != null) {
            resultItem.setDate(DateUtils.format(photo.getDatePosted().getTime()));
        }
        if (photo.getTags() != null) {
            for (Tag tag : photo.getTags()) {
                resultItem.getTags().add(tag.getValue());
            }
        }
        resultItem.setCommentsCount((long) photo.getComments());

        resultItem.setThumbnailSmall(createThumbnail(photo.getSmallSize()));
        resultItem.setThumbnailMedium(createThumbnail(photo.getMediumSize()));
        resultItem.setThumbnailLarge(createThumbnail(photo.getLargeSize()));
        resultItem.setThumbnailOriginal(createThumbnail(photo.getOriginalSize()));

        return resultItem;
    }

    private String[] createTags(String query) {
        return query.split("[\\W]+");
    }

    private String getMediaType(SearchQuery query) {
        Assertions.notNull(query, "query");

        if (query.getContentTypes().size() == 1 && query.getContentTypes().contains(ContentType.image)) {
            return MEDIA_PHOTOS;
        } else if (query.getContentTypes().size() == 1 && query.getContentTypes().contains(ContentType.video)) {
            return MEDIA_VIDEOS;
        } else {
            return MEDIA_ALL;
        }
    }

    private static int convertRanking(SearchRanking ranking) {
        return switch (ranking) {
            case date -> SearchParameters.DATE_POSTED_DESC;
            case interestingness -> SearchParameters.INTERESTINGNESS_DESC;
            default -> SearchParameters.RELEVANCE;
        };
    }

    private static Set<String> getExtras() {
        return Set.of("description", "tags", "owner_name", "date_upload", "views", "media", "url_t", "url_s", "url_m", "url_l", "url_o");
    }
}
