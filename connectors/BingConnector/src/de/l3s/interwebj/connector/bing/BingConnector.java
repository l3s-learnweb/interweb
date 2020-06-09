package de.l3s.interwebj.connector.bing;

import static de.l3s.interwebj.core.util.Assertions.notNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.azure.cognitiveservices.search.imagesearch.BingImageSearchAPI;
import com.microsoft.azure.cognitiveservices.search.imagesearch.BingImageSearchManager;
import com.microsoft.azure.cognitiveservices.search.imagesearch.models.ImagesModel;
import com.microsoft.azure.cognitiveservices.search.videosearch.BingVideoSearchAPI;
import com.microsoft.azure.cognitiveservices.search.videosearch.BingVideoSearchManager;
import com.microsoft.azure.cognitiveservices.search.videosearch.models.VideosModel;
import com.microsoft.azure.cognitiveservices.search.websearch.BingWebSearchAPI;
import com.microsoft.azure.cognitiveservices.search.websearch.BingWebSearchManager;
import com.microsoft.azure.cognitiveservices.search.websearch.models.AnswerType;
import com.microsoft.azure.cognitiveservices.search.websearch.models.Freshness;
import com.microsoft.azure.cognitiveservices.search.websearch.models.ImageObject;
import com.microsoft.azure.cognitiveservices.search.websearch.models.Images;
import com.microsoft.azure.cognitiveservices.search.websearch.models.SearchResponse;
import com.microsoft.azure.cognitiveservices.search.websearch.models.VideoObject;
import com.microsoft.azure.cognitiveservices.search.websearch.models.Videos;
import com.microsoft.azure.cognitiveservices.search.websearch.models.WebPage;
import com.microsoft.azure.cognitiveservices.search.websearch.models.WebWebAnswer;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.connector.ConnectorSearchResults;
import de.l3s.interwebj.core.connector.ServiceConnector;
import de.l3s.interwebj.core.query.ContentType;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.ResultItem;
import de.l3s.interwebj.core.query.Thumbnail;
import de.l3s.interwebj.core.util.CoreUtils;

/**
 * Bing is a web search engine owned and operated by Microsoft.
 * TODO missing search implementations: extras, search_in, ranking.
 *
 * @see <a href="https://docs.microsoft.com/en-us/rest/api/cognitiveservices-bingsearch/bing-web-api-v7-reference">Bing Search API</a>
 */
public class BingConnector extends ServiceConnector {
    private static final Logger log = LogManager.getLogger(BingConnector.class);
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public BingConnector() {
        super("Bing", "https://www.bing.com/", ContentType.text, ContentType.image, ContentType.video);
    }

    public BingConnector(AuthCredentials consumerAuthCredentials) {
        this();
        setAuthCredentials(consumerAuthCredentials);
    }

    @Override
    public ServiceConnector clone() {
        return new BingConnector(getAuthCredentials());
    }

    @Override
    public ConnectorSearchResults get(Query query, AuthCredentials authCredentials) throws InterWebException {
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

        if (query.getContentTypes().size() == 1 && query.getContentTypes().contains(ContentType.image)) {
            return getImagesSearch(query, count, language, authCredentials);
        } else if (query.getContentTypes().size() == 1 && query.getContentTypes().contains(ContentType.video)) {
            return getVideoSearch(query, count, language, authCredentials);
        } else {
            return getWebSearch(query, count, language, authCredentials);
        }
    }

    /**
     * Make a search using Bing Web Search API.
     * It can retrieve any type of content, but only limited amount and no estimated results count.
     *
     * https://docs.microsoft.com/en-us/azure/cognitive-services/bing-image-search/quickstarts/client-libraries?pivots=programming-language-java
     */
    private ConnectorSearchResults getWebSearch(Query query, int count, String language, AuthCredentials authCredentials) throws InterWebException {
        List<AnswerType> answerTypes = new ArrayList<>();
        for (ContentType contentType : query.getContentTypes()) {
            if (contentType == ContentType.text) {
                answerTypes.add(AnswerType.WEB_PAGES);
            } else if (contentType == ContentType.image) {
                answerTypes.add(AnswerType.IMAGES);
            } else if (contentType == ContentType.video) {
                answerTypes.add(AnswerType.VIDEOS);
            }
        }

        try {
            // Init Bing Client
            BingWebSearchAPI client = BingWebSearchManager.authenticate(authCredentials.getSecret());

            // Build query
            SearchResponse webData = client.bingWebs().search()
                .withQuery(query.getQuery())
                .withMarket(language)
                .withFreshness(Freshness.fromString(createFreshness(query)))
                .withCount(count)
                .withResponseFilter(answerTypes)
                .withOffset((query.getPage() - 1) * count)
                .execute();

            // Results go here
            ConnectorSearchResults results = new ConnectorSearchResults(query, getName());


            if (webData != null && webData.webPages() != null && webData.webPages().value() != null && !webData.webPages().value().isEmpty()) {
                WebWebAnswer webResults = webData.webPages();

                if (webResults.totalEstimatedMatches() != null) {
                    results.addTotalResultCount(webResults.totalEstimatedMatches());
                } else {
                    results.addTotalResultCount(webResults.value().size());
                }

                int index = 1;
                for (WebPage page : webResults.value()) {
                    ResultItem resultItem = new ResultItem(getName(), index++);
                    resultItem.setType(ContentType.text);
                    resultItem.setTitle(page.name());
                    resultItem.setDescription(page.snippet());
                    resultItem.setUrl(page.url());
                    resultItem.setDate(page.dateLastCrawled());

                    results.addResultItem(resultItem);
                }
            }

            if (webData != null && webData.images() != null && webData.images().value() != null && !webData.images().value().isEmpty()) {
                Images imagesResults = webData.images();

                if (imagesResults.totalEstimatedMatches() != null) {
                    results.addTotalResultCount(imagesResults.totalEstimatedMatches());
                } else {
                    results.addTotalResultCount(imagesResults.value().size());
                }

                int index = 1;
                for (ImageObject image : imagesResults.value()) {
                    ResultItem resultItem = new ResultItem(getName(), index++);
                    resultItem.setType(ContentType.image);
                    resultItem.setTitle(image.name());
                    resultItem.setDescription(image.description());
                    resultItem.setUrl(image.contentUrl());

                    try {
                        resultItem.setThumbnailMedium(new Thumbnail(image.thumbnailUrl(), image.thumbnail().width(), image.thumbnail().height()));
                    } catch (Exception e) {
                        log.warn(e);
                    }

                    try {
                        resultItem.setThumbnail(new Thumbnail(image.contentUrl(), image.width(), image.height()));
                    } catch (Exception e) {
                        log.warn(e);
                    }

                    results.addResultItem(resultItem);
                }
            }

            if (webData != null && webData.videos() != null && webData.videos().value() != null && !webData.videos().value().isEmpty()) {
                Videos videosResults = webData.videos();

                if (videosResults.totalEstimatedMatches() != null) {
                    results.addTotalResultCount(videosResults.totalEstimatedMatches());
                } else {
                    results.addTotalResultCount(videosResults.value().size());
                }

                int index = 1;
                for (VideoObject video : videosResults.value()) {
                    ResultItem resultItem = new ResultItem(getName(), index++);
                    resultItem.setType(ContentType.video);
                    resultItem.setTitle(video.name());
                    resultItem.setDescription(video.description());
                    resultItem.setUrl(video.contentUrl());

                    try {
                        resultItem.setThumbnailMedium(new Thumbnail(video.thumbnailUrl(), video.thumbnail().width(), video.thumbnail().height()));
                    } catch (Exception e) {
                        log.warn(e);
                    }

                    if (video.embedHtml() != null) {
                        resultItem.setEmbeddedCode(CoreUtils.cleanupEmbedHtml(video.embedHtml()));
                    }

                    results.addResultItem(resultItem);
                }
            }

            return results;
        } catch (Exception e) {
            throw new InterWebException(e);
        }
    }

    /**
     * Make a search using Bing Image Search API.
     *
     * https://docs.microsoft.com/en-us/azure/cognitive-services/bing-image-search/quickstarts/client-libraries?pivots=programming-language-java
     */
    private ConnectorSearchResults getImagesSearch(Query query, int count, String language, AuthCredentials authCredentials) throws InterWebException {
        try {
            // Init Bing Client
            BingImageSearchAPI client = BingImageSearchManager.authenticate(authCredentials.getSecret());

            // Build query
            ImagesModel imageResults = client.bingImages().search()
                .withQuery(query.getQuery())
                .withMarket(language)
                .withFreshness(com.microsoft.azure.cognitiveservices.search.imagesearch.models.Freshness.fromString(createFreshness(query)))
                .withCount(count)
                .withOffset(((query.getPage() - 1L) * count))
                .execute();

            // Results go here
            ConnectorSearchResults results = new ConnectorSearchResults(query, getName());

            if (imageResults != null && imageResults.value() != null && !imageResults.value().isEmpty()) {
                if (imageResults.totalEstimatedMatches() != null) {
                    results.setTotalResultCount(imageResults.totalEstimatedMatches());
                } else {
                    results.setTotalResultCount(imageResults.value().size());
                }

                int index = 1;
                for (com.microsoft.azure.cognitiveservices.search.imagesearch.models.ImageObject image : imageResults.value()) {
                    ResultItem resultItem = new ResultItem(getName(), index++);
                    resultItem.setType(ContentType.image);
                    resultItem.setTitle(image.name());
                    resultItem.setDescription(image.description());
                    resultItem.setUrl(image.contentUrl());

                    try {
                        resultItem.setThumbnailMedium(new Thumbnail(image.thumbnailUrl(), image.thumbnail().width(), image.thumbnail().height()));
                    } catch (Exception e) {
                        log.warn(e);
                    }

                    try {
                        resultItem.setThumbnail(new Thumbnail(image.contentUrl(), image.width(), image.height()));
                    } catch (Exception e) {
                        log.warn(e);
                    }

                    results.addResultItem(resultItem);
                }
            }

            return results;
        } catch (Exception e) {
            throw new InterWebException(e);
        }
    }

    /**
     * Make a search using Bing Video Search API.
     *
     * https://docs.microsoft.com/en-us/azure/cognitive-services/bing-video-search/quickstarts/client-libraries?pivots=programming-language-java
     */
    private ConnectorSearchResults getVideoSearch(Query query, int count, String language, AuthCredentials authCredentials) throws InterWebException {
        try {
            // Init Bing Client
            BingVideoSearchAPI client = BingVideoSearchManager.authenticate(authCredentials.getSecret());

            // Build query
            VideosModel videoResults = client.bingVideos().search()
                .withQuery(query.getQuery())
                .withMarket(language)
                .withFreshness(com.microsoft.azure.cognitiveservices.search.videosearch.models.Freshness.fromString(createFreshness(query)))
                .withCount(count)
                .withOffset(((query.getPage() - 1) * count))
                .execute();

            // Results go here
            ConnectorSearchResults results = new ConnectorSearchResults(query, getName());

            if (videoResults != null && videoResults.value() != null && !videoResults.value().isEmpty()) {
                if (videoResults.totalEstimatedMatches() != null) {
                    results.setTotalResultCount(videoResults.totalEstimatedMatches());
                } else {
                    results.setTotalResultCount(videoResults.value().size());
                }

                int index = 1;
                for (com.microsoft.azure.cognitiveservices.search.videosearch.models.VideoObject video : videoResults.value()) {
                    ResultItem resultItem = new ResultItem(getName(), index++);
                    resultItem.setType(ContentType.video);
                    resultItem.setTitle(video.name());
                    resultItem.setDescription(video.description());
                    resultItem.setUrl(video.contentUrl());
                    resultItem.setViewCount(video.viewCount().longValue());

                    try {
                        resultItem.setThumbnailMedium(new Thumbnail(video.thumbnailUrl(), video.thumbnail().width(), video.thumbnail().height()));
                    } catch (Exception e) {
                        log.warn(e);
                    }

                    if (video.embedHtml() != null) {
                        resultItem.setEmbeddedCode(CoreUtils.cleanupEmbedHtml(video.embedHtml()));
                    }

                    results.addResultItem(resultItem);
                }
            }

            return results;
        } catch (Exception e) {
            throw new InterWebException(e);
        }
    }

    /**
     * To get articles discovered by Bing during a specific timeframe, specify a date range in the form, YYYY-MM-DD..YYYY-MM-DD.
     * For example, &freshness=2019-02-01..2019-05-30. To limit the results to a single date, set this parameter to a specific date.
     * For example, &freshness=2019-02-04.
     */
    private static String createFreshness(Query query) {
        Date dateFrom = null;
        Date dateTill = new Date();

        if (query.getDateFrom() != null) {
            try {
                dateFrom = new Date(CoreUtils.parseDate(query.getDateFrom()));
            } catch (Exception e) {
                log.error(e);
            }
        }

        if (query.getDateTill() != null) {
            try {
                dateTill = new Date(CoreUtils.parseDate(query.getDateTill()));
            } catch (Exception e) {
                log.error(e);
            }
        }

        if (dateFrom != null) {
            String dateFromFormat = DATE_FORMAT.format(dateFrom);
            String dateTillFormat = DATE_FORMAT.format(dateTill);
            return dateFromFormat.equals(dateTillFormat) ? dateFromFormat : dateFromFormat + ".." + dateTillFormat;
        }

        return null;
    }

    private static String createMarket(String language) {
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
