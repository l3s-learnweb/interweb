package de.l3s.interweb.connector.ipernity;

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
class IpernityConnectorTest {
    private static final Logger log = Logger.getLogger(IpernityConnectorTest.class);
    private static final IpernityConnector connector = new IpernityConnector();

    @ConfigProperty(name = "connectors.ipernity.key")
    String apikey;

    @Test
    void search() throws ConnectorException {
        SearchQuery query = new SearchQuery();
        query.setQuery("tree");
        query.addContentType(ContentType.image);
        query.setPerPage(20);
        // query.setDateFrom("2009-01-01 00:00:00");
        // query.setDateTill("2009-06-01 00:00:00");
        query.setRanking(SearchRanking.relevance);

        SearchConnectorResults queryResult = connector.search(query, new AuthCredentials(apikey));

        for (SearchItem res : queryResult.getItems()) {
            log.info(res);
        }

        assertEquals(20, queryResult.getItems().size());
        assertTrue(queryResult.getTotalResults() > 100);
    }
}
