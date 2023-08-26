package de.l3s.interweb.connector.bing;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.l3s.interweb.connector.bing.client.BingUtils;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.suggest.SuggestConnector;
import de.l3s.interweb.core.suggest.SuggestConnectorResults;
import de.l3s.interweb.core.suggest.SuggestQuery;

@Dependent
public class BingConnector implements SuggestConnector {

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
    BingSuggestClient suggestClient;

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
}
