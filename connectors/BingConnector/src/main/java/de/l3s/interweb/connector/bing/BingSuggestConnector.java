package de.l3s.interweb.connector.bing;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import jakarta.enterprise.context.Dependent;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.ws.rs.core.UriBuilder;

import de.l3s.interweb.connector.bing.client.BingUtils;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.suggest.SuggestConnector;
import de.l3s.interweb.core.suggest.SuggestConnectorResults;
import de.l3s.interweb.core.suggest.SuggestQuery;

@Dependent
public class BingSuggestConnector implements SuggestConnector {

    @Override
    public String getName() {
        return "Bing";
    }

    @Override
    public String getBaseUrl() {
        return "https://bing.com/";
    }

    @Override
    public SuggestConnectorResults suggest(SuggestQuery query) throws ConnectorException {
        UriBuilder requestUri = UriBuilder.fromUri("https://api.bing.com/osjson.aspx");
        requestUri.queryParam("query", query.getQuery());
        if (query.getLanguage() != null) {
            requestUri.queryParam("market", BingUtils.getMarket(query.getLanguage()));
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
            requestBuilder.uri(requestUri.build());

            HttpResponse<InputStream> response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());

            try (JsonReader jsonReader = Json.createReader(response.body())) {
                JsonArray suggestions = jsonReader.readArray().getJsonArray(1);
                SuggestConnectorResults results = new SuggestConnectorResults();
                for (JsonString suggestion : suggestions.getValuesAs(JsonString.class)) {
                    results.addItem(suggestion.getString());
                }

                return results;
            }
        } catch (IOException | InterruptedException e) {
            throw new ConnectorException("Unable to retrieve suggestions", e);
        }
    }
}
