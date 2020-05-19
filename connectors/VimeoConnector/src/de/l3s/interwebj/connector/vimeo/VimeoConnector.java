package de.l3s.interwebj.connector.vimeo;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.core.AbstractServiceConnector;
import de.l3s.interwebj.core.core.ServiceConnector;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.ConnectorResults;
import de.l3s.interwebj.core.query.ResultItem;
import de.l3s.interwebj.core.query.Thumbnail;
import de.l3s.interwebj.core.util.CoreUtils;

public class VimeoConnector extends AbstractServiceConnector implements Cloneable {
    private static final Logger log = LogManager.getLogger(VimeoConnector.class);

    private static final String TOKEN = "***REMOVED***";

    public VimeoConnector() {
        super("Vimeo", "http://www.vimeo.com", new TreeSet<>(Arrays.asList("video")));
    }

    public VimeoConnector(AuthCredentials consumerAuthCredentials) {
        this();
        setAuthCredentials(consumerAuthCredentials);
    }

    private static Date parseDate(String dateString) throws InterWebException {
        if (dateString == null) {
            return null;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            log.error(e);
            throw new InterWebException("dateString: [" + dateString + "] " + e.getMessage());
        }
    }

    /*
    private static String createSortOrder(SortOrder sortOrder)
    {
        switch(sortOrder)
        {
        case RELEVANCE:
            return "relevant";
        case DATE:
            return "date";
        case INTERESTINGNESS:
            return "plays";
        default:
            return "relevant";
        }
    }*/

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

    @Override
    public ServiceConnector clone() {
        return new VimeoConnector(getAuthCredentials());
    }

    @Override
    public ConnectorResults get(Query query, AuthCredentials authCredentials) throws InterWebException {
        if (!isRegistered()) {
            throw new InterWebException("Service is not yet registered");
        }
        ConnectorResults queryResult = new ConnectorResults(query, getName());

        if (!query.getContentTypes().contains(Query.CT_VIDEO)) {
            return queryResult;
        }

        if (query.getQuery().startsWith("user::")) {
            return queryResult;
        }

        try {
            String requestUrl = "https://api.vimeo.com/videos?page=" + query.getPage() + "&per_page=" + query.getResultCount() + "&query=" + URLEncoder.encode(query.getQuery(), StandardCharsets.UTF_8);
            String response = httpRequest(requestUrl, Map.ofEntries(
                    Map.entry("Accept", "application/vnd.vimeo.*+json; version=3.2"),
                    Map.entry("Authorization", "bearer " + TOKEN)
            ));

            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();

            int count = (query.getPage() - 1) * query.getResultCount();
            long totalResultCount = jsonObject.get("total").getAsLong();
            queryResult.setTotalResultCount(totalResultCount);

            JsonArray data = jsonObject.getAsJsonArray("data");
            for (JsonElement element : data) {
                try {
                    JsonObject video = element.getAsJsonObject();
                    String name = video.get("name").getAsString();
                    JsonElement descriptionEl = video.get("description");
                    String description = descriptionEl == null ? "" : descriptionEl.getAsString();
                    int duration = video.get("duration").getAsInt();
                    String link = video.get("link").getAsString();

                    ResultItem resultItem = new ResultItem(getName());
                    resultItem.setType(Query.CT_VIDEO);
                    resultItem.setId(link.substring(1 + link.lastIndexOf('/')));
                    resultItem.setTitle(name);
                    resultItem.setDescription(description);
                    resultItem.setUrl(link);
                    resultItem.setDate(CoreUtils.formatDate(parseDate(video.get("created_time").getAsString())));
                    resultItem.setRank(count++);
                    resultItem.setTotalResultCount(totalResultCount);
                    //resultItem.setCommentCount(video.getNumberOfComments());
                    // resultItem.setViewCount(video.getJSONObject("stats").getInt("plays")); plays can be null TODO need to handle it
                    resultItem.setDuration(duration);

                    JsonArray pictures = video.getAsJsonObject("pictures").getAsJsonArray("sizes");
                    Set<Thumbnail> thumbnails = new LinkedHashSet<Thumbnail>();
                    for (JsonElement elementPic : pictures) {
                        JsonObject picture = elementPic.getAsJsonObject();
                        int width = picture.get("width").getAsInt();
                        int height = picture.get("height").getAsInt();
                        String pictureURL = picture.get("link").getAsString();

                        thumbnails.add(new Thumbnail(pictureURL, width, height));

                        resultItem.setImageUrl(pictureURL); // thumbnails are orderd by size. so the last assigned image is the largest

                        if (width <= 100) {
                            resultItem.setEmbeddedSize1(CoreUtils.createImageCode(pictureURL, width, height, 100, 100));
                        }

                    }
                    resultItem.setThumbnails(thumbnails);

                    queryResult.addResultItem(resultItem);
                } catch (Throwable e) {
                    log.warn("Can't parse entry: " + e.getMessage());
                }
            }
        } catch (Throwable e) {
            log.warn(e.getMessage());
        }
        return queryResult;
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
}
