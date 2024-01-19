package de.l3s.interweb.connector.youtube;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import de.l3s.interweb.connector.youtube.entity.*;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.search.SearchItem;
import de.l3s.interweb.core.search.SearchSort;
import de.l3s.interweb.core.util.DateUtils;

public final class YouTubeUtils {

    private YouTubeUtils() {
    }

    static String convertSort(SearchSort sort) {
        return switch (sort) {
            case date -> "date";
            case popularity -> "viewCount";
            default -> "relevance";
        };
    }

    static void updateSearchItem(SearchItem searchItem, ListItem item) throws ConnectorException {
        // Confirm that the result represents a video. Otherwise, the item will not contain a video ID.
        if (item == null) return;
        searchItem.setId(item.id());

        Snippet vSnippet = item.snippet();
        if (vSnippet != null) {
            if (vSnippet.title() != null) {
                searchItem.setTitle(vSnippet.title());
            }
            if (vSnippet.description() != null) {
                searchItem.setDescription(vSnippet.description());
            }
            if (vSnippet.channelTitle() != null) {
                searchItem.setAuthor(vSnippet.channelTitle());
            }
            if (vSnippet.channelId() != null) {
                searchItem.setAuthorUrl("https://www.youtube.com/channel/" + vSnippet.channelId());
            }
            if (vSnippet.publishedAt() != null) {
                searchItem.setDate(DateUtils.parse(vSnippet.publishedAt()));
            }
            if (vSnippet.tags() != null) {
                searchItem.getTags().addAll(vSnippet.tags());
            }

            Thumbnails thumbnails = vSnippet.thumbnails();
            if (thumbnails.medium() != null) {
                searchItem.setThumbnailSmall(thumbnails.medium().toThumbnail());
                searchItem.setWidth(240);
                searchItem.setHeight(240);
            }
            if (thumbnails.high() != null) {
                searchItem.setThumbnailMedium(thumbnails.high().toThumbnail());
                searchItem.setWidth(640);
                searchItem.setHeight(480);
            }
            if (thumbnails.maxres() != null) {
                searchItem.setThumbnailLarge(thumbnails.maxres().toThumbnail());
                searchItem.setWidth(1280);
                searchItem.setHeight(720);
            }
        }

        Statistics vStatistics = item.statistics();
        if (vStatistics != null) {
            if (vStatistics.viewCount() != null) {
                searchItem.setViewsCount(vStatistics.viewCount());
            }
            if (vStatistics.commentCount() != null) {
                searchItem.setViewsCount(vStatistics.commentCount());
            }
        }

        ContentDetails vContentDetails = item.contentDetails();
        if (vContentDetails != null && vContentDetails.duration() != null) {
            searchItem.setDuration(Duration.parse(vContentDetails.duration()).get(ChronoUnit.SECONDS));
        }
    }
}
