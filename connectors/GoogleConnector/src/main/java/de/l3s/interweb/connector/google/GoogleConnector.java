package de.l3s.interweb.connector.google;

import java.util.Optional;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.l3s.interweb.connector.google.serper.*;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.search.*;
import de.l3s.interweb.core.suggest.SuggestConnector;
import de.l3s.interweb.core.suggest.SuggestConnectorResults;
import de.l3s.interweb.core.suggest.SuggestQuery;

@Dependent
public class GoogleConnector implements SuggestConnector, SearchConnector {

    private static final double SERPER_PRICE_PER_1K_CREDITS = 1;

    @Override
    public String getName() {
        return "Google";
    }

    @Override
    public String getBaseUrl() {
        return "https://google.com/";
    }

    @Inject
    ObjectMapper mapper;

    @RestClient
    GoogleSuggestClient suggestClient;

    @RestClient
    SerperClient serperClient;

    @Override
    public Uni<SuggestConnectorResults> suggest(SuggestQuery query) throws ConnectorException {
        try {
            return suggestClient.search(query.getQuery(), query.getLanguage()).onItem().transform(Unchecked.function(data -> {
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
        } catch (Exception e) {
            throw new ConnectorException("Failed to execute suggest", e);
        }
    }

    /**
     * Uses the Serper API for suggestions, which is a paid. We can use Google's endpoint for free.
     */
    @Deprecated
    public Uni<SuggestConnectorResults> serperSuggest(SuggestQuery query) throws ConnectorException {
        try {
            return serperClient.autocomplete(query.getQuery()).onItem().transform(Unchecked.function(data -> {
                try {
                    String[] suggestionsArray = data.getSuggestions().stream().map(Suggestion::getValue).toArray(String[]::new);
                    SuggestConnectorResults results = new SuggestConnectorResults();
                    results.addItems(suggestionsArray);
                    return results;
                } catch (Exception e) {
                    throw new ConnectorException("Failed to parse response", e);
                }
            }));
        } catch (Exception e) {
            throw new ConnectorException("Failed to execute suggest", e);
        }
    }

    @Override
    public ContentType[] getSearchTypes() {
        return new ContentType[]{ContentType.webpage, ContentType.image, ContentType.video, ContentType.news};
    }

    @Override
    public Uni<SearchConnectorResults> search(SearchQuery query) throws ConnectorException {
        try {
            SearchRequest request = buildSearchRequest(query);
            return serperClient.search(request).onItem().transform(Unchecked.function(response -> {
                try {
                    return processSearchResponse(response);
                } catch (Exception e) {
                    throw new ConnectorException("Failed to process search response", e);
                }
            }));
        } catch (Exception e) {
            throw new ConnectorException("Failed to execute search", e);
        }
    }

    private SearchRequest buildSearchRequest(SearchQuery query) {
        SearchRequest request = new SearchRequest();
        request.setQuery(query.getQuery());
        request.setDateRange(SerperUtils.mapDateRange(query.getDateFrom(), query.getDateTo()));

        if (query.getContentTypes().size() == 1) {
            if (query.getContentTypes().contains(ContentType.webpage)) {
                request.setType("search");
            } else if (query.getContentTypes().contains(ContentType.image)) {
                request.setType("images");
            } else if (query.getContentTypes().contains(ContentType.video)) {
                request.setType("videos");
            } else if (query.getContentTypes().contains(ContentType.news)) {
                request.setType("news");
            } else {
                throw new ConnectorException("Unsupported content type: " + query.getContentTypes());
            }
        } else if (!query.getContentTypes().isEmpty()) {
            throw new ConnectorException("This connector only supports searching for one content type at a time.");
        }

        if (query.getLanguage() != null) {
            request.setLanguage(query.getLanguage());
        }
        if (query.getCountry() != null) {
            request.setCountry(query.getCountry());
        }
        if (query.getPage() > 0) {
            request.setPage(query.getPage());
        }

        if (query.getPerPage() != null && query.getPerPage() > 0) {
            if (query.getPerPage() > 10) {
                request.setPerPage(100); // serper charges 2 credits for anything above 10 results, so we set it to max 100
            } else {
                request.setPerPage(query.getPerPage());
            }
        }
        return request;
    }

    private SearchConnectorResults processSearchResponse(SearchResponse response) throws ConnectorException {
        if (response == null) {
            throw new ConnectorException("No response received");
        }

        SearchConnectorResults results = new SearchConnectorResults();
        int page = Optional.ofNullable(response.getSearchParameters().getPage()).orElse(1);
        int perPage = Optional.ofNullable(response.getSearchParameters().getPerPage()).orElse(10);

        int rank = (page - 1) * perPage;

        if (response.getOrganic() != null) {
            for (OrganicResult organic : response.getOrganic()) {
                SearchItem item = new SearchItem(++rank);
                item.setType(ContentType.webpage);
                item.setTitle(organic.getTitle());
                item.setDescription(organic.getSnippet());
                item.setUrl(organic.getLink());
                item.setDate(SerperUtils.parseDate(organic.getDate(), response.getSearchParameters().getLanguage()));
                results.addResultItem(item);
            }
        }

        if (response.getImages() != null) {
            for (ImageResult image : response.getImages()) {
                SearchItem item = new SearchItem(++rank);
                item.setType(ContentType.image);
                item.setTitle(image.getTitle());
                item.setUrl(image.getLink());
                item.setAuthor(image.getSource() != null ? image.getSource() : image.getDomain());

                if (image.getImageUrl() != null) {
                    item.setThumbnailOriginal(new Thumbnail(image.getImageUrl(), image.getImageWidth(), image.getImageHeight()));
                }

                if (image.getThumbnailUrl() != null) {
                    item.setThumbnail(new Thumbnail(image.getThumbnailUrl(), image.getThumbnailWidth(), image.getThumbnailHeight()));
                }

                results.addResultItem(item);
            }
        }

        if (response.getVideos() != null) {
            for (VideoResult video : response.getVideos()) {
                SearchItem item = new SearchItem(++rank);
                item.setType(ContentType.video);
                item.setTitle(video.getTitle());
                item.setDescription(video.getSnippet());
                item.setUrl(video.getLink());
                item.setDuration(SerperUtils.parseDuration(video.getDuration()));
                item.setAuthor(video.getChannel() != null ? video.getChannel() : video.getSource());
                if (video.getDate() != null) {
                    item.setDate(SerperUtils.parseDate(video.getDate(), response.getSearchParameters().getLanguage()));
                }
                if (video.getImageUrl() != null) {
                    item.setThumbnailOriginal(new Thumbnail(video.getImageUrl()));
                }
                results.addResultItem(item);
            }
        }

        if (response.getNews() != null) {
            for (NewsResult news : response.getNews()) {
                SearchItem item = new SearchItem(++rank);
                item.setType(ContentType.news);
                item.setTitle(news.getTitle());
                item.setDescription(news.getSnippet());
                item.setUrl(news.getLink());
                item.setAuthor(news.getSource());
                if (news.getDate() != null) {
                    item.setDate(SerperUtils.parseDate(news.getDate(), response.getSearchParameters().getLanguage()));
                }
                if (news.getImageUrl() != null) {
                    item.setThumbnailOriginal(new Thumbnail(news.getImageUrl()));
                }
                results.addResultItem(item);
            }
        }

        results.setEstimatedCost(SERPER_PRICE_PER_1K_CREDITS / 1000 * response.getCredits());
        return results;
    }
}
