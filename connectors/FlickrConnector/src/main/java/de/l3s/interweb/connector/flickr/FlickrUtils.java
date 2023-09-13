package de.l3s.interweb.connector.flickr;

import de.l3s.interweb.core.search.ContentType;
import de.l3s.interweb.core.search.SearchQuery;
import de.l3s.interweb.core.search.SearchSort;

public final class FlickrUtils {
    private static final String MEDIA_ALL = "all";
    private static final String MEDIA_PHOTOS = "photos";
    private static final String MEDIA_VIDEOS = "videos";

    static String getMediaType(SearchQuery query) {
        if (query.getContentTypes().size() == 1) {
            if (query.getContentTypes().contains(ContentType.images)) {
                return MEDIA_PHOTOS;
            } else if (query.getContentTypes().contains(ContentType.videos)) {
                return MEDIA_VIDEOS;
            }
        }
        return MEDIA_ALL;
    }

    static String convertSort(SearchSort sort) {
        return switch (sort) {
            case date -> "date-posted-desc";
            case popularity -> "interestingness-desc";
            default -> "relevance";
        };
    }
}
