package de.l3s.interweb.connector.vimeo;

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

import jakarta.enterprise.context.Dependent;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.l3s.interweb.connector.vimeo.entity.*;
import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.search.*;
import de.l3s.interweb.core.util.DateUtils;

/**
 * Vimeo is a video hosting, sharing, and services platform headquartered in New York City.
 * TODO missing search implementations: extras, search_in, date, language.
 *
 * @see <a href="https://developer.vimeo.com/api/reference/videos#search_videos">Vimeo Search API</a>
 */
@Dependent
public class VimeoConnector implements SearchConnector {
    private static final Logger log = Logger.getLogger(VimeoConnector.class);

    @Override
    public String getName() {
        return "Vimeo";
    }

    @Override
    public String getBaseUrl() {
        return "https://vimeo.com/";
    }

    @Override
    public ContentType[] getSearchTypes() {
        return new ContentType[]{ContentType.video};
    }

    @Override
    public SearchConnectorResults search(SearchQuery query, AuthCredentials credentials) throws ConnectorException {
        SearchConnectorResults queryResult = new SearchConnectorResults();

        try {
            final String requestUrl = "https://api.vimeo.com/videos?page=" + query.getPage()
                    + "&per_page=" + query.getPerPage()
                    + "&sort=" + createSortOrder(query.getRanking())
                    + "&query=" + URLEncoder.encode(query.getQuery(), StandardCharsets.UTF_8);
            final String response = httpRequest(requestUrl, Map.ofEntries(
                    Map.entry("Accept", "application/vnd.vimeo.*+json; version=3.2"),
                    Map.entry("Authorization", "bearer " + credentials.getKey())
            ));

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            final VimeoResponse vimeoResponse = objectMapper.readValue(response, VimeoResponse.class);

            if (vimeoResponse.getError() != null
                    && vimeoResponse.getErrorCode() != 2286 // 2286 - no results for this page (when not first page requested)
                    && vimeoResponse.getErrorCode() != 2969) { // 2969 - requested a page of results that does not exist
                throw new ConnectorException(vimeoResponse.getErrorCode() + ": " + vimeoResponse.getDeveloperMessage());
            }

            if (vimeoResponse.getTotal() == 0) {
                return queryResult;
            }

            int count = (query.getPage() - 1) * query.getPerPage();
            queryResult.setTotalResults(vimeoResponse.getTotal());

            for (Datum video : vimeoResponse.getData()) {
                try {
                    SearchItem resultItem = new SearchItem(count++);
                    resultItem.setType(ContentType.video);
                    resultItem.setId(video.getLink().substring(1 + video.getLink().lastIndexOf('/')));
                    resultItem.setTitle(video.getName());
                    resultItem.setDescription(video.getDescription());
                    resultItem.setUrl(video.getLink());
                    resultItem.setDuration(video.getDuration().longValue());
                    resultItem.setDate(DateUtils.format(parseDate(video.getCreatedTime())));
                    resultItem.setWidth(video.getWidth());
                    resultItem.setHeight(video.getHeight());

                    if (video.getUser() != null) {
                        resultItem.setAuthor(video.getUser().getName());
                        resultItem.setAuthorUrl(video.getUser().getLink());
                    }

                    if (video.getTags() != null) {
                        for (Tag tag : video.getTags()) {
                            resultItem.getTags().add(tag.getName());
                        }
                    }
                    if (video.getMetadata() != null && video.getMetadata().getConnections().getComments() != null) {
                        resultItem.setCommentsCount(video.getMetadata().getConnections().getComments().getTotal());
                    }
                    if (video.getStats() != null && video.getStats().getPlays() != null) {
                        resultItem.setViewsCount(video.getStats().getPlays());
                    }

                    resultItem.setEmbeddedUrl("https://player.vimeo.com/video/" + resultItem.getId() + "?dnt=1");

                    Pictures pictures = video.getPictures();
                    if (pictures == null) {
                        queryResult.addTotalResults(-1);
                        continue; // makes no sense, we can't show them
                    }

                    List<Size> pictureSizes = pictures.getSizes();
                    for (Size size : pictureSizes) {
                        resultItem.setThumbnail(new Thumbnail(size.getLink(), size.getWidth(), size.getHeight()));
                    }

                    queryResult.addResultItem(resultItem);
                } catch (Throwable e) {
                    log.error("Can't parse entry: ", e);
                }
            }
        } catch (Throwable e) {
            log.errorv("Failed to retrieve results for query {0}", query, e);
        }
        return queryResult;
    }

    private static String createSortOrder(SearchRanking ranking) {
        return switch (ranking) {
            case date -> "date";
            case interestingness -> "plays";
            default -> "relevant";
        };
    }

    protected static ZonedDateTime parseDate(String dateString) throws ConnectorException {
        if (dateString == null) {
            return null;
        }

        try {
            return ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new ConnectorException("dateString: [" + dateString + "]. " + e.getMessage());
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
