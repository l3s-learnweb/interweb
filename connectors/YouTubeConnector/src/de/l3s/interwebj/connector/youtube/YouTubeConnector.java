package de.l3s.interwebj.connector.youtube;

import static de.l3s.interwebj.core.util.Assertions.notNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
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
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Joiner;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoContentDetails;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatistics;
import com.google.api.services.youtube.model.VideoStatus;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.Parameters;
import de.l3s.interwebj.core.core.AbstractServiceConnector;
import de.l3s.interwebj.core.core.InterWebPrincipal;
import de.l3s.interwebj.core.core.ServiceConnector;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.ConnectorResults;
import de.l3s.interwebj.core.query.ResultItem;
import de.l3s.interwebj.core.query.Thumbnail;
import de.l3s.interwebj.core.util.CoreUtils;

public class YouTubeConnector extends AbstractServiceConnector implements Cloneable {
    /**
     * Define a global instance of the HTTP transport.
     */
    public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    /**
     * Define a global instance of the JSON factory.
     */
    public static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static final Logger log = LogManager.getLogger(YouTubeConnector.class);
    private static final String CLIENT_ID = "***REMOVED***";
    private static final String CLIENT_SECRET = "***REMOVED***";
    private static final String API_KEY = "***REMOVED***";
    /**
     * Define a global instance of the scopes.
     */
    private static final List<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/youtube.upload", "profile", "https://www.googleapis.com/auth/youtube");

    private static GoogleAuthorizationCodeFlow flow = null;

    public YouTubeConnector() {
        super("YouTube", "http://www.youtube.com", new TreeSet<>(Arrays.asList("video")));
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

            log.info("requesting url: " + url);
        } catch (Exception e) {
            log.error(e);
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
        AuthCredentials authCredentials = null;
        if (!isRegistered()) {
            throw new InterWebException("Service is not yet registered");
        }

        String authorizationCode = params.get("code");
        log.info("authorization_code: " + authorizationCode);

        Credential cred = null;

        try {
            GoogleTokenResponse response = getFlow().newTokenRequest(authorizationCode).setRedirectUri(params.get(Parameters.CALLBACK + "Auth")).execute();
            cred = flow.createAndStoreCredential(response, null);

            authCredentials = new AuthCredentials(cred.getAccessToken(), cred.getRefreshToken());
        } catch (IOException e) {
            log.error(e);
            throw new InterWebException(e);
        }

        return authCredentials;
    }

    @Override
    public ConnectorResults get(Query query, AuthCredentials authCredentials) throws InterWebException {
        notNull(query, "query");
        if (!isRegistered()) {
            throw new InterWebException("Service is not yet registered");
        }

        ConnectorResults queryResult = new ConnectorResults(query, getName());
        TokenStorage tokens = TokenStorage.getInstance();

        if (!query.getContentTypes().contains(Query.CT_VIDEO)) {
            return queryResult;
        }

        try {
            // This object is used to make YouTube Data API requests. The last argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override the interface and provide a no-op function.
            YouTube youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, request -> {
            }).setApplicationName("Interweb").build();

            // Define the API request for retrieving search results.
            YouTube.Search.List search = youtube.search().list("id");

            // Set your developer key from the {{ Google Cloud Console }} for non-authenticated requests.
            search.setKey(API_KEY);

            if (query.getQuery().startsWith("user::")) {
                String[] splitQuery = query.getQuery().split(" ", 2);
                ChannelListResponse channelListResponse = youtube.channels().list("id")
                    .setKey(API_KEY).setForUsername(splitQuery[ 0 ].substring(6)).setFields("items(id)").execute();

                search.setChannelId(channelListResponse.getItems().get(0).getId());

                if (splitQuery.length > 1 && splitQuery[ 1 ] != null) {
                    search.setQ(splitQuery[ 1 ]);
                }
            } else {
                search.setQ(query.getQuery());
            }

            search.setMaxResults((long) query.getResultCount());

            if (query.getParam("date_from") != null) {
                try {
                    DateTime dateFrom = new DateTime(CoreUtils.parseDate(query.getParam("date_from")));
                    search.setPublishedAfter(dateFrom);
                } catch (Exception e) {
                    log.error(e);
                }
            }

            if (query.getParam("date_till") != null) {
                try {
                    DateTime dateTill = new DateTime(CoreUtils.parseDate(query.getParam("date_till")));
                    search.setPublishedBefore(dateTill);
                } catch (Exception e) {
                    log.error(e);
                }
            }

            /*
             * The pageToken parameter identifies a specific page in the result set that
             * should be returned. In an API response, the nextPageToken and prevPageToken
             * properties identify other pages that could be retrieved.
             *
             * Old solution:
             * ytq.setStartIndex(Math.min(50, query.getResultCount()) * (query.getPage()-1)+1);
             */
            if (query.getPage() > 1) {
                if (tokens.get(query.getPage()) != null) {
                    search.setPageToken(tokens.get(query.getPage()));
                } else if (tokens.get(-1) != null && tokens.get(-1).equals("no-more-pages")) {
                    log.warn("No more results for page " + query.getPage());
                    return queryResult;
                } else {
                    throw new InterWebException("YouTube does not support search by specific page numbers without requesting prevent page.");
                }
            }

            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");
            search.setSafeSearch("none"); // moderate | none | strict

            switch (query.getSortOrder()) {
                case RELEVANCE:
                    search.setOrder("relevance");
                    break;
                //ytq.addCustomParameter(new CustomParameter("orderby", "relevance_lang_"+ query.getLanguage())); break;
                case DATE:
                    search.setOrder("date");
                    break;
                case INTERESTINGNESS:
                    search.setOrder("viewCount");
                    break;
                default:
                    // The default value is relevance (by Google)
                    //search.setOrder("relevance");
                    break;
            }

            // To increase efficiency, only retrieve the fields that the application uses.
            search.setFields("nextPageToken,pageInfo/totalResults,items(id/videoId)");
            search.setMaxResults((long) query.getResultCount());

            // Call the API and print results.
            log.info("Request url: " + search.buildHttpRequestUrl());
            SearchListResponse searchResponse = search.execute();

            // Limiting YouTube data API, see more: http://stackoverflow.com/questions/23255957
            long totalResults = searchResponse.getPageInfo().getTotalResults();
            queryResult.setTotalResultCount(totalResults > 500 ? 500 : totalResults);

            if (searchResponse.getNextPageToken() != null) {
                log.info("Next page " + (query.getPage() + 1) + " its token " + searchResponse.getNextPageToken());
                tokens.put(query.getPage() + 1, searchResponse.getNextPageToken());
            } else {
                log.info("No more results");
                tokens.put(-1, "no-more-pages");
            }

            List<SearchResult> searchResultList = searchResponse.getItems();
            List<String> videoIds = new ArrayList<String>();

            if (searchResultList != null) {
                // Merge video IDs
                for (SearchResult searchResult : searchResultList) {
                    videoIds.add(searchResult.getId().getVideoId());
                }
                Joiner stringJoiner = Joiner.on(',');
                String videoId = stringJoiner.join(videoIds);

                // Call the YouTube Data API's youtube.videos.list method to retrieve the resources that represent the specified videos.
                YouTube.Videos.List listVideosRequest = youtube.videos().list("snippet, statistics, contentDetails").setId(videoId);
                listVideosRequest.setKey(API_KEY);
                VideoListResponse listResponse = listVideosRequest.execute();

                List<Video> videoList = listResponse.getItems();

                if (videoList != null) {

                    Iterator<Video> iteratorVideoResults = videoList.iterator();
                    int rank = Math.min(50, query.getResultCount()) * (query.getPage() - 1) + 1;
                    int resultCount = (int) queryResult.getTotalResultCount();

                    while (iteratorVideoResults.hasNext()) {
                        Video singleVideo = iteratorVideoResults.next();
                        ResultItem resultItem = createResultItem(singleVideo, rank++, resultCount);
                        if (resultItem != null) {
                            queryResult.addResultItem(resultItem);
                        }
                    }
                }
            }

        } catch (Throwable e) {
            log.error(e);
            throw new InterWebException(e);
        }

        return queryResult;
    }

    private ResultItem createResultItem(Video singleVideo, int rank, int totalResultCount) {
        // Confirm that the result represents a video. Otherwise, the item will not contain a video ID.
        if (!singleVideo.getKind().equals("youtube#video")) {
            return null;
        }

        ResultItem resultItem = new ResultItem(getName());
        resultItem.setType(Query.CT_VIDEO);
        resultItem.setId(singleVideo.getId());
        resultItem.setUrl("https://www.youtube.com/watch?v=" + singleVideo.getId());
        resultItem.setRank(rank);
        resultItem.setTotalResultCount(totalResultCount);

        VideoSnippet vSnippet = singleVideo.getSnippet();
        if (vSnippet != null) {
            if (vSnippet.getTitle() != null) {
                resultItem.setTitle(vSnippet.getTitle());
            }
            if (vSnippet.getDescription() != null) {
                resultItem.setDescription(vSnippet.getDescription());
            }
            if (vSnippet.getPublishedAt() != null) {
                resultItem.setDate(CoreUtils.formatDate(vSnippet.getPublishedAt().getValue()));
            }
            if (vSnippet.getTags() != null) {
                resultItem.setTags(StringUtils.join(vSnippet.getTags(), ','));
            }
        }

        VideoStatistics vStatistics = singleVideo.getStatistics();
        if (vStatistics != null) {
            if (vStatistics.getViewCount() != null) {
                resultItem.setViewCount(vStatistics.getViewCount().intValue());
            }
            if (vStatistics.getCommentCount() != null) {
                resultItem.setViewCount(vStatistics.getCommentCount().intValue());
            }
        }

        VideoContentDetails vContentDetails = singleVideo.getContentDetails();
        if (vContentDetails != null) {
            if (vContentDetails.getDuration() != null) {
                resultItem.setDuration((int) getSecondFromDuration(vContentDetails.getDuration()));
            }
        }

        // load thumbnails
        Set<Thumbnail> thumbnails = new TreeSet<Thumbnail>();
        Thumbnail thumbnail;
        com.google.api.services.youtube.model.Thumbnail googleThumbnail;

        googleThumbnail = singleVideo.getSnippet().getThumbnails().getDefault();
        thumbnail = new Thumbnail(googleThumbnail.getUrl(), googleThumbnail.getWidth().intValue(), googleThumbnail.getHeight().intValue());
        resultItem.setEmbeddedSize1(CoreUtils.createImageCode(thumbnail, 100, 100));
        thumbnails.add(thumbnail);

        if (singleVideo.getSnippet().getThumbnails().getMedium() != null) {
            googleThumbnail = singleVideo.getSnippet().getThumbnails().getMedium();
        } else {
            googleThumbnail = singleVideo.getSnippet().getThumbnails().getHigh();
        }

        thumbnail = new Thumbnail(googleThumbnail.getUrl(), googleThumbnail.getWidth().intValue(), googleThumbnail.getHeight().intValue());
        resultItem.setEmbeddedSize2(CoreUtils.createImageCode(thumbnail, 240, 240));
        resultItem.setImageUrl(thumbnail.getUrl());
        thumbnails.add(thumbnail);

        resultItem.setThumbnails(thumbnails);

        //create embedded flash video player
        String embeddedCode = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/" + singleVideo.getId() + "\" frameborder=\"0\" allowfullscreen></iframe>";
        resultItem.setEmbeddedSize3(embeddedCode);

        return resultItem;
    }

    private long getSecondFromDuration(String period) {
        String time = period.substring(2);
        long duration = 0L;
        Object[][] indexs = new Object[][]{{"H", 3600}, {"M", 60}, {"S", 1}};
        for (int i = 0; i < indexs.length; i++) {
            int index = time.indexOf((String) indexs[ i ][ 0 ]);
            if (index != -1) {
                String value = time.substring(0, index);
                duration += Integer.parseInt(value) * (Integer) indexs[ i ][ 1 ];
                time = time.substring(value.length() + 1);
            }
        }
        return duration;
    }

    @Override
    public String getEmbedded(AuthCredentials authCredentials, String url, int maxWidth, int maxHeight) throws InterWebException {
        Pattern pattern = Pattern.compile(".*(?:youtu.be\\/|v\\/|u\\/\\w\\/|embed\\/|watch\\?v=)([^#\\&\\?]*).*");
        Matcher matcher = pattern.matcher(url);

        if (matcher.matches()) {
            String id = matcher.group(1);
            return "<iframe width=\"" + maxWidth + "\" height=\"" + maxHeight + "\" src=\"https://www.youtube.com/embed/" + id + "\" frameborder=\"0\" allowfullscreen></iframe>";
        }

        throw new InterWebException("URL: [" + url + "] doesn't belong to connector [" + getName() + "]");
    }

    @Override
    public String getUserId(AuthCredentials authCredentials) throws InterWebException {
        try {
            YouTube youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, getYoutubeCredential(authCredentials)).setApplicationName("Interweb").build();

            ChannelListResponse channelListResponse = youtube.channels().list("id,contentDetails")
                .setMine(true).setFields("items(contentDetails/relatedPlaylists/uploads,id)").execute();

            return channelListResponse.getItems().get(0).getId();
        } catch (IOException e) {
            log.error(e);
            log.error(e);
            throw new InterWebException(e);
        }
    }

    @Override
    public boolean isConnectorRegistrationDataRequired() {
        return true;
    }

    @Override
    public boolean isUserRegistrationDataRequired() {
        return false;
    }

    @Override
    public boolean isUserRegistrationRequired() {
        return true;
    }

    @Override
    public ResultItem put(byte[] data, String contentType, Parameters params, AuthCredentials authCredentials) throws InterWebException {
        notNull(data, "data");
        notNull(contentType, "contentType");
        notNull(params, "params");
        if (!isRegistered()) {
            throw new InterWebException("Service is not yet registered");
        }

        if (authCredentials == null) {
            throw new InterWebException("Upload is forbidden for non-authorized users");
        }

        ResultItem resultItem = null;

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

            /*
            TODO set categories
            String category = params.get("category", "Film");
            mg.addCategory(new MediaCategory(YouTubeNamespace.CATEGORY_SCHEME, category));
            */

            // Add the completed snippet object to the video resource.
            videoObjectDefiningMetadata.setSnippet(snippet);

            InputStream is = new ByteArrayInputStream(data);
            InputStreamContent mediaContent = new InputStreamContent("video/*", is);

            // Insert the video. The command sends three arguments. The first
            // specifies which information the API request is setting and which
            // information the API response should return. The second argument
            // is the video resource that contains metadata about the new video.
            // The third argument is the actual video content.
            YouTube.Videos.Insert videoInsert = youtube.videos().insert("snippet,statistics,status", videoObjectDefiningMetadata, mediaContent);

            // Set the upload type and add an event listener.
            MediaHttpUploader uploader = videoInsert.getMediaHttpUploader();

            // Indicate whether direct media upload is enabled. A value of
            // "True" indicates that direct media upload is enabled and that
            // the entire media content will be uploaded in a single request.
            // A value of "False," which is the default, indicates that the
            // request will use the resumable media upload protocol, which
            // supports the ability to resume an upload operation after a
            // network interruption or other transmission failure, saving
            // time and bandwidth in the event of network failures.
            uploader.setDirectUploadEnabled(false);

            MediaHttpUploaderProgressListener progressListener = new MediaHttpUploaderProgressListener() {
                public void progressChanged(MediaHttpUploader uploader) throws IOException {
                    switch (uploader.getUploadState()) {
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
                }
            };
            uploader.setProgressListener(progressListener);

            Video returnedVideo = videoInsert.execute();
            resultItem = createResultItem(returnedVideo, 0, 0);

        } catch (Throwable e) {
            log.error(e);
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

        ChannelListResponse channelListResponse = youtube.channels().list("id")
            .setKey(API_KEY).setForUsername(username).setFields("items(id)").execute();

        ChannelListResponse detailedItem = youtube.channels().list("id, brandingSettings")
            .setKey(API_KEY).setId(channelListResponse.getItems().get(0).getId()).setFields("items(id, brandingSettings)").execute();

        HashSet<String> tags = new HashSet<String>();
        String keywords = detailedItem.getItems().get(0).getBrandingSettings().getChannel().getKeywords();

        for (String keyword : keywords.replaceAll("[\"'-+.^:,]", "").split(" ")) {
            tags.add(keyword);
        }

        return tags;
    }

    /**
     * Build an authorization flow and store it as a static class attribute.
     *
     * @return GoogleAuthorizationCodeFlow instance.
     */
    public GoogleAuthorizationCodeFlow getFlow() throws IOException {
        if (flow == null) {
            flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, SCOPES)
                .setAccessType("offline").setApprovalPrompt("force").build();
        }
        return flow;
    }

    /**
     * Convert AuthCredentials to Google Credential.
     */
    private Credential getYoutubeCredential(AuthCredentials authCredentials) {
        Credential credential = new GoogleCredential.Builder().setTransport(HTTP_TRANSPORT).setJsonFactory(JSON_FACTORY)
            .setClientSecrets(CLIENT_ID, CLIENT_SECRET).build();

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
}
