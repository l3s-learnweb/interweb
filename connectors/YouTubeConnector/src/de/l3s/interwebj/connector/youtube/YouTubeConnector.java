package de.l3s.interwebj.connector.youtube;

import static de.l3s.interwebj.core.util.Assertions.notNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.google.api.services.youtube.model.ThumbnailDetails;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoContentDetails;
import com.google.api.services.youtube.model.VideoFileDetails;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatistics;
import com.google.api.services.youtube.model.VideoStatus;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.Parameters;
import de.l3s.interwebj.core.connector.ConnectorSearchResults;
import de.l3s.interwebj.core.connector.ServiceConnector;
import de.l3s.interwebj.core.core.InterWebPrincipal;
import de.l3s.interwebj.core.query.ContentType;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.ResultItem;
import de.l3s.interwebj.core.query.SearchExtra;
import de.l3s.interwebj.core.query.SearchRanking;
import de.l3s.interwebj.core.query.Thumbnail;
import de.l3s.interwebj.core.util.CoreUtils;

/**
 * YouTube is an American online video-sharing platform headquartered in San Bruno, California.
 * TODO missing search implementations: extras, search_in.
 *
 * @see <a href="https://developers.google.com/youtube/v3/docs/search/list">YouTube Search API</a>
 */
public class YouTubeConnector extends ServiceConnector {
    private static final Logger log = LogManager.getLogger(YouTubeConnector.class);

    private static final String API_KEY = "***REMOVED***";
    private static final List<String> SCOPES = Arrays.asList("profile", "https://www.googleapis.com/auth/youtube", "https://www.googleapis.com/auth/youtube.upload");
    public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    public static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static GoogleAuthorizationCodeFlow flow = null;

    /**
     * Because there is no easy way to get next page (like settings offset), we need to store a token to the next page for each query.
     */
    private static final Cache<Integer, HashMap<Integer, String>> tokensCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();

    public YouTubeConnector() {
        super("YouTube", "https://www.youtube.com/", ContentType.video);
    }

    public YouTubeConnector(AuthCredentials consumerAuthCredentials) {
        this();
        setAuthCredentials(consumerAuthCredentials);
    }

    @Override
    public Parameters authenticate(String callbackUrl, Parameters parameters) throws InterWebException {
        if (!isRegistered()) {
            throw new InterWebException("Service is not yet registered");
        }

        Parameters params = new Parameters();
        params.add(Parameters.CALLBACK + "Auth", callbackUrl);

        try {
            String userName = "astappiev";
            String url = getFlow().newAuthorizationUrl().setRedirectUri(callbackUrl).setState(userName).build();

            params.add(Parameters.AUTHORIZATION_URL, url);

            log.info("requesting url: {}", url);
        } catch (Exception e) {
            throw new InterWebException(e);
        }

        return params;
    }

    @Override
    public ServiceConnector clone() {
        return new YouTubeConnector(getAuthCredentials());
    }

    @Override
    public AuthCredentials completeAuthentication(Parameters params) throws InterWebException {
        notNull(params, "params");
        if (!isRegistered()) {
            throw new InterWebException("Service is not yet registered");
        }

        String authorizationCode = params.get("code");
        log.info("authorization_code: {}", authorizationCode);

        AuthCredentials authCredentials;
        Credential cred;

        try {
            GoogleTokenResponse response = getFlow().newTokenRequest(authorizationCode).setRedirectUri(params.get(Parameters.CALLBACK + "Auth")).execute();
            cred = flow.createAndStoreCredential(response, null);

            authCredentials = new AuthCredentials(cred.getAccessToken(), cred.getRefreshToken());
        } catch (IOException e) {
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

        ConnectorSearchResults queryResult = new ConnectorSearchResults(query, getName());

        if (!query.getContentTypes().contains(ContentType.video)) {
            return queryResult;
        }

        try {
            // This object is used to make YouTube Data API requests. The last argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override the interface and provide a no-op function.
            YouTube youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, request -> {}).setApplicationName("Interweb").build();

            YouTube.Search.List search = createSearch(query, youtube);

            /*
             * The pageToken parameter identifies a specific page in the result set that should be returned.
             * In an API response, the nextPageToken and prevPageToken properties identify other pages that could be retrieved.
             */
            if (query.getPage() > 1) {
                HashMap<Integer, String> tokensMap = getTokensMap(query);
                if (tokensMap.containsKey(query.getPage())) {
                    search.setPageToken(tokensMap.get(query.getPage()));
                } else if (tokensMap.containsKey(-1)) {
                    log.warn("No more results for page {}", query.getPage());
                    return queryResult;
                } else {
                    log.warn("YouTube does not support search by specific page numbers without requesting previous page.");
                    return queryResult;
                }
            }

            SearchListResponse searchResponse = search.execute();

            // Limit of YouTube data API, see more: http://stackoverflow.com/questions/23255957
            queryResult.setTotalResultCount(Math.min(searchResponse.getPageInfo().getTotalResults(), 500));

            if (searchResponse.getNextPageToken() != null) {
                log.debug("Next page {} its token {}", query.getPage() + 1, searchResponse.getNextPageToken());
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
                VideoListResponse videosResponse = youtube.videos().list(list).setId(videoIds).setKey(API_KEY).execute();

                for (Video video : videosResponse.getItems()) {
                    videosDetails.put(video.getId(), video);
                }
            }

            int rank = query.getPerPage() * (query.getPage() - 1);
            for (SearchResult video : searchResultList) {
                ResultItem resultItem = createResultItem(video, videosDetails.get(video.getId().getVideoId()), rank++);
                if (resultItem != null) {
                    queryResult.addResultItem(resultItem);
                }
            }
        } catch (Throwable e) {
            throw new InterWebException(e);
        }

        return queryResult;
    }

    private static YouTube.Search.List createSearch(Query query, final YouTube youtube) throws IOException {
        // Define the API request for retrieving search results.
        YouTube.Search.List search = youtube.search().list(Arrays.asList("id", "snippet"));

        // Set your developer key from the {{ Google Cloud Console }} for non-authenticated requests.
        search.setKey(API_KEY);
        search.setRelevanceLanguage(query.getLanguage());

        if (query.getQuery().startsWith("user::")) {
            String[] splitQuery = query.getQuery().split(" ", 2);
            ChannelListResponse channelListResponse = youtube.channels().list(Collections.singletonList("id"))
                .setKey(API_KEY).setForUsername(splitQuery[0].substring(6)).execute();

            search.setChannelId(channelListResponse.getItems().get(0).getId());

            if (splitQuery.length > 1 && splitQuery[1] != null) {
                search.setQ(splitQuery[1]);
            }
        } else {
            search.setQ(query.getQuery());
        }

        if (query.getDateFrom() != null) {
            try {
                DateTime dateFrom = new DateTime(CoreUtils.parseDate(query.getDateFrom()).toInstant().toEpochMilli());
                search.setPublishedAfter(dateFrom.toStringRfc3339());
            } catch (DateTimeParseException e) {
                log.error("Failed to parse date {}", query.getDateFrom(), e);
            }
        }

        if (query.getDateTill() != null) {
            try {
                DateTime dateTill = new DateTime(CoreUtils.parseDate(query.getDateTill()).toInstant().toEpochMilli());
                search.setPublishedBefore(dateTill.toStringRfc3339());
            } catch (DateTimeParseException e) {
                log.error("Failed to parse date {}", query.getDateTill(), e);
            }
        }

        search.setType(Collections.singletonList("video")); // Restrict the search results to only include videos.
        search.setMaxResults((long) query.getPerPage());
        search.setOrder(convertRanking(query.getRanking()));

        search.setMaxResults((long) query.getPerPage());
        return search;
    }

    private static String convertRanking(SearchRanking ranking) {
        switch (ranking) {
            case date:
                return "date";
            case interestingness:
                return "viewCount";
            case relevance:
            default:
                return "relevance";
        }
    }

    private HashMap<Integer, String> getTokensMap(Query query) {
        try {
            return tokensCache.get(query.hashCodeWithoutPage(), HashMap::new);
        } catch (ExecutionException ignored) {
            return new HashMap<>(); // actually, never happens
        }
    }

    private ResultItem createResultItem(SearchResult searchResult, Video videoResult, int rank) throws InterWebException {
        ResultItem resultItem = new ResultItem(getName(), rank);
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
                    resultItem.setDate(CoreUtils.formatDate(vSnippet.getPublishedAt().getValue()));
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
                    resultItem.setDate(CoreUtils.formatDate(vSnippet.getPublishedAt().getValue()));
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
                    resultItem.setViewCount(vStatistics.getViewCount().longValue());
                }
                if (vStatistics.getCommentCount() != null) {
                    resultItem.setViewCount(vStatistics.getCommentCount().longValue());
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

    @Override
    public String getEmbedded(AuthCredentials authCredentials, String url, int maxWidth, int maxHeight) throws InterWebException {
        Pattern pattern = Pattern.compile(".*(?:youtu.be/|v/|u/\\w/|embed/|watch\\?v=)([^#&?]*).*");
        Matcher matcher = pattern.matcher(url);

        if (matcher.matches()) {
            String id = matcher.group(1);
            return "<iframe width=\"" + maxWidth + "\" height=\"" + maxHeight + "\" src=\"https://www.youtube.com/embed/" + id +
                "\" frameborder=\"0\" allowfullscreen></iframe>";
        }

        throw new InterWebException("URL: [" + url + "] doesn't belong to connector [" + getName() + "]");
    }

    @Override
    public String getUserId(AuthCredentials authCredentials) throws InterWebException {
        try {
            YouTube youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, getYoutubeCredential(authCredentials)).setApplicationName("Interweb").build();

            ChannelListResponse channelListResponse = youtube.channels().list(Arrays.asList("id", "contentDetails")).setMine(true).execute();

            return channelListResponse.getItems().get(0).getId();
        } catch (IOException e) {
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

        ResultItem resultItem;

        try {
            YouTube youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, getYoutubeCredential(authCredentials)).setApplicationName("Interweb").build();

            Video videoObjectDefiningMetadata = new Video();

            // Set the video to be publicly visible. This is the default setting. Other supporting settings are "unlisted" and "private."
            VideoStatus status = new VideoStatus();
            int privacy = Integer.parseInt(params.get(Parameters.PRIVACY, "0"));
            status.setPrivacyStatus(privacy > 0 ? "private" : "public");
            videoObjectDefiningMetadata.setStatus(status);

            VideoSnippet snippet = new VideoSnippet();

            String title = params.get(Parameters.TITLE, "No Title");
            snippet.setTitle(title);
            String description = params.get(Parameters.DESCRIPTION, "No Description");
            snippet.setDescription(description);
            String tags = params.get(Parameters.TAGS, "");
            snippet.setTags(CoreUtils.convertToUniqueList(tags));

            // Add the completed snippet object to the video resource.
            videoObjectDefiningMetadata.setSnippet(snippet);

            InputStream is = new ByteArrayInputStream(data);
            InputStreamContent mediaContent = new InputStreamContent("video/*", is);

            // Insert the video. The command sends three arguments. The first specifies which information the API request is setting and which
            // information the API response should return. The second argument  is the video resource that contains metadata about the new video.
            // The third argument is the actual video content.
            YouTube.Videos.Insert videoInsert = youtube.videos().insert(Arrays.asList("snippet", "statistics", "status"), videoObjectDefiningMetadata, mediaContent);

            // Set the upload type and add an event listener.
            MediaHttpUploader uploader = videoInsert.getMediaHttpUploader();

            // Indicate whether direct media upload is enabled. A value of "True" indicates that direct media upload is enabled and that
            // the entire media content will be uploaded in a single request. A value of "False," which is the default, indicates that the
            // request will use the resumable media upload protocol, which supports the ability to resume an upload operation after a
            // network interruption or other transmission failure, saving time and bandwidth in the event of network failures.
            uploader.setDirectUploadEnabled(false);

            MediaHttpUploaderProgressListener progressListener = uploader1 -> {
                switch (uploader1.getUploadState()) {
                    case INITIATION_STARTED:
                        log.info("Initiation Started");
                        break;
                    case INITIATION_COMPLETE:
                        log.info("Initiation Completed");
                        break;
                    case MEDIA_IN_PROGRESS:
                        log.info("Upload in progress");
                        break;
                    case MEDIA_COMPLETE:
                        log.info("Upload Completed!");
                        break;
                    case NOT_STARTED:
                        log.info("Upload Not Started!");
                        break;
                    default:
                        log.error("Unknown upload state");
                }
            };
            uploader.setProgressListener(progressListener);

            Video returnedVideo = videoInsert.execute();
            resultItem = createResultItem(null, returnedVideo, 0);

        } catch (Throwable e) {
            throw new InterWebException(e);
        }

        return resultItem;
    }

    @Override
    public Set<String> getTags(String username, int maxCount) throws IllegalArgumentException, IOException {
        if (maxCount < 1) {
            return null;
        }

        YouTube youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, request -> {
        }).setApplicationName("Interweb").build();

        ChannelListResponse channelListResponse = youtube.channels().list(Collections.singletonList("id"))
            .setKey(API_KEY).setForUsername(username).execute();

        ChannelListResponse detailedItem = youtube.channels().list(Arrays.asList("id", "brandingSettings"))
            .setKey(API_KEY).setId(Collections.singletonList(channelListResponse.getItems().get(0).getId())).execute();

        String keywords = detailedItem.getItems().get(0).getBrandingSettings().getChannel().getKeywords();

        String[] tagsArray = keywords.replaceAll("[\"'-+.^:,]", "").split(" ");
        return new HashSet<>(Arrays.asList(tagsArray));
    }

    /**
     * Build an authorization flow and store it as a static class attribute.
     *
     * @return GoogleAuthorizationCodeFlow instance.
     */
    public GoogleAuthorizationCodeFlow getFlow() {
        if (flow == null) {
            AuthCredentials clientCredentials = getAuthCredentials();
            flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientCredentials.getKey(), clientCredentials.getSecret(), SCOPES)
                .setAccessType("offline").setApprovalPrompt("force").build();
        }
        return flow;
    }

    /**
     * Convert AuthCredentials to Google Credential.
     */
    private Credential getYoutubeCredential(AuthCredentials authCredentials) {
        AuthCredentials clientCredentials = getAuthCredentials();
        Credential credential = new GoogleCredential.Builder().setTransport(HTTP_TRANSPORT).setJsonFactory(JSON_FACTORY)
            .setClientSecrets(clientCredentials.getKey(), clientCredentials.getSecret()).build();

        credential.setAccessToken(authCredentials.getKey());
        credential.setRefreshToken(authCredentials.getSecret());

        return credential;
    }

    @Override
    public InterWebPrincipal getPrincipal(Parameters parameters) throws InterWebException {
        parameters.add(Parameters.IWJ_USER_ID, parameters.get("state"));
        return super.getPrincipal(parameters);
    }

    @Override
    public String generateCallbackUrl(String baseApiUrl, Parameters parameters) {
        parameters.remove(Parameters.IWJ_USER_ID);
        return baseApiUrl + "callback?" + parameters.toQueryString();
    }

    protected static ZonedDateTime parseDate(String dateString) throws InterWebException {
        if (dateString == null) {
            return null;
        }

        try {
            return ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_ZONED_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new InterWebException("dateString: [" + dateString + "] " + e.getMessage());
        }
    }

    private static Thumbnail createThumbnail(com.google.api.services.youtube.model.Thumbnail vThumbnail) {
        return new Thumbnail(vThumbnail.getUrl(), vThumbnail.getWidth().intValue(), vThumbnail.getHeight().intValue());
    }
}
