package de.l3s.interweb.connector.youtube;

import java.io.IOException;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.Dependent;

import org.jboss.logging.Logger;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.search.*;
import de.l3s.interweb.core.search.Thumbnail;
import de.l3s.interweb.core.util.Assertions;
import de.l3s.interweb.core.util.DateUtils;

/**
 * YouTube is an American online video-sharing platform headquartered in San Bruno, California.
 * TODO missing search implementations: extras, search_in.
 *
 * @see <a href="https://developers.google.com/youtube/v3/docs/search/list">YouTube Search API</a>
 */
@Dependent
public class YouTubeConnector implements SearchConnector {
    private static final Logger log = Logger.getLogger(YouTubeConnector.class);

    public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    public static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    /**
     * Because there is no easy way to get next page (like settings offset), we need to store a token to the next page for each query.
     */
    private static final Cache<Integer, HashMap<Integer, String>> tokensCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();

    @Override
    public String getName() {
        return "YouTube";
    }

    @Override
    public String getBaseUrl() {
        return "https://www.youtube.com/";
    }

    @Override
    public ContentType[] getSearchTypes() {
        return new ContentType[]{ContentType.video};
    }

    @Override
    public SearchConnectorResults search(SearchQuery query, AuthCredentials credentials) throws ConnectorException {
        Assertions.notNull(query, "query");

        SearchConnectorResults queryResult = new SearchConnectorResults();

        try {
            // This object is used to make YouTube Data API requests. The last argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override the interface and provide a no-op function.
            YouTube youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, request -> {
            }).setApplicationName("Interweb").build();

            YouTube.Search.List search = createSearch(query, youtube, credentials);

            /*
             * The pageToken parameter identifies a specific page in the result set that should be returned.
             * In an API response, the nextPageToken and prevPageToken properties identify other pages that could be retrieved.
             */
            if (query.getPage() > 1) {
                HashMap<Integer, String> tokensMap = getTokensMap(query);
                if (tokensMap.containsKey(query.getPage())) {
                    search.setPageToken(tokensMap.get(query.getPage()));
                } else if (tokensMap.containsKey(-1)) {
                    log.warnv("No more results for page {0}", query.getPage());
                    return queryResult;
                } else {
                    log.warn("YouTube does not support search by specific page numbers without requesting previous page.");
                    return queryResult;
                }
            }

            SearchListResponse searchResponse = search.execute();

            // Limit of YouTube data API, see more: http://stackoverflow.com/questions/23255957
            queryResult.setTotalResults(Math.min(searchResponse.getPageInfo().getTotalResults(), 500));

            if (searchResponse.getNextPageToken() != null) {
                log.debugv("Next page {0} its token {1}", query.getPage() + 1, searchResponse.getNextPageToken());
                getTokensMap(query).put(query.getPage() + 1, searchResponse.getNextPageToken());
            } else {
                log.debug("No more results");
                getTokensMap(query).put(-1, "no-more-pages");
            }

            // at this point we will have video ID and snippet information
            List<SearchResult> searchResultList = searchResponse.getItems();

            if (searchResultList == null || searchResultList.isEmpty()) {
                return queryResult;
            }

            HashMap<String, Video> videosDetails = new HashMap<>();
            // in the next step we check if we need to request additional information
            if (query.getExtras() != null && !query.getExtras().isEmpty()) {
                List<String> videoIds = new ArrayList<>();
                for (SearchResult searchResult : searchResultList) {
                    videoIds.add(searchResult.getId().getVideoId());
                }

                List<String> list = new ArrayList<>();
                if (query.getExtras().contains(SearchExtra.tags)) {
                    list.add("snippet");
                }
                if (query.getExtras().contains(SearchExtra.statistics)) {
                    list.add("statistics");
                }
                if (query.getExtras().contains(SearchExtra.duration)) {
                    list.add("contentDetails");
                }

                // Call the YouTube Data API's youtube.videos.list method to retrieve additional information for the specified videos
                VideoListResponse videosResponse = youtube.videos().list(list).setId(videoIds).setKey(credentials.getKey()).execute();

                for (Video video : videosResponse.getItems()) {
                    videosDetails.put(video.getId(), video);
                }
            }

            int rank = query.getPerPage() * (query.getPage() - 1);
            for (SearchResult video : searchResultList) {
                SearchItem resultItem = createResultItem(video, videosDetails.get(video.getId().getVideoId()), rank++);
                if (resultItem != null) {
                    queryResult.addResultItem(resultItem);
                }
            }
        } catch (Throwable e) {
            throw new ConnectorException(e);
        }

        return queryResult;
    }

    private static YouTube.Search.List createSearch(SearchQuery query, final YouTube youtube, AuthCredentials credentials) throws IOException {
        // Define the API request for retrieving search results.
        YouTube.Search.List search = youtube.search().list(Arrays.asList("id", "snippet"));

        // Set your developer key from the {{ Google Cloud Console }} for non-authenticated requests.
        search.setKey(credentials.getKey());
        search.setRelevanceLanguage(query.getLanguage());

        if (query.getQuery().startsWith("user::")) {
            String[] splitQuery = query.getQuery().split(" ", 2);
            ChannelListResponse channelListResponse = youtube.channels().list(Collections.singletonList("id"))
                    .setKey(credentials.getKey()).setForUsername(splitQuery[0].substring(6)).execute();

            search.setChannelId(channelListResponse.getItems().get(0).getId());

            if (splitQuery.length > 1 && splitQuery[1] != null) {
                search.setQ(splitQuery[1]);
            }
        } else {
            search.setQ(query.getQuery());
        }

        if (query.getDateFrom() != null) {
            DateTime dateFrom = new DateTime(query.getDateFrom().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());
            search.setPublishedAfter(dateFrom.toStringRfc3339());
        }

        if (query.getDateTill() != null) {
            DateTime dateTill = new DateTime(query.getDateTill().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());
            search.setPublishedBefore(dateTill.toStringRfc3339());
        }

        search.setType(Collections.singletonList("video")); // Restrict the search results to only include videos.
        search.setMaxResults((long) query.getPerPage());
        search.setOrder(convertRanking(query.getRanking()));

        search.setMaxResults((long) query.getPerPage());
        return search;
    }

    private static String convertRanking(SearchRanking ranking) {
        return switch (ranking) {
            case date -> "date";
            case interestingness -> "viewCount";
            default -> "relevance";
        };
    }

    private HashMap<Integer, String> getTokensMap(SearchQuery query) {
        try {
            return tokensCache.get(query.hashCodeWithoutPage(), HashMap::new);
        } catch (ExecutionException ignored) {
            return new HashMap<>(); // actually, never happens
        }
    }

    private SearchItem createResultItem(SearchResult searchResult, Video videoResult, int rank) throws ConnectorException {
        SearchItem resultItem = new SearchItem(rank);
        resultItem.setType(ContentType.video);

        if (searchResult != null) {
            // Confirm that the result represents a video. Otherwise, the item will not contain a video ID.
            if (!searchResult.getId().getKind().equals("youtube#video")) {
                return null;
            }

            resultItem.setId(searchResult.getId().getVideoId());

            SearchResultSnippet vSnippet = searchResult.getSnippet();
            if (vSnippet != null) {
                if (vSnippet.getTitle() != null) {
                    resultItem.setTitle(vSnippet.getTitle());
                }
                if (vSnippet.getDescription() != null) {
                    resultItem.setDescription(vSnippet.getDescription());
                }
                if (vSnippet.getChannelTitle() != null) {
                    resultItem.setAuthor(vSnippet.getChannelTitle());
                }
                if (vSnippet.getChannelId() != null) {
                    resultItem.setAuthorUrl("https://www.youtube.com/channel/" + vSnippet.getChannelId());
                }
                if (vSnippet.getPublishedAt() != null) {
                    resultItem.setDate(DateUtils.format(vSnippet.getPublishedAt().getValue()));
                }

                ThumbnailDetails thumbnails = vSnippet.getThumbnails();
                if (thumbnails.getMedium() != null) {
                    resultItem.setThumbnailSmall(createThumbnail(thumbnails.getMedium()));
                    resultItem.setWidth(240);
                    resultItem.setHeight(240);
                }
                if (thumbnails.getHigh() != null) {
                    resultItem.setThumbnailMedium(createThumbnail(thumbnails.getHigh()));
                    resultItem.setWidth(640);
                    resultItem.setHeight(480);
                }
                if (thumbnails.getMaxres() != null) {
                    resultItem.setThumbnailLarge(createThumbnail(thumbnails.getMaxres()));
                    resultItem.setWidth(1280);
                    resultItem.setHeight(720);
                }
            }
        } else if (videoResult != null) {
            // This is required for a case when `searchResult == null`, e.g. from `put` method
            if (!videoResult.getKind().equals("youtube#video")) {
                return null;
            }

            resultItem.setId(videoResult.getId());

            VideoSnippet vSnippet = videoResult.getSnippet();
            if (vSnippet != null) {
                if (vSnippet.getTitle() != null) {
                    resultItem.setTitle(vSnippet.getTitle());
                }
                if (vSnippet.getDescription() != null) {
                    resultItem.setDescription(vSnippet.getDescription());
                }
                if (vSnippet.getChannelTitle() != null) {
                    resultItem.setAuthor(vSnippet.getChannelTitle());
                }
                if (vSnippet.getChannelId() != null) {
                    resultItem.setAuthor("https://www.youtube.com/channel/" + vSnippet.getChannelId());
                }
                if (vSnippet.getPublishedAt() != null) {
                    resultItem.setDate(DateUtils.format(vSnippet.getPublishedAt().getValue()));
                }

                ThumbnailDetails thumbnails = vSnippet.getThumbnails();
                if (thumbnails.getMedium() != null) {
                    resultItem.setThumbnailSmall(createThumbnail(thumbnails.getMedium()));
                    resultItem.setWidth(240);
                    resultItem.setHeight(240);
                }
                if (thumbnails.getHigh() != null) {
                    resultItem.setThumbnailMedium(createThumbnail(thumbnails.getHigh()));
                    resultItem.setWidth(640);
                    resultItem.setHeight(480);
                }
                if (thumbnails.getMaxres() != null) {
                    resultItem.setThumbnailLarge(createThumbnail(thumbnails.getMaxres()));
                    resultItem.setWidth(1280);
                    resultItem.setHeight(720);
                }
            }
        }

        if (videoResult != null) {
            VideoSnippet vSnippet = videoResult.getSnippet();
            if (vSnippet != null) {
                if (vSnippet.getTags() != null) {
                    resultItem.getTags().addAll(vSnippet.getTags());
                }
            }

            VideoStatistics vStatistics = videoResult.getStatistics();
            if (vStatistics != null) {
                if (vStatistics.getViewCount() != null) {
                    resultItem.setViewsCount(vStatistics.getViewCount().longValue());
                }
                if (vStatistics.getCommentCount() != null) {
                    resultItem.setViewsCount(vStatistics.getCommentCount().longValue());
                }
            }

            VideoContentDetails vContentDetails = videoResult.getContentDetails();
            if (vContentDetails != null) {
                if (vContentDetails.getDuration() != null) {
                    resultItem.setDuration(Duration.parse(vContentDetails.getDuration()).get(ChronoUnit.SECONDS));
                }
            }

            VideoFileDetails vFileDetails = videoResult.getFileDetails();
            if (vFileDetails != null) {
                if (vFileDetails.getVideoStreams() != null) {
                    resultItem.setWidth(vFileDetails.getVideoStreams().get(0).getWidthPixels().intValue());
                    resultItem.setHeight(vFileDetails.getVideoStreams().get(0).getHeightPixels().intValue());
                }
            }
        }

        resultItem.setUrl("https://www.youtube.com/watch?v=" + resultItem.getId());
        resultItem.setEmbeddedUrl("https://www.youtube-nocookie.com/embed/" + resultItem.getId());
        return resultItem;
    }

    protected static ZonedDateTime parseDate(String dateString) throws ConnectorException {
        if (dateString == null) {
            return null;
        }

        try {
            return ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_ZONED_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new ConnectorException("dateString: [" + dateString + "] " + e.getMessage());
        }
    }

    private static Thumbnail createThumbnail(com.google.api.services.youtube.model.Thumbnail vThumbnail) {
        return new Thumbnail(vThumbnail.getUrl(), vThumbnail.getWidth().intValue(), vThumbnail.getHeight().intValue());
    }
}
