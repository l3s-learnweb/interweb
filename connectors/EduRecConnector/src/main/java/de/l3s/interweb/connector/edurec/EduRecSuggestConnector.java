package de.l3s.interweb.connector.edurec;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import jakarta.enterprise.context.Dependent;
import jakarta.json.*;

import org.jboss.logging.Logger;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.suggest.SuggestConnector;
import de.l3s.interweb.core.suggest.SuggestConnectorResults;
import de.l3s.interweb.core.suggest.SuggestQuery;

@Dependent
public class EduRecSuggestConnector implements SuggestConnector {
    private static final Logger log = Logger.getLogger(EduRecSuggestConnector.class);

    @Override
    public String getName() {
        return "EduRec";
    }

    @Override
    public String getBaseUrl() {
        return "https://edurec.kevinhaller.dev/";
    }

    @Override
    public SuggestConnectorResults suggest(SuggestQuery query) throws ConnectorException {
        final String requestUrl = "https://demo3.kbs.uni-hannover.de/recommend/10/items/for/dbpedia100k/with/transE";

        JsonObject nodeObject = Json.createObjectBuilder()
                .add("record", Json.createObjectBuilder()
                        .add("nodes", Json.createArrayBuilder()
                                .add(Json.createObjectBuilder()
                                        .add("query", query.getQuery()))))
                .build();

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
            requestBuilder.uri(URI.create(requestUrl));
            requestBuilder.header("Content-Type", "application/json");

            try (Writer stringWriter = new StringWriter(); JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
                jsonWriter.write(nodeObject);
                requestBuilder.POST(HttpRequest.BodyPublishers.ofString(stringWriter.toString()));
            }

            HttpResponse<InputStream> response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());

            try (JsonReader jsonReader = Json.createReader(response.body())) {
                JsonObject jsonRoot = jsonReader.readObject();
                JsonArray suggestions = jsonRoot.getJsonArray("list");

                SuggestConnectorResults results = new SuggestConnectorResults();
                for (JsonValue suggestion : suggestions) {
                    final String value = suggestion.asJsonObject().getString("iri");
                    results.addItem(value.substring(value.lastIndexOf('/') + 1).replace('_', ' '));
                }

                return results;
            }
        } catch (IOException | InterruptedException e) {
            throw new ConnectorException("Unable to retrieve suggestions", e);
        }
    }
}
