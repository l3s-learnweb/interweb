package de.l3s.interweb.connector.flickr;

import de.l3s.interweb.core.search.ContentType;
import de.l3s.interweb.core.search.SearchQuery;
import de.l3s.interweb.core.search.SearchRanking;

public final class FlickrUtils {
    private static final String MEDIA_ALL = "all";
    private static final String MEDIA_PHOTOS = "photos";
    private static final String MEDIA_VIDEOS = "videos";

    static String getMediaType(SearchQuery query) {
        if (query.getContentTypes().size() == 1) {
            if (query.getContentTypes().contains(ContentType.image)) {
                return MEDIA_PHOTOS;
            } else if (query.getContentTypes().contains(ContentType.video)) {
                return MEDIA_VIDEOS;
            }
        }
        return MEDIA_ALL;
    }

    static String getRanking(SearchRanking ranking) {
        return switch (ranking) {
            case date -> "date-posted-desc";
            case dateReverse -> "date-posted-asc";
            case interestingness -> "interestingness-desc";
            case interestingnessReverse -> "interestingness-asc";
            default -> "relevance";
        };
    }
}
