package de.l3s.interwebj.connector.bing;

import static de.l3s.interwebj.core.util.Assertions.notNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.azure.cognitiveservices.search.websearch.BingWebSearchAPI;
import com.microsoft.azure.cognitiveservices.search.websearch.BingWebSearchManager;
import com.microsoft.azure.cognitiveservices.search.websearch.models.AnswerType;
import com.microsoft.azure.cognitiveservices.search.websearch.models.ImageObject;
import com.microsoft.azure.cognitiveservices.search.websearch.models.Images;
import com.microsoft.azure.cognitiveservices.search.websearch.models.SearchResponse;
import com.microsoft.azure.cognitiveservices.search.websearch.models.VideoObject;
import com.microsoft.azure.cognitiveservices.search.websearch.models.Videos;
import com.microsoft.azure.cognitiveservices.search.websearch.models.WebPage;
import com.microsoft.azure.cognitiveservices.search.websearch.models.WebWebAnswer;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.core.AbstractServiceConnector;
import de.l3s.interwebj.core.core.ServiceConnector;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.QueryResult;
import de.l3s.interwebj.core.query.ResultItem;
import de.l3s.interwebj.core.query.Thumbnail;

public class BingConnector extends AbstractServiceConnector implements Cloneable {
    private static final Logger log = LogManager.getLogger(BingConnector.class);

    public BingConnector() {
        super("Bing", "http://www.bing.com", new TreeSet<>(Arrays.asList("text", "image", "video")));
    }

    public BingConnector(AuthCredentials consumerAuthCredentials) {
        this();
        setAuthCredentials(consumerAuthCredentials);
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

    @Override
    public ServiceConnector clone() {
        return new BingConnector(getAuthCredentials());
    }

    @Override
    public QueryResult get(Query query, AuthCredentials authCredentials) throws InterWebException {
        notNull(query, "query");

        if (authCredentials == null) {
            authCredentials = getAuthCredentials(); // we have to use this; authCredentials parameter is null
        }

        // Prepare params
        int count = Math.max(query.getResultCount(), 20); // min 20 results per request to save money

        String language = query.getLanguage();
        if (language != null && language.length() == 2) {
            language = createMarket(language);
        }

        List<AnswerType> answerTypes = new ArrayList<>();
        for (String contentType : query.getContentTypes()) {
            if (contentType.equals(Query.CT_TEXT)) {
                answerTypes.add(AnswerType.WEB_PAGES);
            } else if (contentType.equals(Query.CT_IMAGE)) {
                answerTypes.add(AnswerType.IMAGES);
            } else if (contentType.equals(Query.CT_VIDEO)) {
                answerTypes.add(AnswerType.VIDEOS);
            }
        }

        // TODO: move this log to core if requires
        // String key = (authCredentials != null) ? authCredentials.getKey() : "no";
        // Environment.getInstance().getDatabase().logQuery(key, query.getQuery());

        try {
            // Init Bing Client
            BingWebSearchAPI client = BingWebSearchManager.authenticate(authCredentials.getKey());

            // Build query
            SearchResponse webData = client.bingWebs().search()
                    .withQuery(query.getQuery())
                    .withMarket(language)
                    .withCount(count)
                    .withResponseFilter(answerTypes)
                    .withOffset((query.getPage() - 1) * count)
                    .execute();

            // Results go here
            QueryResult results = new QueryResult(query);


            if (webData != null && webData.webPages() != null && webData.webPages().value() != null && !webData.webPages().value().isEmpty()) {
                WebWebAnswer webResults = webData.webPages();

                if (webResults.totalEstimatedMatches() != null) {
                    results.addTotalResultCount(webResults.totalEstimatedMatches());
                }

                int index = 1;
                for (WebPage page : webResults.value()) {
                    ResultItem resultItem = new ResultItem(getName());
                    resultItem.setType(Query.CT_TEXT);
                    resultItem.setTitle(page.name());
                    resultItem.setDescription(page.snippet());
                    resultItem.setUrl(page.url());
                    resultItem.setRank(index++);
                    resultItem.setDate(page.dateLastCrawled());
                    resultItem.setTotalResultCount(webResults.totalEstimatedMatches());

                    results.addResultItem(resultItem);
                }
            }

            if (webData != null && webData.images() != null && webData.images().value() != null && !webData.images().value().isEmpty()) {
                Images imagesResults = webData.images();

                if (imagesResults.totalEstimatedMatches() != null) {
                    results.addTotalResultCount(imagesResults.totalEstimatedMatches());
                }

                int index = 1;
                for (ImageObject image : imagesResults.value()) {
                    ResultItem resultItem = new ResultItem(getName());
                    resultItem.setType(Query.CT_IMAGE);
                    resultItem.setTitle(image.name());
                    resultItem.setDescription(image.description());
                    resultItem.setUrl(image.contentUrl());
                    resultItem.setRank(index++);

                    Set<Thumbnail> thumbnails = new LinkedHashSet<>();

                    try {
                        Thumbnail thumbnail = new Thumbnail(image.thumbnailUrl(), image.thumbnail().width(), image.thumbnail().height());

                        if (thumbnail.getUrl() != null && thumbnail.getWidth() > 0 && thumbnail.getHeight() > 0) {
                            thumbnails.add(thumbnail);
                            resultItem.setEmbeddedSize1("<img src=\"" + thumbnail.getUrl()
                                    + "\" height=\"" + thumbnail.getHeight()
                                    + "\" width=\"" + thumbnail.getWidth() + "\"/>");
                        }
                    } catch (Exception e) {
                        log.warn(e.getMessage());
                    }

                    try {
                        Thumbnail thumbnail = new Thumbnail(image.contentUrl(), image.thumbnail().width(), image.thumbnail().height());

                        if (thumbnail.getUrl() != null && thumbnail.getWidth() > 0 && thumbnail.getHeight() > 0) {
                            thumbnails.add(thumbnail);
                            resultItem.setEmbeddedSize2("<img src=\"" + thumbnail.getUrl()
                                    + "\" height=\"" + thumbnail.getHeight()
                                    + "\" width=\"" + thumbnail.getWidth() + "\"/>");
                        }
                    } catch (Exception e) {
                        log.warn(e.getMessage());
                    }

                    resultItem.setThumbnails(thumbnails);
                    results.addResultItem(resultItem);
                }
            }

            if (webData != null && webData.videos() != null && webData.videos().value() != null && !webData.videos().value().isEmpty()) {
                Videos videosResults = webData.videos();

                if (videosResults.totalEstimatedMatches() != null) {
                    results.addTotalResultCount(videosResults.totalEstimatedMatches());
                }

                int index = 1;
                for (VideoObject video : videosResults.value()) {
                    ResultItem resultItem = new ResultItem(getName());
                    resultItem.setType(Query.CT_VIDEO);
                    resultItem.setTitle(video.name());
                    resultItem.setDescription(video.description());
                    resultItem.setUrl(video.contentUrl());
                    resultItem.setRank(index++);

                    Set<Thumbnail> thumbnails = new LinkedHashSet<>();

                    try {
                        Thumbnail thumbnail = new Thumbnail(video.thumbnailUrl(), video.thumbnail().width(), video.thumbnail().height());

                        if (thumbnail.getUrl() != null && thumbnail.getWidth() > 0 && thumbnail.getHeight() > 0) {
                            thumbnails.add(thumbnail);
                            resultItem.setEmbeddedSize1("<img src=\"" + thumbnail.getUrl()
                                    + "\" height=\"" + thumbnail.getHeight()
                                    + "\" width=\"" + thumbnail.getWidth() + "\"/>");
                        }
                    } catch (Exception e) {
                        log.warn(e.getMessage());
                    }

                    try {
                        Thumbnail thumbnail = new Thumbnail(video.contentUrl(), video.thumbnail().width(), video.thumbnail().height());

                        if (thumbnail.getUrl() != null && thumbnail.getWidth() > 0 && thumbnail.getHeight() > 0) {
                            thumbnails.add(thumbnail);
                            resultItem.setEmbeddedSize2(video.embedHtml());
                        }
                    } catch (Exception e) {
                        log.warn(e.getMessage());
                    }

                    resultItem.setThumbnails(thumbnails);
                    results.addResultItem(resultItem);
                }
            }

            return results;
        } catch (Exception e) {
            throw new InterWebException(e);
        }
    }

    @Override
    public boolean isConnectorRegistrationDataRequired() {
        return false;
    }

    @Override
    public boolean isRegistered() {
        return true;
    }

    @Override
    public boolean isUserRegistrationDataRequired() {
        return false;
    }

    @Override
    public boolean isUserRegistrationRequired() {
        return false;
    }
}
