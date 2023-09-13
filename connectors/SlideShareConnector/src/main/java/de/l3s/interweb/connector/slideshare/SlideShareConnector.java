package de.l3s.interweb.connector.slideshare;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.enterprise.context.Dependent;
import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import de.l3s.interweb.connector.slideshare.entity.Slideshow;
import de.l3s.interweb.connector.slideshare.entity.Slideshows;
import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.search.*;
import de.l3s.interweb.core.util.Assertions;
import de.l3s.interweb.core.util.DateUtils;
import de.l3s.interweb.core.util.StringUtils;

/**
 * LinkedIn SlideShare is an American hosting service for professional content including presentations, infographics, documents, and videos.
 * TODO missing search implementations: extras, date.
 *
 * @see <a href="https://www.slideshare.net/developers/documentation#search_slideshows">SlideShare Search API</a>
 */
@Dependent
public class SlideShareConnector implements SearchConnector {
    private static final Logger log = Logger.getLogger(SlideShareConnector.class);

    @Override
    public String getName() {
        return "SlideShare";
    }

    @Override
    public String getBaseUrl() {
        return "https://www.slideshare.net/";
    }

    @Override
    public ContentType[] getSearchTypes() {
        return new ContentType[]{ContentType.text, ContentType.presentation, ContentType.video};
    }

    @Override
    public SearchConnectorResults search(SearchQuery query, AuthCredentials credentials) throws ConnectorException {
        SearchConnectorResults queryResult = new SearchConnectorResults();

        UriBuilder uriBuilder = UriBuilder.fromUri("https://www.slideshare.net/api/2/search_slideshows")
                .queryParam("q", query.getQuery())
                .queryParam("format", "json")
                .queryParam("page", Integer.toString(query.getPage()))
                .queryParam("items_per_page", Integer.toString(query.getPerPage()))
                // .queryParam("detailed", "1")
                .queryParam("sort", convertRanking(query.getRanking()));

        if (query.getLanguage() != null) {
            uriBuilder.queryParam("lang", query.getLanguage());
        }
        if (query.getSearchScope() == SearchScope.tags) {
            uriBuilder.queryParam("what", "tag");
        }

        String fileType = convertContentType(query.getContentTypes());
        if (fileType == null) {
            return queryResult;
        }
        uriBuilder.queryParam("file_type", fileType);

        try {
            HttpResponse<String> response = postQuery(uriBuilder, credentials);

            XmlMapper objectMapper = new XmlMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            Slideshows sr = objectMapper.readValue(response.body(), Slideshows.class);

            queryResult.setTotalResults(sr.getMeta().getTotalResults());
            int count = sr.getMeta().getResultOffset() - 1;
            List<Slideshow> searchResults = sr.getSearchResults();
            if (searchResults == null) {
                return queryResult;
            }

            for (Slideshow sre : searchResults) {
                SearchItem resultItem = new SearchItem(count++);
                resultItem.setType(createType(sre.getSlideshowType()));
                resultItem.setId(Integer.toString(sre.getId()));
                resultItem.setTitle(sre.getTitle());
                resultItem.setDescription(sre.getDescription());
                resultItem.setUrl(sre.getUrl());
                resultItem.setDate(DateUtils.format(parseDate(sre.getUpdated())));
                resultItem.setAuthor(sre.getUserName());
                resultItem.setAuthorUrl("https://www.slideshare.net/" + sre.getUserName());

                // slideshare api return always the same wrong thumbnail size
                // String[] size = sre.getThumbnailSize().substring(1, sre.getThumbnailSize().length()-1).split(",");

                int width = 170;
                int height = fileType.equals("documents") ? 220 : 128;

                resultItem.setThumbnailSmall(new Thumbnail(sre.getThumbnailURL(), width, height));
                resultItem.setWidth(width);
                resultItem.setHeight(height);
                resultItem.setEmbeddedUrl(StringUtils.parseSourceUrl(sre.getEmbed()));

                queryResult.addResultItem(resultItem);
            }
        } catch (Exception e) {
            throw new ConnectorException("Failed to retrieve results", e);
        }

        return queryResult;
    }

    private String convertContentType(Set<ContentType> contentTypes) {
        if (contentTypes.size() == 1) {
            if (contentTypes.contains(ContentType.presentation)) {
                return "presentations";
            } else if (contentTypes.contains(ContentType.text)) {
                return "documents";
            } else if (contentTypes.contains(ContentType.video)) {
                return "videos";
            }
        }

        return "all";
    }

    private static String convertRanking(SearchRanking ranking) {
        return switch (ranking) {
            case date -> "latest";
            case interestingness -> "mostviewed";
            default -> "relevance";
        };
    }

    private ContentType createType(int slideshowType) {
        return switch (slideshowType) {
            case 0 -> ContentType.presentation;
            case 1 -> ContentType.text;
            case 2 -> ContentType.image;
            case 3 -> ContentType.video;
            default -> {
                log.errorv("Unknown type {0}", slideshowType);
                yield null;
            }
        };
    }

    protected static ZonedDateTime parseDate(String dateString) throws ConnectorException {
        if (dateString == null) {
            return null;
        }

        try {
            return ZonedDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z").withZone(ZoneId.systemDefault()));
        } catch (DateTimeParseException e) {
            throw new ConnectorException("dateString: [" + dateString + "] " + e.getMessage());
        }
    }

    private HttpResponse<String> postQuery(UriBuilder uriBuilder, AuthCredentials authCredentials) throws IOException, InterruptedException {
        long timestamp = System.currentTimeMillis() / 1000;
        String toHash = authCredentials.getSecret() + timestamp;

        Map<String, String> params = new HashMap<>();
        params.put("api_key", authCredentials.getKey());
        params.put("ts", Long.toString(timestamp));
        params.put("hash", getHash(toHash).toLowerCase());

        String form = params.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        requestBuilder.uri(uriBuilder.build()).POST(HttpRequest.BodyPublishers.ofString(form));
        requestBuilder.header("Content-Type", "application/x-www-form-urlencoded");
        requestBuilder.header("Accept", "application/json");
        return client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
    }

    public static String getHash(String input) {
        String sha1 = null;
        try {
            MessageDigest msdDigest = MessageDigest.getInstance("SHA-1");
            msdDigest.update(input.getBytes(StandardCharsets.UTF_8), 0, input.length());
            sha1 = new BigInteger(1, msdDigest.digest()).toString(16);
            //            sha1 = DatatypeConverter.printHexBinary(msdDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to hash string", e);
        }
        return sha1;
    }
}
