package de.l3s.interweb.connector.bing;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.l3s.interweb.connector.bing.entity.*;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.search.Thumbnail;
import de.l3s.interweb.core.search.*;
import de.l3s.interweb.core.suggest.SuggestConnector;
import de.l3s.interweb.core.suggest.SuggestConnectorResults;
import de.l3s.interweb.core.suggest.SuggestQuery;
import de.l3s.interweb.core.util.DateUtils;
import de.l3s.interweb.core.util.StringUtils;

@Dependent
public class BingConnector implements SearchConnector, SuggestConnector {
    private static final Logger log = Logger.getLogger(BingConnector.class);

    @Override
    public String getName() {
        return "Bing";
    }

    @Override
    public String getBaseUrl() {
        return "https://bing.com/";
    }

    @Inject
    ObjectMapper mapper;

    @RestClient
    BingSearchClient searchClient;

    @RestClient
    BingSuggestClient suggestClient;

    @Override
    public ContentType[] getSearchTypes() {
        return new ContentType[]{ContentType.text, ContentType.image, ContentType.video};
    }

    @Override
    public SearchConnectorResults search(final SearchQuery query) throws ConnectorException {
        BingResponse response = null;
        if (query.getContentTypes().size() == 1) {
            if (query.getContentTypes().contains(ContentType.image)) {
                response = searchClient.searchImages(
                        query.getQuery(),
                        query.getPerPage(150),
                        query.getOffset(150),
                        query.getLanguage(),
                        BingUtils.getMarket(query.getLanguage()),
                        BingUtils.createFreshness(null, query.getDateTill())
                ).await().indefinitely();
            } else if (query.getContentTypes().contains(ContentType.video)) {
                response = searchClient.searchVideos(
                        query.getQuery(),
                        query.getPerPage(105),
                        query.getOffset(105),
                        query.getLanguage(),
                        BingUtils.getMarket(query.getLanguage()),
                        BingUtils.createFreshness(null, query.getDateTill())
                ).await().indefinitely();
            }
        }

        if (response == null) {
            List<BingSearchClient.ResponseFilter> answerTypes = new ArrayList<>();
            for (ContentType contentType : query.getContentTypes()) {
                if (contentType == ContentType.text) {
                    answerTypes.add(BingSearchClient.ResponseFilter.webpages);
                } else if (contentType == ContentType.image) {
                    answerTypes.add(BingSearchClient.ResponseFilter.images);
                } else if (contentType == ContentType.video) {
                    answerTypes.add(BingSearchClient.ResponseFilter.videos);
                }
            }

            response = searchClient.search(
                    query.getQuery(),
                    query.getPerPage(50),
                    query.getOffset(50),
                    query.getLanguage(),
                    BingUtils.getMarket(query.getLanguage()),
                    BingUtils.createFreshness(query.getDateFrom(), query.getDateTill()),
                    answerTypes.isEmpty() ? null : answerTypes.stream().map(Enum::name).collect(Collectors.joining(","))
            ).await().indefinitely();
        }

        return convertResponseToResults(response);
    }

    @Override
    public Uni<SuggestConnectorResults> suggest(SuggestQuery query) throws ConnectorException {
        return suggestClient.search(query.getQuery(), BingUtils.getMarket(query.getLanguage())).onItem().transform(Unchecked.function(data -> {
            try {
                String valuesJson = data.substring(data.indexOf('[', 1), data.indexOf(']') + 1); // no other way of getting the json array found :(
                String[] suggestionsArray = mapper.readValue(valuesJson, String[].class);
                SuggestConnectorResults results = new SuggestConnectorResults();
                results.addItems(suggestionsArray);
                return results;
            } catch (Exception e) {
                throw new ConnectorException("Failed to parse response", e);
            }
        }));
    }

    private SearchConnectorResults convertResponseToResults(BingResponse response) throws ConnectorException {
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
                resultItem.setDate(DateUtils.format(BingUtils.parseDate(page.getDateLastCrawled())));

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
                resultItem.setDate(DateUtils.format(BingUtils.parseDate(image.getDatePublished())));
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
                resultItem.setDate(DateUtils.format(BingUtils.parseDate(video.getDatePublished())));
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
    }
}
