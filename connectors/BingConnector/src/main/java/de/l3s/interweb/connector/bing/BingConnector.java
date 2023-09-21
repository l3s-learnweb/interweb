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
    private static final int fallbackPerPageWeb = 50;
    private static final int fallbackPerPageImages = 150;
    private static final int fallbackPerPageVideos = 105;

    @Inject
    ObjectMapper mapper;

    @RestClient
    BingSearchClient searchClient;

    @RestClient
    BingSuggestClient suggestClient;

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
        return new ContentType[]{ContentType.webpage, ContentType.image, ContentType.video, ContentType.news};
    }

    @Override
    public Uni<SearchConnectorResults> search(SearchQuery query) throws ConnectorException {
        return processQuery(query).map(this::processResponse);
    }

    private Uni<BingResponse> processQuery(SearchQuery query) {
        if (query.getContentTypes().size() == 1) {
            if (query.getContentTypes().contains(ContentType.image)) {
                return searchClient.searchImages(
                        query.getQuery(),
                        query.getPerPage(fallbackPerPageImages),
                        query.getOffset(fallbackPerPageImages),
                        query.getLanguage(),
                        BingUtils.getMarket(query.getLanguage()),
                        BingUtils.createFreshness(null, query.getDateTo())
                );
            } else if (query.getContentTypes().contains(ContentType.video)) {
                return searchClient.searchVideos(
                        query.getQuery(),
                        query.getPerPage(fallbackPerPageVideos),
                        query.getOffset(fallbackPerPageVideos),
                        query.getLanguage(),
                        BingUtils.getMarket(query.getLanguage()),
                        BingUtils.createFreshness(null, query.getDateTo())
                );
            }
        }

        List<BingSearchClient.ResponseFilter> answerTypes = new ArrayList<>();
        for (ContentType contentType : query.getContentTypes()) {
            if (contentType == ContentType.webpage) {
                answerTypes.add(BingSearchClient.ResponseFilter.webpages);
            } else if (contentType == ContentType.image) {
                answerTypes.add(BingSearchClient.ResponseFilter.images);
            } else if (contentType == ContentType.video) {
                answerTypes.add(BingSearchClient.ResponseFilter.videos);
            } else if (contentType == ContentType.news) {
                answerTypes.add(BingSearchClient.ResponseFilter.news);
            }
        }

        return searchClient.search(
                query.getQuery(),
                query.getPerPage(fallbackPerPageWeb),
                query.getOffset(fallbackPerPageWeb),
                query.getLanguage(),
                BingUtils.getMarket(query.getLanguage()),
                BingUtils.createFreshness(query.getDateFrom(), query.getDateTo()),
                answerTypes.isEmpty() ? null : answerTypes.stream().map(Enum::name).collect(Collectors.joining(","))
        );
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

    private SearchConnectorResults processResponse(BingResponse response) throws ConnectorException {
        if (response == null) {
            throw new ConnectorException("No response");
        }

        if (response.getError() != null) {
            throw new ConnectorException(response.getError().getMessage());
        }

        int rank = 0;
        SearchConnectorResults results = new SearchConnectorResults();
        if (response.getWebPages() != null && response.getWebPages().getValues() != null) {
            WebPagesHolder webResults = response.getWebPages();
            if (webResults.getCurrentOffset() != null) {
                rank += webResults.getCurrentOffset();
            }

            if (webResults.getTotalEstimatedMatches() != null) {
                results.addTotalResults(webResults.getTotalEstimatedMatches());
            } else {
                results.addTotalResults(webResults.getValues().size());
            }

            for (WebPage page : webResults.getValues()) {
                SearchItem resultItem = new SearchItem(++rank);
                resultItem.setType(ContentType.webpage);
                resultItem.setTitle(page.getName());
                resultItem.setDescription(page.getSnippet());
                resultItem.setUrl(page.getUrl());
                resultItem.setDate(DateUtils.parse(page.getDateLastCrawled()));

                results.addResultItem(resultItem);
            }
        }

        if (response.getImages() != null && response.getImages().getValues() != null) {
            ImageHolder imagesResults = response.getImages();
            if (imagesResults.getCurrentOffset() != null) {
                rank += imagesResults.getCurrentOffset();
            }

            if (imagesResults.getTotalEstimatedMatches() != null) {
                results.addTotalResults(imagesResults.getTotalEstimatedMatches());
            } else {
                results.addTotalResults(imagesResults.getValues().size());
            }

            for (Image image : imagesResults.getValues()) {
                SearchItem resultItem = new SearchItem(++rank);
                resultItem.setType(ContentType.image);
                resultItem.setTitle(image.getName());
                resultItem.setUrl(image.getContentUrl());
                resultItem.setDate(DateUtils.parse(image.getDatePublished()));
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

        if (response.getVideos() != null && response.getVideos().getValues() != null) {
            VideoHolder videosResults = response.getVideos();
            if (videosResults.getCurrentOffset() != null) {
                rank += videosResults.getCurrentOffset();
            }

            if (videosResults.getTotalEstimatedMatches() != null) {
                results.addTotalResults(videosResults.getTotalEstimatedMatches());
            } else {
                results.addTotalResults(videosResults.getValues().size());
            }

            for (Video video : videosResults.getValues()) {
                SearchItem resultItem = new SearchItem(++rank);
                resultItem.setType(ContentType.video);
                resultItem.setTitle(video.getName());
                resultItem.setDescription(video.getDescription());
                resultItem.setUrl(video.getContentUrl());
                resultItem.setDate(DateUtils.parse(video.getDatePublished()));
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
                    resultItem.setEmbedUrl(StringUtils.parseSourceUrl(video.getEmbedHtml()));
                }

                results.addResultItem(resultItem);
            }
        }

        return results;
    }
}
