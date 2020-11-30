package de.l3s.bingService.services;

import java.io.IOException;

import de.l3s.bingService.models.BingResponse;
import de.l3s.bingService.models.query.BingQuery;
import de.l3s.interwebj.core.InterWebException;

public interface BingRequestConstants {
    String API_HOST = "https://api.cognitive.microsoft.com";
    String API_BASE_PATH = "/bing/v7.0/search";
    String API_IMAGES_PATH = "/bing/v7.0/images/search";
    String API_VIDEOS_PATH = "/bing/v7.0/videos/search";
    String KEY_HEADER_NAME = "Ocp-Apim-Subscription-Key";
    String PARAMETER_SAFESEARCH = "safesearch";
    String PARAMETER_MKT = "mkt";
    String PARAMETER_COUNT = "count";
    String PARAMETER_QUERY = "q";
    String PARAMETER_OFFSET = "offset";
    String PARAMETER_FRESHNESS = "freshness";
    String PARAMETER_RESPONSE_FILTER = "responsefilter";
    String PARAMETER_LANGUAGE = "setLang";
    String PARAMETER_TEXT_FORMAT = "textFormat";
    String PARAMETER_VALUE_HTML = "HTML";
    String PARAMETER_TEXT_DECORATIONS = "textDecorations";
    String PARAMETER_VALUE_TRUE = "true";

    BingResponse getResponseFromBingApi(BingQuery bingQuery) throws InterWebException, IOException, InterruptedException;
}
