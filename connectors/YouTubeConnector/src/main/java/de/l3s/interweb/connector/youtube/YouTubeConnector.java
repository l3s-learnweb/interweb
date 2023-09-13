package de.l3s.interweb.connector.youtube;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.Dependent;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import de.l3s.interweb.connector.youtube.entity.ListItem;
import de.l3s.interweb.connector.youtube.entity.ListResponse;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.search.*;
import de.l3s.interweb.core.util.DateUtils;

@Dependent
public class YouTubeConnector implements SearchConnector {
    private static final Logger log = Logger.getLogger(YouTubeConnector.class);

    @RestClient
    YouTubeSearchClient searchClient;

    /**
     * Because there is no easy way to get next page (like settings offset), we need to store a token to the next page for each query.
     */
    // private static final Cache<Integer, HashMap<Integer, String>> tokensCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();
    private static final Map<Integer, HashMap<Integer, String>> tokensCache = new HashMap<>();

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
    public SearchConnectorResults search(SearchQuery query) throws ConnectorException {
        try {
            /*
             * The pageToken parameter identifies a specific page in the result set that should be returned.
             * In an API response, the nextPageToken and prevPageToken properties identify other pages that could be retrieved.
             */
            String pageToken = null;
            if (query.getPage() > 1) {
                HashMap<Integer, String> tokensMap = getTokensMap(query);
                if (tokensMap.containsKey(query.getPage())) {
                    pageToken = tokensMap.get(query.getPage());
                } else if (tokensMap.containsKey(-1)) {
                    throw new ConnectorException("No more results");
                } else {
                    throw new ConnectorException("YouTube does not support search by specific page numbers without requesting previous page.");
                }
            }

            String q = query.getQuery();
            String channelId = null;
            if (query.getQuery().startsWith("user::")) {
                String[] splitQuery = query.getQuery().split(" ", 2);

                var channelListResponse = searchClient.channels(splitQuery[0].substring(6)).await().indefinitely();
                channelId = channelListResponse.items().get(0).id();

                if (splitQuery.length > 1 && splitQuery[1] != null) {
                    q = splitQuery[1];
                }
            }

            ListResponse response = searchClient.search(
                    q,
                    channelId,
                    DateUtils.toRfc3339(query.getDateFrom()),
                    DateUtils.toRfc3339(query.getDateTill()),
                    query.getPerPage(50),
                    query.getLanguage(),
                    YouTubeUtils.convertRanking(query.getRanking()),
                    pageToken
            ).await().indefinitely();

            SearchConnectorResults queryResult = new SearchConnectorResults();
            // Limit of YouTube data API, see more: http://stackoverflow.com/questions/23255957
            queryResult.setTotalResults(Math.min(response.pageInfo().totalResults(), 500));

            if (response.nextPageToken() != null) {
                log.debugv("Next page {0} its token {1}", query.getPage() + 1, response.nextPageToken());
                getTokensMap(query).put(query.getPage() + 1, response.nextPageToken());
            } else {
                log.debug("No more results");
                getTokensMap(query).put(-1, "no-more-pages");
            }

            // at this point we will have video ID and snippet information
            List<ListItem> videoItemList = response.items();

            if (videoItemList == null || videoItemList.isEmpty()) {
                return queryResult;
            }

            HashMap<String, ListItem> videosDetails = new HashMap<>();
            // in the next step we check if we need to request additional information
            if (query.getExtras() != null && !query.getExtras().isEmpty()) {
                List<String> videoIds = new ArrayList<>();
                for (ListItem listItem : videoItemList) {
                    videoIds.add(listItem.id());
                }

                List<String> parts = new ArrayList<>();
                if (query.getExtras().contains(SearchExtra.tags)) {
                    parts.add("snippet");
                }
                if (query.getExtras().contains(SearchExtra.statistics)) {
                    parts.add("statistics");
                }
                if (query.getExtras().contains(SearchExtra.duration)) {
                    parts.add("contentDetails");
                }

                // Call the YouTube Data API's youtube.videos.list method to retrieve additional information for the specified videos
                ListResponse videosResponse = searchClient.videos(String.join(",", parts), String.join(",", videoIds)).await().indefinitely();

                for (ListItem video : videosResponse.items()) {
                    videosDetails.put(video.id(), video);
                }
            }

            int rank = query.getPerPage() * (query.getPage() - 1);
            for (ListItem video : videoItemList) {
                SearchItem resultItem = new SearchItem(rank++);
                resultItem.setType(ContentType.video);

                YouTubeUtils.updateSearchItem(resultItem, video);
                YouTubeUtils.updateSearchItem(resultItem, videosDetails.get(video.id()));

                resultItem.setUrl("https://www.youtube.com/watch?v=" + resultItem.getId());
                resultItem.setEmbeddedUrl("https://www.youtube-nocookie.com/embed/" + resultItem.getId());
                queryResult.addResultItem(resultItem);
            }
            return queryResult;
        } catch (Throwable e) {
            throw new ConnectorException(e);
        }
    }

    private HashMap<Integer, String> getTokensMap(SearchQuery query) {
        return tokensCache.computeIfAbsent(query.hashCodeWithoutPage(), k -> new HashMap<>());
    }
}
