package de.l3s.interweb.connector.flickr;

import java.time.Instant;
import java.util.regex.Pattern;

import jakarta.enterprise.context.Dependent;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import de.l3s.interweb.connector.flickr.entity.InfoPhoto;
import de.l3s.interweb.connector.flickr.entity.PhotoItem;
import de.l3s.interweb.connector.flickr.entity.Tag;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.describe.DescribeConnector;
import de.l3s.interweb.core.describe.DescribeQuery;
import de.l3s.interweb.core.describe.DescribeResults;
import de.l3s.interweb.core.search.*;
import de.l3s.interweb.core.util.DateUtils;

@Dependent
public class FlickrConnector implements SearchConnector, DescribeConnector {
    private static final Pattern pattern = Pattern.compile("(?:https?:)?//(?:www\\.)?(?:flickr\\.com/photos/[^/]+/(\\d+)|(?:flic\\.kr/p/|flickr\\.com/photo\\.gne\\?short=)(\\w+))", Pattern.CASE_INSENSITIVE);
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
        return new ContentType[]{ContentType.image, ContentType.video};
    }

    @Override
    public Pattern getLinkPattern() {
        return pattern;
    }

    @Override
    public Uni<DescribeResults> describe(DescribeQuery query) throws ConnectorException {
        return searchClient.getInfo(query.getId()).map(Unchecked.function(response -> {
            if (response.getPhoto() == null) {
                throw new ConnectorException("No results");
            }

            return new DescribeResults(createResultItem(response.getPhoto()));
        }));
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

    private SearchItem createResultItem(InfoPhoto photo) {
        SearchItem resultItem = new SearchItem();
        resultItem.setId(photo.getId());
        resultItem.setType("photo".equals(photo.getMedia()) ? ContentType.image : ContentType.video);
        resultItem.setTitle(photo.getTitle());
        resultItem.setDescription(photo.getDescription());

        if (photo.getOwner() != null) {
            resultItem.setAuthor(photo.getOwner().getRealname());
            resultItem.setUrl("https://flickr.com/photos/" + photo.getOwner().getPathAlias() + "/" + photo.getId());
            resultItem.setAuthorUrl("https://www.flickr.com/photos/" + photo.getOwner().getPathAlias());
        }

        if (photo.getDateUploaded() != null) {
            resultItem.setDate(Instant.ofEpochSecond(photo.getDateUploaded()));
        }

        if (photo.getTags() != null) {
            for (Tag tag : photo.getTags().getTag()) {
                resultItem.getTags().add(tag.getRaw());
            }
        }

        if (photo.getComments() != null) {
            resultItem.setCommentsCount(Long.parseLong(photo.getComments()));
        }
        resultItem.setViewsCount(photo.getViews());

        resultItem.setThumbnailSmall(new Thumbnail("https://live.staticflickr.com/" + photo.getServer() + "/" + photo.getId() + "_" + photo.getSecret() + "_m.jpg", 240, null));
        resultItem.setThumbnailMedium(new Thumbnail("https://live.staticflickr.com/" + photo.getServer() + "/" + photo.getId() + "_" + photo.getSecret() + ".jpg", 500, null));
        resultItem.setThumbnailLarge(new Thumbnail("https://live.staticflickr.com/" + photo.getServer() + "/" + photo.getId() + "_" + photo.getSecret() + "_b.jpg", 1024, null));
        // not always available
        resultItem.setThumbnailOriginal(new Thumbnail("https://live.staticflickr.com/" + photo.getServer() + "/" + photo.getId() + "_" + photo.getSecret() + "_o.jpg"));
        return resultItem;
    }

    private SearchItem createResultItem(PhotoItem photo, int rank) {
        SearchItem resultItem = new SearchItem(rank);
        resultItem.setId(photo.getId());
        resultItem.setType("photo".equals(photo.getMedia()) ? ContentType.image : ContentType.video);
        resultItem.setTitle(photo.getTitle());
        resultItem.setDescription(photo.getDescription());
        resultItem.setUrl("https://flickr.com/photos/" + photo.getPathAlias() + "/" + photo.getId());
        resultItem.setAuthor(photo.getOwnerName());
        resultItem.setAuthorUrl("https://www.flickr.com/photos/" + photo.getPathAlias());

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
