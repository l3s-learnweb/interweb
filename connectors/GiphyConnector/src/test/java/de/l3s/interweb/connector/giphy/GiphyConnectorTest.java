package de.l3s.interweb.connector.giphy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.junit.QuarkusTest;
import org.jboss.logging.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.search.ContentType;
import de.l3s.interweb.core.search.SearchConnectorResults;
import de.l3s.interweb.core.search.SearchItem;
import de.l3s.interweb.core.search.SearchQuery;

@Disabled
@QuarkusTest
class GiphyConnectorTest {
    private static final Logger log = Logger.getLogger(GiphyConnectorTest.class);
    private static final GiphyConnector connector = new GiphyConnector();

    @ConfigProperty(name = "connector.giphy.key")
    String apikey;

    @Test
    void search() throws ConnectorException {
        SearchQuery query = new SearchQuery();
        query.setQuery("hello world");
        query.addContentType(ContentType.image);

        SearchConnectorResults queryResult = connector.search(query, new AuthCredentials(apikey));

        for (SearchItem res : queryResult.getItems()) {
            log.info(res);
        }

        assertEquals(10, queryResult.getItems().size());
        assertTrue(queryResult.getTotalResults() > 10);
    }
}
