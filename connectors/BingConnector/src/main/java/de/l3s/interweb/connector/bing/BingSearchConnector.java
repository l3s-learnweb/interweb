package de.l3s.interweb.connector.bing;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.Dependent;

import org.jboss.logging.Logger;

import de.l3s.interweb.connector.bing.client.BingApiService;
import de.l3s.interweb.connector.bing.client.BingUtils;
import de.l3s.interweb.connector.bing.client.entity.*;
import de.l3s.interweb.connector.bing.client.entity.query.BingQuery;
import de.l3s.interweb.connector.bing.client.entity.query.ResponseFilterParam;
import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.search.*;
import de.l3s.interweb.core.search.Thumbnail;
import de.l3s.interweb.core.util.Assertions;
import de.l3s.interweb.core.util.DateUtils;
import de.l3s.interweb.core.util.StringUtils;

/**
 * Bing is a web search engine owned and operated by Microsoft.
 * TODO missing search implementations: extras, search_in, ranking.
 *
 * @see <a href="https://docs.microsoft.com/en-us/rest/api/cognitiveservices-bingsearch/bing-web-api-v7-reference">Bing Search API</a>
 */
@Dependent
public class BingSearchConnector implements SearchConnector {
    private static final Logger log = Logger.getLogger(BingSearchConnector.class);

    @Override
    public String getName() {
        return "Bing";
    }

    @Override
    public String getBaseUrl() {
        return "https://bing.com/";
    }

    @Override
    public ContentType[] getSearchTypes() {
        return new ContentType[]{ContentType.text, ContentType.image, ContentType.video};
    }

    @Override
    public SearchConnectorResults search(SearchQuery query, AuthCredentials credentials) throws ConnectorException {
        Assertions.notNull(query, "query");

        // Prepare params
        int count = Math.max(query.getPerPage(), 20); // min 20 results per request to save money

        String language = query.getLanguage();
        if (language != null && language.length() == 2) {
            language = BingUtils.getMarket(language);
        }

        List<ResponseFilterParam> answerTypes = new ArrayList<>();
        for (ContentType contentType : query.getContentTypes()) {
            if (contentType == ContentType.text) {
                answerTypes.add(ResponseFilterParam.WEB_PAGES);
            } else if (contentType == ContentType.image) {
                answerTypes.add(ResponseFilterParam.IMAGES);
            } else if (contentType == ContentType.video) {
                answerTypes.add(ResponseFilterParam.VIDEOS);
            }
        }

        try {
            // Create bing request
            BingQuery bingQuery = new BingQuery();
            bingQuery.setQuery(query.getQuery());
            bingQuery.setMarket(language);
            bingQuery.setFreshness(createFreshness(query));
            bingQuery.setCount(count);
            bingQuery.setResponseFilter(answerTypes);
            bingQuery.setOffset((query.getPage() - 1) * count);

            BingApiService bingApiService = new BingApiService(credentials.getKey());
            BingResponse response = bingApiService.getResponseFromBingApi(bingQuery);

            // Results go here
            SearchConnectorResults results = new SearchConnectorResults();

            if (response != null && response.getWebPages() != null && response.getWebPages().getValues() != null) {
                WebPagesHolder webResults = response.getWebPages();

                if (webResults.getTotalEstimatedMatches() != null) {
                    results.addTotalResults(webResults.getTotalEstimatedMatches());
                } else {
                    results.addTotalResults(webResults.getValues().size());
                }

                int index = 1;
                for (WebPage page : webResults.getValues()) {
                    SearchItem resultItem = new SearchItem(index++);
                    resultItem.setType(ContentType.text);
                    resultItem.setTitle(page.getName());
                    resultItem.setDescription(page.getSnippet());
                    resultItem.setUrl(page.getUrl());
                    resultItem.setDate(DateUtils.format(parseDate(page.getDateLastCrawled())));

                    results.addResultItem(resultItem);
                }
            }

            if (response != null && response.getImages() != null && response.getImages().getValues() != null) {
                ImageHolder imagesResults = response.getImages();

                if (imagesResults.getTotalEstimatedMatches() != null) {
                    results.addTotalResults(imagesResults.getTotalEstimatedMatches());
                } else {
                    results.addTotalResults(imagesResults.getValues().size());
                }

                int index = 1;
                for (Image image : imagesResults.getValues()) {
                    SearchItem resultItem = new SearchItem(index++);
                    resultItem.setType(ContentType.image);
                    resultItem.setTitle(image.getName());
                    resultItem.setUrl(image.getContentUrl());
                    resultItem.setDate(DateUtils.format(parseDate(image.getDatePublished())));
                    resultItem.setWidth(image.getWidth());
                    resultItem.setHeight(image.getHeight());

                    try {
                        resultItem.setThumbnailMedium(new Thumbnail(image.getThumbnailUrl(), image.getThumbnail().getWidth(), image.getThumbnail().getHeight()));
                    } catch (Exception e) {
                        log.warn(e);
                    }

                    try {
                        resultItem.setThumbnail(new Thumbnail(image.getContentUrl(), image.getWidth(), image.getHeight()));
                    } catch (Exception e) {
                        log.warn(e);
                    }

                    results.addResultItem(resultItem);
                }
            }

            if (response != null && response.getVideos() != null && response.getVideos().getValues() != null) {
                VideoHolder videosResults = response.getVideos();

                if (videosResults.getTotalEstimatedMatches() != null) {
                    results.addTotalResults(videosResults.getTotalEstimatedMatches());
                } else {
                    results.addTotalResults(videosResults.getValues().size());
                }

                int index = 1;
                for (Video video : videosResults.getValues()) {
                    SearchItem resultItem = new SearchItem(index++);
                    resultItem.setType(ContentType.video);
                    resultItem.setTitle(video.getName());
                    resultItem.setDescription(video.getDescription());
                    resultItem.setUrl(video.getContentUrl());
                    resultItem.setDate(DateUtils.format(parseDate(video.getDatePublished())));
                    resultItem.setWidth(video.getWidth());
                    resultItem.setHeight(video.getHeight());

                    if (video.getDuration() != null) {
                        Duration duration = Duration.parse(video.getDuration());
                        resultItem.setDuration(duration.getSeconds());
                    }

                    try {
                        resultItem.setThumbnailMedium(new Thumbnail(video.getThumbnailUrl(), video.getThumbnail().getWidth(), video.getThumbnail().getHeight()));
                    } catch (Exception e) {
                        log.warn(e);
                    }

                    if (video.getEmbedHtml() != null) {
                        resultItem.setEmbeddedUrl(StringUtils.parseSourceUrl(video.getEmbedHtml()));
                    }

                    results.addResultItem(resultItem);
                }
            }

            return results;
        } catch (Exception e) {
            throw new ConnectorException(e);
        }
    }

    protected static ZonedDateTime parseDate(String dateString) throws ConnectorException {
        if (dateString == null) {
            return null;
        }

        try {
            return ZonedDateTime.parse(dateString.split("\\.")[0], DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.systemDefault()));
        } catch (DateTimeParseException e) {
            throw new ConnectorException("dateString: [" + dateString + "] " + e.getMessage());
        }
    }

    /**
     * To get articles discovered by Bing during a specific timeframe, specify a date range in the form, YYYY-MM-DD..YYYY-MM-DD.
     * For example, &freshness=2019-02-01..2019-05-30. To limit the results to a single date, set this parameter to a specific date.
     * For example, &freshness=2019-02-04.
     */
    private static String createFreshness(SearchQuery query) {
        if (query.getDateFrom() != null) {
            String dateFromFormat = DateTimeFormatter.ISO_LOCAL_DATE.format(query.getDateFrom());

            if (query.getDateTill() != null && !query.getDateFrom().equals(query.getDateTill())) {
                String dateTillFormat = DateTimeFormatter.ISO_LOCAL_DATE.format(query.getDateTill());
                return dateFromFormat + ".." + dateTillFormat;
            }

            return dateFromFormat;
        }

        return null;
    }
}
