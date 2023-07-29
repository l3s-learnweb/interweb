package de.l3s.interweb.connector.bing.client;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.l3s.interweb.connector.bing.client.entity.BingResponse;
import de.l3s.interweb.connector.bing.client.entity.query.BingQuery;
import de.l3s.interweb.connector.bing.client.entity.query.ResponseFilterParam;
import de.l3s.interweb.core.ConnectorException;

public class BingApiService implements BingRequestConstants {
    private static final Logger log = Logger.getLogger(BingApiService.class);

    private final String apiKey;

    public BingApiService(String apiKey) {
        super();
        this.apiKey = apiKey;
    }

    public BingResponse getResponseFromBingApi(BingQuery bingQuery) throws UnsupportedOperationException, ConnectorException, IOException, InterruptedException {
        // logger.debug("Receiving json from bing api...");

        // Construct the URL.
        String requestUrl = createUri(bingQuery);
        log.debugv("Bing request url: {0}", requestUrl);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        requestBuilder.uri(URI.create(requestUrl));
        requestBuilder.header(KEY_HEADER_NAME, apiKey);

        // Open the connection.
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());

        // Receive the JSON response body.
        String responseBody = response.body();

        // Construct the result object.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        BingResponse bingResponse = objectMapper.readValue(responseBody, BingResponse.class);
        bingResponse.setJsonResponse(responseBody);

        if (bingResponse.getError() != null) {
            throw new ConnectorException("Failed to retrieve bing results: " + bingResponse.getError());
        }

        return bingResponse;
    }

    private String createUri(BingQuery bingUrl) {
        boolean baseSearch = true;
        String apiPath = API_BASE_PATH;
        if (bingUrl.hasResponseFilter() && bingUrl.getResponseFilter().size() == 1) {
            if (bingUrl.getResponseFilter().get(0) == ResponseFilterParam.IMAGES) {
                apiPath = API_IMAGES_PATH;
                baseSearch = false;
            } else if (bingUrl.getResponseFilter().get(0) == ResponseFilterParam.VIDEOS) {
                apiPath = API_VIDEOS_PATH;
                baseSearch = false;
            }
        }

        String requestUrl = API_HOST + apiPath + "?" + PARAMETER_QUERY + "=" + URLEncoder.encode(bingUrl.getQuery(), StandardCharsets.UTF_8)
                + "&" + PARAMETER_COUNT + "=" + bingUrl.getCount()
                + "&" + PARAMETER_OFFSET + "=" + bingUrl.getOffset()
                + "&" + PARAMETER_TEXT_FORMAT + "=" + PARAMETER_VALUE_HTML
                + "&" + PARAMETER_TEXT_DECORATIONS + "=" + PARAMETER_VALUE_TRUE;

        if (bingUrl.hasMarket()) {
            requestUrl += "&" + PARAMETER_MKT + "=" + bingUrl.getMarket();
        }
        if (bingUrl.hasSafesearch()) {
            requestUrl += "&" + PARAMETER_SAFESEARCH + "=" + bingUrl.getSafesearch().getValue();
        }
        if (bingUrl.hasLanguage()) {
            requestUrl += "&" + PARAMETER_LANGUAGE + "=" + bingUrl.getLanguage();
        }
        if (baseSearch) {
            if (bingUrl.hasFreshness()) {
                requestUrl += "&" + PARAMETER_FRESHNESS + "=" + bingUrl.getFreshness();
            }
            if (bingUrl.hasResponseFilter()) {
                requestUrl += "&" + PARAMETER_RESPONSE_FILTER + "=" + bingUrl.getResponseFilter().stream().map(ResponseFilterParam::getValue).collect(Collectors.joining(","));
            }
        }
        return requestUrl;
    }

}
