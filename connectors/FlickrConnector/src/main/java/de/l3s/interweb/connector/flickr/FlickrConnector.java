package de.l3s.interweb.connector.flickr;

import java.time.Instant;

import jakarta.enterprise.context.Dependent;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import de.l3s.interweb.connector.flickr.entity.PhotoItem;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.search.*;
import de.l3s.interweb.core.util.DateUtils;

@Dependent
public class FlickrConnector implements SearchConnector {
    private static final Logger log = Logger.getLogger(FlickrConnector.class);
    private static final int fallbackPerPage = 500;

    @RestClient
    FlickrSearchClient searchClient;

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
        return new ContentType[]{ContentType.images, ContentType.videos};
    }

    @Override
    public Uni<SearchConnectorResults> search(SearchQuery query) throws ConnectorException {
        return searchClient.search(
                query.getQuery(),
                FlickrUtils.getMediaType(query),
                DateUtils.toEpochSecond(query.getDateFrom()),
                DateUtils.toEpochSecond(query.getDateTo()),
                FlickrUtils.convertSort(query.getSort()),
                query.getPage(),
                query.getPerPage(fallbackPerPage)
        ).map(response -> {
            SearchConnectorResults queryResult = new SearchConnectorResults();
            queryResult.setTotalResults(response.getPhotos().getTotal());

            int rank = query.getOffset();
            for (PhotoItem photo : response.getPhotos().getPhoto()) {
                SearchItem resultItem = createResultItem(photo, ++rank);
                queryResult.addResultItem(resultItem);
            }
            return queryResult;
        });
    }

    private SearchItem createResultItem(PhotoItem photo, int rank) {
        SearchItem resultItem = new SearchItem(rank);
        resultItem.setId(photo.getId());
        resultItem.setType("photo".equals(photo.getMedia()) ? ContentType.images : ContentType.videos);
        resultItem.setTitle(photo.getTitle());
        resultItem.setDescription(photo.getDescription().getContent());
        resultItem.setUrl("https://flickr.com/photos/" + photo.getPathAlias() + "/" + photo.getId());

        if (photo.getOriginalWidth() != null) {
            resultItem.setWidth(photo.getOriginalWidth());
            resultItem.setHeight(photo.getOriginalHeight());
        } else if (photo.getMediaOriginalWidth() != null) {
            resultItem.setWidth(photo.getMediaOriginalWidth());
            resultItem.setHeight(photo.getMediaOriginalHeight());
        } else if (photo.getMediaLargeWidth() != null) {
            resultItem.setWidth(photo.getMediaLargeWidth());
            resultItem.setHeight(photo.getMediaLargeHeight());
        } else if (photo.getMediaMediumWidth() != null) {
            resultItem.setWidth(photo.getMediaMediumWidth());
            resultItem.setHeight(photo.getMediaMediumHeight());
        }

        if (photo.getOwner() != null) {
            resultItem.setAuthor(photo.getOwnerName());
            resultItem.setAuthorUrl("https://www.flickr.com/photos/" + photo.getPathAlias());
        }

        if (photo.getDateUpload() != null) {
            resultItem.setDate(Instant.ofEpochSecond(photo.getDateUpload()));
        }

        if (photo.getTags() != null) {
            for (String tag : photo.getTags().split(" ")) {
                resultItem.getTags().add(tag);
            }
        }

        resultItem.setViewsCount(photo.getViews());

        if (photo.getMediaOriginalUrl() != null) {
            resultItem.setThumbnailOriginal(new Thumbnail(photo.getMediaOriginalUrl(), photo.getMediaOriginalWidth(), photo.getMediaOriginalHeight()));
        }

        if (photo.getMediaLargeUrl() != null) {
            resultItem.setThumbnailLarge(new Thumbnail(photo.getMediaLargeUrl(), photo.getMediaLargeWidth(), photo.getMediaLargeHeight()));
        }

        if (photo.getMediaMediumUrl() != null) {
            resultItem.setThumbnailMedium(new Thumbnail(photo.getMediaMediumUrl(), photo.getMediaMediumWidth(), photo.getMediaMediumHeight()));
        }

        if (photo.getMediaSmallUrl() != null) {
            resultItem.setThumbnailSmall(new Thumbnail(photo.getMediaSmallUrl(), photo.getMediaSmallWidth(), photo.getMediaSmallHeight()));
        }

        return resultItem;
    }
}
