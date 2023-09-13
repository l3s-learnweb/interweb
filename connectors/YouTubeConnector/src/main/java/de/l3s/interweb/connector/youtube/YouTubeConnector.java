package de.l3s.interweb.connector.youtube;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
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
    private static final int fallbackPerPage = 50;

    @RestClient
    YouTubeSearchClient searchClient;

    /**
     * Because there is no easy way to get next page (like settings offset), we need to store a token to the next page for each query.
     */
    @Inject
    @CacheName("youtube-tokens")
    Cache cache;

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
        return new ContentType[]{ContentType.videos};
    }

    @Override
    public Uni<SearchConnectorResults> search(SearchQuery query) throws ConnectorException {
        return processSearch(query).chain(response -> processResponse(query, response));
    }

    private Uni<ListResponse> processSearch(SearchQuery query) throws ConnectorException {
        /*
         * The pageToken parameter identifies a specific page in the result set that should be returned.
         * In an API response, the nextPageToken and prevPageToken properties identify other pages that could be retrieved.
         */
        Uni<String> pageUni = Uni.createFrom().nullItem();
        if (query.getPage() > 1) {
            pageUni = getTokensMap(query).map(Unchecked.function(tokensMap -> {
                if (tokensMap.containsKey(query.getPage())) {
                    return tokensMap.get(query.getPage());
                } else if (tokensMap.containsKey(-1)) {
                    throw new ConnectorException("No more results");
                } else {
                    throw new ConnectorException("YouTube does not support search by specific page numbers without requesting previous page.");
                }
            }));
        }

        return pageUni.chain(pageToken -> {
            String q = query.getQuery();
            Uni<String> channelUni = Uni.createFrom().nullItem();
            if (query.getQuery().startsWith("user::")) {
                String[] splitQuery = query.getQuery().split(" ", 2);
                String username = splitQuery[0].substring(6);
                if (splitQuery.length > 1 && splitQuery[1] != null) {
                    q = splitQuery[1];
                }

                channelUni = searchClient.channels(username).map(response -> {
                    if (!response.items().isEmpty()) {
                        return response.items().get(0).id();
                    }
                    return null;
                });
            }

            final String finalQ = q;
            return channelUni.chain(channelId -> searchClient.search(
                    finalQ,
                    channelId,
                    DateUtils.toRfc3339(query.getDateFrom()),
                    DateUtils.toRfc3339(query.getDateTo()),
                    query.getPerPage(fallbackPerPage),
                    query.getLanguage(),
                    YouTubeUtils.convertSort(query.getSort()),
                    pageToken
            ));
        });
    }

    private Uni<SearchConnectorResults> processResponse(SearchQuery query, ListResponse response) throws ConnectorException {
        return getTokensMap(query).invoke(tokensMap -> {
            if (response.nextPageToken() != null) {
                log.debugv("Next page {0} its token {1}", query.getPage() + 1, response.nextPageToken());
                tokensMap.put(query.getPage() + 1, response.nextPageToken());
            } else {
                log.debug("No more results");
                tokensMap.put(-1, "no-more-pages");
            }
        }).flatMap(ignored -> {
            // at this point we will have video ID and snippet information
            final long total = response.pageInfo().totalResults();
            final List<ListItem> videoItemList = response.items();

            if (videoItemList == null || videoItemList.isEmpty()) {
                return null;
            }

            Uni<HashMap<String, ListItem>> detailsUni = Uni.createFrom().item(new HashMap<>());
            // in the next step we check if we need to request additional information
            if (query.getExtras() != null && !query.getExtras().isEmpty()) {
                final List<String> videoIds = new ArrayList<>();
                for (ListItem listItem : videoItemList) {
                    videoIds.add(listItem.id());
                }

                List<String> parts = new ArrayList<>();
                if (query.getExtras().contains(SearchExtra.tags)) {
                    parts.add("snippet");
                }
                if (query.getExtras().contains(SearchExtra.stats)) {
                    parts.add("statistics");
                }
                if (query.getExtras().contains(SearchExtra.duration)) {
                    parts.add("contentDetails");
                }

                // Call the YouTube Data API's youtube.videos.list method to retrieve additional information for the specified videos
                detailsUni = searchClient.videos(String.join(",", parts), String.join(",", videoIds)).map(detailsResponse -> {
                    final HashMap<String, ListItem> videosDetails = new HashMap<>();
                    for (ListItem video : detailsResponse.items()) {
                        videosDetails.put(video.id(), video);
                    }
                    return videosDetails;
                });
            }

            return detailsUni.map(details -> {
                SearchConnectorResults queryResult = new SearchConnectorResults();
                // Limit of YouTube data API, see more: http://stackoverflow.com/questions/23255957
                queryResult.setTotalResults(Math.min(total, 500));

                int rank = query.getOffset();
                for (ListItem video : videoItemList) {
                    SearchItem resultItem = new SearchItem(++rank);
                    resultItem.setType(ContentType.videos);

                    YouTubeUtils.updateSearchItem(resultItem, video);
                    YouTubeUtils.updateSearchItem(resultItem, details.get(video.id()));

                    resultItem.setUrl("https://www.youtube.com/watch?v=" + resultItem.getId());
                    resultItem.setEmbeddedUrl("https://www.youtube-nocookie.com/embed/" + resultItem.getId());
                    queryResult.addResultItem(resultItem);
                }
                return queryResult;
            });
        });
    }

    private Uni<HashMap<Integer, String>> getTokensMap(SearchQuery query) {
        return cache.get(query.hashCodeWithoutPage(), ignored -> new HashMap<>());
    }
}
