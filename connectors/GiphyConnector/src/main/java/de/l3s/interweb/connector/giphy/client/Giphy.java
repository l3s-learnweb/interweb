package de.l3s.interweb.connector.giphy.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import jakarta.ws.rs.core.UriBuilder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.l3s.interweb.connector.giphy.client.entity.search.SearchFeed;
import de.l3s.interweb.connector.giphy.client.entity.search.SearchGiphy;
import de.l3s.interweb.connector.giphy.client.entity.search.SearchRandom;

/**
 * This class represents the main API.
 *
 * @author Mark Tripoli
 */
public class Giphy {
    private static final String SearchEndpoint = "https://api.giphy.com/v1/gifs/search";
    private static final String IDEndpoint = "https://api.giphy.com/v1/gifs/";
    private static final String TranslateEndpoint = "https://api.giphy.com/v1/gifs/translate";
    private static final String RandomEndpoint = "https://api.giphy.com/v1/gifs/random";
    private static final String TrendingEndpoint = "https://api.giphy.com/v1/gifs/trending";

    private static final String SearchStickerEndpoint = "https://api.giphy.com/v1/stickers/search";
    private static final String TranslateStickerEndpoint = "https://api.giphy.com/v1/stickers/translate";
    private static final String RandomEndpointSticker = "https://api.giphy.com/v1/stickers/random";
    private static final String TrendingStickerEndpoint = "https://api.giphy.com/v1/stickers/trending";

    private final String apiKey;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Constructs a new HttpRequestSender object.
     *
     * @param apiKey the GiphyAPI key
     */
    public Giphy(String apiKey) {
        this.apiKey = apiKey;

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private <T> T makeRequest(UriBuilder uriBuilder, Class<T> clazz) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(uriBuilder.build()).build();
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        return objectMapper.readValue(response.body(), clazz);
    }

    /**
     * Search all Giphy GIFs for a word or phrase and returns a SearchFeed
     * object.
     *
     * <p>
     * Be aware that not every response has all information available. In that
     * case the value will be returned as null.
     *
     * @param query  the query parameters. Multiple parameters are separated by a space
     * @param offset the offset
     * @return the SearchFeed object
     * @throws IOException if an error occurs during the search
     */
    public SearchFeed search(String query, int offset) throws IOException, InterruptedException {
        return search(query, 25, offset, null);
    }

    /**
     * Search all Giphy GIFs for a word or phrase and returns a SearchFeed
     * object.
     *
     * <p>
     * Be aware that not every response has all information available. In that
     * case the value will be returned as null.
     *
     * @param query  the query parameters. Multiple parameters are separated by a space
     * @param limit  the result limit. The maximum is 100.
     * @param offset the offset
     * @return the SearchFeed object
     * @throws IOException if an error occurs during the search
     */
    @SuppressWarnings("Duplicates")
    public SearchFeed search(String query, int limit, int offset, String lang) throws IOException, InterruptedException {
        SearchFeed feed;

        UriBuilder uriBuilder = UriBuilder.fromUri(SearchEndpoint);
        uriBuilder.queryParam("api_key", apiKey);
        uriBuilder.queryParam("q", query);
        if (limit > 100) {
            uriBuilder.queryParam("limit", "100");
        } else {
            uriBuilder.queryParam("limit", limit);
        }
        uriBuilder.queryParam("offset", offset);
        if (lang != null) {
            uriBuilder.queryParam("lang", lang);
        }
        uriBuilder.queryParam("rating", "G");

        feed = makeRequest(uriBuilder, SearchFeed.class);
        return feed;
    }

    /**
     * Returns a SerachGiphy object.
     *
     * <p>
     * Be aware that not every response has all information available. In that
     * case the value will be returned as null.
     *
     * @param id the Giphy id
     * @return the SerachGiphy object
     * @throws IOException if an error occurs during the search
     */
    public SearchGiphy searchByID(String id) throws IOException, InterruptedException {
        SearchGiphy giphy;

        UriBuilder uriBuilder = UriBuilder.fromUri(IDEndpoint).path(id);
        uriBuilder.queryParam("api_key", apiKey);

        giphy = makeRequest(uriBuilder, SearchGiphy.class);
        return giphy;
    }

    /**
     * The translate API draws on search, but also translates from one
     * vocabulary to another. In this case, words and phrases to GIFs.
     *
     * <p>
     * Be aware that not every response has all information available. In that
     * case the value will be returned as null.
     *
     * @param query the query parameters
     * @return the SerachGiphy object
     * @throws IOException if an error occurs during the search
     */
    @SuppressWarnings("Duplicates")
    public SearchGiphy translate(String query) throws IOException, InterruptedException {
        SearchGiphy giphy;

        UriBuilder uriBuilder = UriBuilder.fromUri(TranslateEndpoint);
        uriBuilder.queryParam("api_key", apiKey);
        uriBuilder.queryParam("s", query);

        giphy = makeRequest(uriBuilder, SearchGiphy.class);
        return giphy;
    }

    /**
     * Returns a random GIF, limited by tag.
     *
     * <p>
     * Be aware that not every response has all information available. In that
     * case the value will be returned as null.
     *
     * @param tag the GIF tag to limit randomness
     * @return the SerachGiphy object
     * @throws IOException if an error occurs during the search
     */
    @SuppressWarnings("Duplicates")
    public SearchRandom searchRandom(String tag) throws IOException, InterruptedException {
        SearchRandom random;

        UriBuilder uriBuilder = UriBuilder.fromUri(RandomEndpoint);
        uriBuilder.queryParam("api_key", apiKey);
        uriBuilder.queryParam("tag", tag);

        random = makeRequest(uriBuilder, SearchRandom.class);
        return random;
    }

    /**
     * Fetch GIFs currently trending online. Hand curated by the Giphy editorial
     * team. The data returned mirrors the GIFs showcased on the Giphy homepage.
     * Returns 25 results by default.
     *
     * <p>
     * Be aware that not every response has all information available. In that
     * case the value will be returned as null.
     *
     * @return the SearchFeed object
     * @throws IOException if an error occurs during the search
     */
    @SuppressWarnings("Duplicates")
    public SearchFeed trend() throws IOException, InterruptedException {
        SearchFeed feed;

        UriBuilder uriBuilder = UriBuilder.fromUri(TrendingEndpoint);
        uriBuilder.queryParam("api_key", apiKey);

        feed = makeRequest(uriBuilder, SearchFeed.class);
        return feed;
    }

    /**
     * Search all Giphy Sticker GIFs for a word or phrase and returns a
     * SearchFeed object.
     *
     * <p>
     * Be aware that not every response has all information available. In that
     * case the value will be returned as null.
     *
     * @param query  the query parameters. Multiple parameters are separated by a space
     * @param offset the offset
     * @return the SearchFeed object
     * @throws IOException if an error occurs during the search
     */
    public SearchFeed searchSticker(String query, int offset) throws IOException, InterruptedException {
        return searchSticker(query, 25, offset);
    }

    /**
     * Search all Giphy Sticker GIFs for a word or phrase and returns a
     * SearchFeed object.
     *
     * <p>
     * Be aware that not every response has all information available. In that
     * case the value will be returned as null.
     *
     * @param query  the query parameters. Multiple parameters are separated by a space
     * @param limit  the result limit. The maximum is 100.
     * @param offset the offset
     * @return the SearchFeed object
     * @throws IOException if an error occurs during the search
     */
    @SuppressWarnings("Duplicates")
    public SearchFeed searchSticker(String query, int limit, int offset) throws IOException, InterruptedException {
        SearchFeed feed;

        UriBuilder uriBuilder = UriBuilder.fromUri(SearchStickerEndpoint);
        uriBuilder.queryParam("api_key", apiKey);

        uriBuilder.queryParam("q", query);
        if (limit > 100) {
            uriBuilder.queryParam("limit", "100");
        } else {
            uriBuilder.queryParam("limit", limit);
        }
        uriBuilder.queryParam("offset", offset);

        feed = makeRequest(uriBuilder, SearchFeed.class);
        return feed;
    }

    /**
     * The translate API draws on search, but also translates from one
     * vocabulary to another. In this case, words and phrases to GIFs.
     *
     * <p>
     * Be aware that not every response has all information available. In that
     * case the value will be returned as null.
     *
     * @param query the query parameters
     * @return the SerachGiphy object
     * @throws IOException if an error occurs during the search
     */
    @SuppressWarnings("Duplicates")
    public SearchGiphy translateSticker(String query) throws IOException, InterruptedException {
        SearchGiphy giphy;

        UriBuilder uriBuilder = UriBuilder.fromUri(TranslateStickerEndpoint);
        uriBuilder.queryParam("api_key", apiKey);
        uriBuilder.queryParam("s", query);

        giphy = makeRequest(uriBuilder, SearchGiphy.class);
        return giphy;
    }

    /**
     * Fetch GIFs currently trending online. Hand curated by the Giphy editorial
     * team. The data returned mirrors the GIFs showcased on the Giphy homepage.
     * Returns 25 results by default.
     *
     * <p>
     * Be aware that not every response has all information available. In that
     * case the value will be returned as null.
     *
     * @return the SearchFeed object
     * @throws IOException if an error occurs during the search
     */
    @SuppressWarnings("Duplicates")
    public SearchFeed trendSticker() throws IOException, InterruptedException {
        SearchFeed feed;

        UriBuilder uriBuilder = UriBuilder.fromUri(TrendingStickerEndpoint);
        uriBuilder.queryParam("api_key", apiKey);

        feed = makeRequest(uriBuilder, SearchFeed.class);
        return feed;
    }

    /**
     * Returns a random GIF, limited by tag.
     *
     * <p>
     * Be aware that not every response has all information available. In that
     * case the value will be returned as null.
     *
     * @param tag the GIF tag to limit randomness
     * @return the SerachGiphy object
     * @throws IOException if an error occurs during the search
     */
    @SuppressWarnings("Duplicates")
    public SearchRandom searchRandomSticker(String tag) throws IOException, InterruptedException {
        SearchRandom random;

        UriBuilder uriBuilder = UriBuilder.fromUri(RandomEndpointSticker);
        uriBuilder.queryParam("api_key", apiKey);
        uriBuilder.queryParam("tag", tag);

        random = makeRequest(uriBuilder, SearchRandom.class);
        return random;
    }

}
