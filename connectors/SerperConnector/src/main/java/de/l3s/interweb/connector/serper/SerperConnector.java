package de.l3s.interweb.connector.serper;

import de.l3s.interweb.connector.serper.entity.KnowledgeGraph;
import de.l3s.interweb.connector.serper.entity.OrganicResult;
import de.l3s.interweb.connector.serper.entity.SearchRequest;
import de.l3s.interweb.connector.serper.entity.SearchResponse;
import de.l3s.interweb.connector.serper.entity.Suggestion;

import de.l3s.interweb.core.suggest.SuggestConnectorResults;
import de.l3s.interweb.core.suggest.SuggestQuery;

import io.smallrye.mutiny.unchecked.Unchecked;

import jakarta.enterprise.context.Dependent;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.search.*;

import java.util.Optional;

@Dependent
public class SerperConnector implements SearchConnector {

    private static final double PRICE_PER_1K_CREDITS = 1;

    @RestClient
    SerperClient serperClient;

    @Override
    public String getName() {
        return "Serper";
    }

    @Override
    public String getBaseUrl() {
        return "https://serper.dev";
    }

    @Override
    public ContentType[] getSearchTypes() {
        return new ContentType[]{ContentType.webpage, ContentType.image, ContentType.video, ContentType.news};
    }

    public Uni<SuggestConnectorResults> suggest(SuggestQuery query) throws ConnectorException {
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
    }

    @Override
    public Uni<SearchConnectorResults> search(SearchQuery query) throws ConnectorException {
        SearchRequest request = buildSearchRequest(query);
        return serperClient.search(request).onItem().transform(Unchecked.function(response -> {
            try {
                return processSearchResponse(response);
            } catch (Exception e) {
                throw new ConnectorException("Failed to process search response", e);
            }
        }));
    }

    private SearchRequest buildSearchRequest(SearchQuery query) {
        SearchRequest request = new SearchRequest();
        request.setQuery(query.getQuery());
        if (query.getLanguage() != null) {
            request.setLanguage(query.getLanguage());
        }
        if (query.getPage() > 0) {
            request.setPage(query.getPage());
        }
        if (query.getPerPage() > 0) {
            request.setResults(query.getPerPage());
        }
        return request;
    }

    private SearchConnectorResults processSearchResponse(SearchResponse response) throws ConnectorException {
        if (response == null) {
            throw new ConnectorException("No response received");
        }

        SearchConnectorResults results = new SearchConnectorResults();
        int page = Optional.ofNullable(response.getSearchParameters().getPage()).orElse(1);
        int perPage = Optional.ofNullable(response.getSearchParameters().getResults()).orElse(10);
        int rank = (page - 1) * perPage;

        // Process organic search results
        if (response.getOrganic() != null) {
            for (OrganicResult organic : response.getOrganic()) {
                SearchItem item = new SearchItem(++rank);
                item.setType(ContentType.webpage);
                item.setTitle(organic.getTitle());
                item.setDescription(organic.getSnippet());
                item.setUrl(organic.getLink());
                results.addResultItem(item);
            }
        }

        results.setEstimatedCost(PRICE_PER_1K_CREDITS / 1000 * response.getCredits());
        return results;
    }
}
