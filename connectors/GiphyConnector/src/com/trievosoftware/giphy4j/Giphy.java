/*
 * The MIT License
 *
 * Copyright (c) 2019 Trievo, LLC. https://trievosoftware.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 *
 */

package com.trievosoftware.giphy4j;

import java.io.IOException;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.trievosoftware.giphy4j.dao.HttpRequestSender;
import com.trievosoftware.giphy4j.dao.RequestSender;
import com.trievosoftware.giphy4j.entity.search.SearchFeed;
import com.trievosoftware.giphy4j.entity.search.SearchGiphy;
import com.trievosoftware.giphy4j.entity.search.SearchRandom;
import com.trievosoftware.giphy4j.exception.GiphyException;
import com.trievosoftware.giphy4j.http.Request;
import com.trievosoftware.giphy4j.http.Response;
import com.trievosoftware.giphy4j.util.UrlUtil;

/**
 * This class represents the main API.
 *
 * @author Mark Tripoli
 */
public class Giphy {
    private static final Logger log = LogManager.getLogger(Giphy.class);

    private static final String SearchEndpoint = "http://api.giphy.com/v1/gifs/search";
    private static final String IDEndpoint = "http://api.giphy.com/v1/gifs/";
    private static final String TranslateEndpoint = "http://api.giphy.com/v1/gifs/translate";
    private static final String RandomEndpoint = "http://api.giphy.com/v1/gifs/random";
    private static final String TrendingEndpoint = "http://api.giphy.com/v1/gifs/trending";

    private static final String SearchStickerEndpoint = "http://api.giphy.com/v1/stickers/search";
    private static final String TranslateStickerEndpoint = "http://api.giphy.com/v1/stickers/translate";
    private static final String RandomEndpointSticker = "http://api.giphy.com/v1/stickers/random";
    private static final String TrendingStickerEndpoint = "http://api.giphy.com/v1/stickers/trending";

    private final String apiKey;
    private final RequestSender sender;
    private final Gson gson;

    /**
     * Constructs a new Giphy object.
     *
     * @param apiKey the GiphyAPI key
     */
    public Giphy(String apiKey) {
        this.apiKey = apiKey;

        sender = new HttpRequestSender();
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Constructs a new HttpRequestSender object.
     *
     * <p>
     * It's recommended to use the simple constructor without a sender argument.
     * This one is just for testing purposes or in case you want to use the
     * sender with different settings.
     *
     * @param apiKey the GiphyAPI key
     * @param sender the sender object
     */
    public Giphy(String apiKey, RequestSender sender) {
        this.apiKey = apiKey;
        this.sender = sender;

        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Search all Giphy GIFs for a word or phrase and returns a SearchFeed
     * object.
     *
     * <p>
     * Be aware that not every response has all information available. In that
     * case the value will be returned as null.
     *
     * @param query the query parameters. Multiple parameters are separated by a space
     * @param offset the offset
     * @return the SearchFeed object
     * @throws GiphyException if an error occurs during the search
     */
    public SearchFeed search(String query, int offset) throws GiphyException {
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
     * @param query the query parameters. Multiple parameters are separated by a space
     * @param limit the result limit. The maximum is 100.
     * @param offset the offset
     * @return the SearchFeed object
     * @throws GiphyException if an error occurs during the search
     */
    @SuppressWarnings("Duplicates")
    public SearchFeed search(String query, int limit, int offset, String lang) throws GiphyException {
        SearchFeed feed;

        HashMap<String, String> params = new HashMap<>();

        params.put("api_key", apiKey);
        params.put("q", query);
        if (limit > 100) {
            params.put("limit", "100");
        } else {
            params.put("limit", limit + "");
        }
        params.put("offset", offset + "");
        if (lang != null) {
            params.put("lang", lang);
        }
        params.put("rating", "G");

        Request request = new Request(UrlUtil.buildUrlQuery(SearchEndpoint, params));

        try {
            Response response = sender.sendRequest(request);
            feed = gson.fromJson(response.getBody(), SearchFeed.class);
        } catch (JsonSyntaxException | IOException e) {
            log.catching(e);
            throw new GiphyException(e);
        }

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
     * @throws GiphyException if an error occurs during the search
     */
    @SuppressWarnings("Duplicates")
    public SearchGiphy searchByID(String id) throws GiphyException {
        SearchGiphy giphy;

        HashMap<String, String> params = new HashMap<>();

        params.put("api_key", apiKey);

        Request request = new Request(UrlUtil.buildUrlQuery(IDEndpoint + id, params));

        try {
            Response response = sender.sendRequest(request);
            giphy = gson.fromJson(response.getBody(), SearchGiphy.class);
        } catch (JsonSyntaxException | IOException e) {
            log.catching(e);
            throw new GiphyException(e);
        }

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
     * @throws GiphyException if an error occurs during the search
     */
    @SuppressWarnings("Duplicates")
    public SearchGiphy translate(String query) throws GiphyException {
        SearchGiphy giphy;

        HashMap<String, String> params = new HashMap<>();

        params.put("api_key", apiKey);
        params.put("s", query);

        Request request = new Request(UrlUtil.buildUrlQuery(TranslateEndpoint, params));

        try {
            Response response = sender.sendRequest(request);
            giphy = gson.fromJson(response.getBody(), SearchGiphy.class);
        } catch (JsonSyntaxException | IOException e) {
            log.catching(e);
            throw new GiphyException(e);
        }

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
     * @throws GiphyException if an error occurs during the search
     */
    @SuppressWarnings("Duplicates")
    public SearchRandom searchRandom(String tag) throws GiphyException {
        SearchRandom random;

        HashMap<String, String> params = new HashMap<>();

        params.put("api_key", apiKey);
        params.put("tag", tag);

        Request request = new Request(UrlUtil.buildUrlQuery(RandomEndpoint, params));

        try {
            Response response = sender.sendRequest(request);
            random = gson.fromJson(response.getBody(), SearchRandom.class);
        } catch (JsonSyntaxException | IOException e) {
            log.catching(e);
            throw new GiphyException(e);
        }

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
     * @throws GiphyException if an error occurs during the search
     */
    @SuppressWarnings("Duplicates")
    public SearchFeed trend() throws GiphyException {
        SearchFeed feed;

        HashMap<String, String> params = new HashMap<>();

        params.put("api_key", apiKey);

        Request request = new Request(UrlUtil.buildUrlQuery(TrendingEndpoint, params));

        try {
            Response response = sender.sendRequest(request);
            feed = gson.fromJson(response.getBody(), SearchFeed.class);
        } catch (JsonSyntaxException | IOException e) {
            log.catching(e);
            throw new GiphyException(e);
        }

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
     * @param query the query parameters. Multiple parameters are separated by a space
     * @param offset the offset
     * @return the SearchFeed object
     * @throws GiphyException if an error occurs during the search
     */
    public SearchFeed searchSticker(String query, int offset) throws GiphyException {
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
     * @param query the query parameters. Multiple parameters are separated by a space
     * @param limit the result limit. The maximum is 100.
     * @param offset the offset
     * @return the SearchFeed object
     * @throws GiphyException if an error occurs during the search
     */
    @SuppressWarnings("Duplicates")
    public SearchFeed searchSticker(String query, int limit, int offset) throws GiphyException {
        SearchFeed feed;

        HashMap<String, String> params = new HashMap<>();

        params.put("api_key", apiKey);
        params.put("q", query);
        if (limit > 100) {
            params.put("limit", "100");
        } else {
            params.put("limit", limit + "");
        }
        params.put("offset", offset + "");

        Request request = new Request(UrlUtil.buildUrlQuery(SearchStickerEndpoint, params));

        try {
            Response response = sender.sendRequest(request);
            feed = gson.fromJson(response.getBody(), SearchFeed.class);
        } catch (JsonSyntaxException | IOException e) {
            log.catching(e);
            throw new GiphyException(e);
        }

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
     * @throws GiphyException if an error occurs during the search
     */
    @SuppressWarnings("Duplicates")
    public SearchGiphy translateSticker(String query) throws GiphyException {
        SearchGiphy giphy;

        HashMap<String, String> params = new HashMap<>();

        params.put("api_key", apiKey);
        params.put("s", query);

        Request request = new Request(UrlUtil.buildUrlQuery(TranslateStickerEndpoint, params));

        try {
            Response response = sender.sendRequest(request);
            giphy = gson.fromJson(response.getBody(), SearchGiphy.class);
        } catch (JsonSyntaxException | IOException e) {
            log.catching(e);
            throw new GiphyException(e);
        }

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
     * @throws GiphyException if an error occurs during the search
     */
    @SuppressWarnings("Duplicates")
    public SearchFeed trendSticker() throws GiphyException {
        SearchFeed feed;

        HashMap<String, String> params = new HashMap<>();

        params.put("api_key", apiKey);

        Request request = new Request(UrlUtil.buildUrlQuery(TrendingStickerEndpoint, params));

        try {
            Response response = sender.sendRequest(request);
            feed = gson.fromJson(response.getBody(), SearchFeed.class);
        } catch (JsonSyntaxException | IOException e) {
            log.catching(e);
            throw new GiphyException(e);
        }

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
     * @throws GiphyException if an error occurs during the search
     */
    @SuppressWarnings("Duplicates")
    public SearchRandom searchRandomSticker(String tag) throws GiphyException {
        SearchRandom random;

        HashMap<String, String> params = new HashMap<>();

        params.put("api_key", apiKey);
        params.put("tag", tag);

        Request request = new Request(UrlUtil.buildUrlQuery(RandomEndpointSticker, params));

        try {
            Response response = sender.sendRequest(request);
            random = gson.fromJson(response.getBody(), SearchRandom.class);
        } catch (JsonSyntaxException | IOException e) {
            log.catching(e);
            throw new GiphyException(e);
        }

        return random;
    }

}
