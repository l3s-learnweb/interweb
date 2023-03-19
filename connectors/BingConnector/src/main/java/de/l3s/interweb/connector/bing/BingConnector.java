package de.l3s.interweb.connector.bing;

import static de.l3s.interweb.core.util.Assertions.notNull;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.auto.service.AutoService;

import de.l3s.bingService.models.BingResponse;
import de.l3s.bingService.models.Image;
import de.l3s.bingService.models.ImageHolder;
import de.l3s.bingService.models.Video;
import de.l3s.bingService.models.VideoHolder;
import de.l3s.bingService.models.WebPage;
import de.l3s.bingService.models.WebPagesHolder;
import de.l3s.bingService.models.query.BingQuery;
import de.l3s.bingService.models.query.ResponseFilterParam;
import de.l3s.bingService.services.BingApiService;
import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.InterWebException;
import de.l3s.interweb.core.search.SearchResults;
import de.l3s.interweb.core.search.SearchProvider;
import de.l3s.interweb.core.query.ContentType;
import de.l3s.interweb.core.query.Query;
import de.l3s.interweb.core.search.SearchItem;
import de.l3s.interweb.core.query.Thumbnail;
import de.l3s.interweb.core.util.DateUtils;
import de.l3s.interweb.core.util.StringUtils;

/**
 * Bing is a web search engine owned and operated by Microsoft.
 * TODO missing search implementations: extras, search_in, ranking.
 *
 * @see <a href="https://docs.microsoft.com/en-us/rest/api/cognitiveservices-bingsearch/bing-web-api-v7-reference">Bing Search API</a>
 */
@AutoService(SearchProvider.class)
public class BingConnector extends SearchProvider {
    private static final Logger log = LogManager.getLogger(BingConnector.class);

    public BingConnector() {
        super("Bing", "https://www.bing.com/", ContentType.text, ContentType.image, ContentType.video);
    }

    public BingConnector(AuthCredentials consumerAuthCredentials) {
        this();
        setAuthCredentials(consumerAuthCredentials);
    }

    @Override
    public SearchProvider clone() {
        return new BingConnector(getAuthCredentials());
    }

    @Override
    public SearchResults get(Query query, AuthCredentials authCredentials) throws InterWebException {
        notNull(query, "query");

        if (authCredentials == null) {
            authCredentials = getAuthCredentials();
        }

        // Prepare params
        int count = Math.max(query.getPerPage(), 20); // min 20 results per request to save money

        String language = query.getLanguage();
        if (language != null && language.length() == 2) {
            language = createMarket(language);
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

            BingApiService bingApiService = new BingApiService(authCredentials.getSecret());
            BingResponse response = bingApiService.getResponseFromBingApi(bingQuery);

            // Results go here
            SearchResults results = new SearchResults(query, getName());

            if (response != null && response.getWebPages() != null && response.getWebPages().getValues() != null) {
                WebPagesHolder webResults = response.getWebPages();

                if (webResults.getTotalEstimatedMatches() != null) {
                    results.addTotalResults(webResults.getTotalEstimatedMatches());
                } else {
                    results.addTotalResults(webResults.getValues().size());
                }

                int index = 1;
                for (WebPage page : webResults.getValues()) {
                    SearchItem resultItem = new SearchItem(getName(), index++);
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
                    SearchItem resultItem = new SearchItem(getName(), index++);
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
                    SearchItem resultItem = new SearchItem(getName(), index++);
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
                        String embeddedCode = StringUtils.replaceEachRepeatedly(video.getEmbedHtml(), new String[] {"'", "&#34;", "&#39;", "&quot;", "&apos;"}, new String[]{"\"", "\"", "\"", "\"", "\""});
                        resultItem.setEmbeddedUrl(StringUtils.parseSourceUrl(embeddedCode));
                    }

                    results.addResultItem(resultItem);
                }
            }

            return results;
        } catch (Exception e) {
            throw new InterWebException(e);
        }
    }

    protected static ZonedDateTime parseDate(String dateString) throws InterWebException {
        if (dateString == null) {
            return null;
        }

        try {
            return ZonedDateTime.parse(dateString.split("\\.")[0], DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.systemDefault()));
        } catch (DateTimeParseException e) {
            throw new InterWebException("dateString: [" + dateString + "] " + e.getMessage());
        }
    }

    /**
     * To get articles discovered by Bing during a specific timeframe, specify a date range in the form, YYYY-MM-DD..YYYY-MM-DD.
     * For example, &freshness=2019-02-01..2019-05-30. To limit the results to a single date, set this parameter to a specific date.
     * For example, &freshness=2019-02-04.
     */
    private static String createFreshness(Query query) {
        ZonedDateTime dateFrom = null;
        ZonedDateTime dateTill = ZonedDateTime.now();

        if (query.getDateFrom() != null) {
            try {
                dateFrom = DateUtils.parse(query.getDateFrom());
            } catch (Exception e) {
                log.error("Error parsing from date", e);
            }
        }

        if (query.getDateTill() != null) {
            try {
                dateTill = DateUtils.parse(query.getDateTill());
            } catch (Exception e) {
                log.error("Error parsing to date", e);
            }
        }

        if (dateFrom != null) {
            String dateFromFormat = DateTimeFormatter.ISO_LOCAL_DATE.format(dateFrom);
            String dateTillFormat = DateTimeFormatter.ISO_LOCAL_DATE.format(dateTill);
            return dateFromFormat.equals(dateTillFormat) ? dateFromFormat : dateFromFormat + ".." + dateTillFormat;
        }

        return null;
    }

    protected static String createMarket(String language) {
        if (language.equalsIgnoreCase("ar")) {
            return "ar-XA";
        } else if (language.equalsIgnoreCase("bg")) {
            return "bg-BG";
        } else if (language.equalsIgnoreCase("cs")) {
            return "cs-CZ";
        } else if (language.equalsIgnoreCase("da")) {
            return "da-DK";
        } else if (language.equalsIgnoreCase("de")) {
            return "de-DE";
        } else if (language.equalsIgnoreCase("el")) {
            return "el-GR";
        } else if (language.equalsIgnoreCase("es")) {
            return "es-ES";
        } else if (language.equalsIgnoreCase("et")) {
            return "et-EE";
        } else if (language.equalsIgnoreCase("fi")) {
            return "fi-FI";
        } else if (language.equalsIgnoreCase("fr")) {
            return "fr-FR";
        } else if (language.equalsIgnoreCase("he")) {
            return "he-IL";
        } else if (language.equalsIgnoreCase("hr")) {
            return "hr-HR";
        } else if (language.equalsIgnoreCase("hu")) {
            return "hu-HU";
        } else if (language.equalsIgnoreCase("it")) {
            return "it-IT";
        } else if (language.equalsIgnoreCase("ja")) {
            return "ja-JP";
        } else if (language.equalsIgnoreCase("ko")) {
            return "ko-KR";
        } else if (language.equalsIgnoreCase("lt")) {
            return "lt-LT";
        } else if (language.equalsIgnoreCase("lv")) {
            return "lv-LV";
        } else if (language.equalsIgnoreCase("nb")) {
            return "nb-NO";
        } else if (language.equalsIgnoreCase("nl")) {
            return "nl-NL";
        } else if (language.equalsIgnoreCase("pl")) {
            return "pl-PL";
        } else if (language.equalsIgnoreCase("pt")) {
            return "pt-PT";
        } else if (language.equalsIgnoreCase("ro")) {
            return "ro-RO";
        } else if (language.equalsIgnoreCase("ru")) {
            return "ru-RU";
        } else if (language.equalsIgnoreCase("sk")) {
            return "sk-SK";
        } else if (language.equalsIgnoreCase("sl")) {
            return "sl-SL";
        } else if (language.equalsIgnoreCase("sv")) {
            return "sv-SE";
        } else if (language.equalsIgnoreCase("th")) {
            return "th-TH";
        } else if (language.equalsIgnoreCase("tr")) {
            return "tr-TR";
        } else if (language.equalsIgnoreCase("uk")) {
            return "uk-UA";
        } else if (language.equalsIgnoreCase("zh")) {
            return "zh-CN";
        } else {
            return "en-US";
        }
    }
}
