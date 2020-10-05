package de.l3s.interwebj.connector.vimeo;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import de.l3s.interwebj.connector.vimeo.entity.Datum;
import de.l3s.interwebj.connector.vimeo.entity.Size;
import de.l3s.interwebj.connector.vimeo.entity.Tag;
import de.l3s.interwebj.connector.vimeo.entity.VimeoResponse;
import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.connector.ConnectorSearchResults;
import de.l3s.interwebj.core.connector.ServiceConnector;
import de.l3s.interwebj.core.query.ContentType;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.ResultItem;
import de.l3s.interwebj.core.query.SearchRanking;
import de.l3s.interwebj.core.query.Thumbnail;
import de.l3s.interwebj.core.util.CoreUtils;

/**
 * Vimeo is a video hosting, sharing, and services platform headquartered in New York City.
 * TODO missing search implementations: extras, search_in, date, language.
 *
 * @see <a href="https://developer.vimeo.com/api/reference/videos#search_videos">Vimeo Search API</a>
 */
public class VimeoConnector extends ServiceConnector {
    private static final Logger log = LogManager.getLogger(VimeoConnector.class);

    public VimeoConnector() {
        super("Vimeo", "https://vimeo.com/", ContentType.video);
    }

    public VimeoConnector(AuthCredentials consumerAuthCredentials) {
        this();
        setAuthCredentials(consumerAuthCredentials);
    }

    @Override
    public ServiceConnector clone() {
        return new VimeoConnector(getAuthCredentials());
    }

    @Override
    public ConnectorSearchResults get(Query query, AuthCredentials authCredentials) throws InterWebException {
        if (!isRegistered()) {
            throw new InterWebException("Service is not yet registered");
        }

        if (authCredentials == null) {
            authCredentials = getAuthCredentials();
        }

        ConnectorSearchResults queryResult = new ConnectorSearchResults(query, getName());

        if (!query.getContentTypes().contains(ContentType.video)) {
            return queryResult;
        }

        if (query.getQuery().startsWith("user::")) {
            return queryResult;
        }

        try {
            String requestUrl = "https://api.vimeo.com/videos?page=" + query.getPage()
                + "&per_page=" + query.getPerPage()
                + "&sort=" + createSortOrder(query.getRanking())
                + "&query=" + URLEncoder.encode(query.getQuery(), StandardCharsets.UTF_8);
            String response = httpRequest(requestUrl, Map.ofEntries(
                Map.entry("Accept", "application/vnd.vimeo.*+json; version=3.2"),
                Map.entry("Authorization", "bearer " + authCredentials.getSecret())
            ));

            VimeoResponse vimeoResponse = new Gson().fromJson(response, VimeoResponse.class);

            int count = (query.getPage() - 1) * query.getPerPage();
            long totalResultCount = vimeoResponse.getTotal();
            queryResult.setTotalResultCount(totalResultCount);

            for (Datum video : vimeoResponse.getData()) {
                try {
                    ResultItem resultItem = new ResultItem(getName(), count++);
                    resultItem.setType(ContentType.video);
                    resultItem.setId(video.getLink().substring(1 + video.getLink().lastIndexOf('/')));
                    resultItem.setTitle(video.getName());
                    resultItem.setDescription(video.getDescription());
                    resultItem.setUrl(video.getLink());
                    resultItem.setDuration(video.getDuration().longValue());
                    resultItem.setDate(CoreUtils.formatDate(parseDate(video.getCreatedTime())));

                    if (video.getTags() != null) {
                        for (Tag tag : video.getTags()) {
                            resultItem.getTags().add(tag.getName());
                        }
                    }
                    if (video.getMetadata() != null && video.getMetadata().getConnections().getComments() != null) {
                        resultItem.setCommentCount(video.getMetadata().getConnections().getComments().getTotal());
                    }
                    if (video.getStats() != null && video.getStats().getPlays() != null) {
                        resultItem.setViewCount(video.getStats().getPlays());
                    }

                    resultItem.setEmbeddedCode(createEmbeddedCode(resultItem.getId()));

                    List<Size> pictures = video.getPictures().getSizes();
                    for (Size picture : pictures) {
                        resultItem.setThumbnail(new Thumbnail(picture.getLink(), picture.getWidth(), picture.getHeight()));
                    }

                    queryResult.addResultItem(resultItem);
                } catch (Throwable e) {
                    log.error("Can't parse entry: ", e);
                }
            }
        } catch (Throwable e) {
            log.catching(e);
        }
        return queryResult;
    }

    private static String createSortOrder(SearchRanking ranking) {
        switch (ranking) {
            case date:
                return "date";
            case interestingness:
                return "plays";
            case relevance:
            default:
                return "relevant";
        }
    }

    @Override
    public boolean isUserRegistrationRequired() {
        return true;
    }

    protected static ZonedDateTime parseDate(String dateString) throws InterWebException {
        if (dateString == null) {
            return null;
        }

        try {
            return ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new InterWebException("dateString: [" + dateString + "]. " + e.getMessage());
        }
    }

    private static String httpRequest(String url, Map<String, String> headers) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        requestBuilder.uri(URI.create(url));

        if (!headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.header(entry.getKey(), entry.getValue());
            }
        }

        HttpResponse<String> response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private static String createEmbeddedCode(String id) {
        String iframeUrl = "https://player.vimeo.com/video/" + id + "?dnt=1";
        return "<iframe src=\"" + iframeUrl + "\" allowfullscreen referrerpolicy=\"origin\">Your browser has blocked this iframe</iframe>";
    }
}
