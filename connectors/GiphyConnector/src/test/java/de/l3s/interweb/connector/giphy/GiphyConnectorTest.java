package de.l3s.interweb.connector.giphy;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.search.ContentType;
import de.l3s.interweb.core.search.SearchConnectorResults;
import de.l3s.interweb.core.search.SearchItem;
import de.l3s.interweb.core.search.SearchQuery;

@Disabled
@QuarkusTest
class GiphyConnectorTest {
    private static final Logger log = Logger.getLogger(GiphyConnectorTest.class);

    @Inject
    GiphyConnector connector;

    @Test
    void search() throws ConnectorException {
        SearchQuery query = new SearchQuery();
        query.setQuery("hello world");
        query.setContentTypes(ContentType.image);

        SearchConnectorResults queryResult = connector.search(query).await().indefinitely();

        for (SearchItem res : queryResult.getItems()) {
            log.info(res);
        }

        assertTrue(queryResult.getItems().size() > 30);
        assertTrue(queryResult.getTotalResults() > 50);
    }
}
