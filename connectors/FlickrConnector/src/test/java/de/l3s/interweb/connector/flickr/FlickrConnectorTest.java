package de.l3s.interweb.connector.flickr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.junit.QuarkusTest;
import org.jboss.logging.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.search.*;

@Disabled
@QuarkusTest
class FlickrConnectorTest {
    private static final Logger log = Logger.getLogger(FlickrConnectorTest.class);
    private static final FlickrConnector connector = new FlickrConnector();

    @ConfigProperty(name = "connectors.flickr.key")
    String apikey;

    @ConfigProperty(name = "connectors.flickr.secret")
    String sharedSecret;

    @Test
    void search() throws ConnectorException {
        SearchQuery query = new SearchQuery();
        query.setQuery("hello world");
        query.addContentType(ContentType.image);
        // query.addContentType(ContentType.video);
        query.setPerPage(5);
        // query.setDateFrom("2009-01-01 00:00:00");
        // query.setDateTill("2009-06-01 00:00:00");
        query.setRanking(SearchRanking.relevance);

        SearchConnectorResults queryResult = connector.search(query, new AuthCredentials(apikey, sharedSecret));

        for (SearchItem res : queryResult.getItems()) {
            log.info(res);
        }

        assertEquals(5, queryResult.getItems().size());
        assertTrue(queryResult.getTotalResults() > 100);
    }
}
