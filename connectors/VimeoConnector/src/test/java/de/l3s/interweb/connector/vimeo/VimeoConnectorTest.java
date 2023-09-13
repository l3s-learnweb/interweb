package de.l3s.interweb.connector.vimeo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.search.*;

@Disabled
@QuarkusTest
class VimeoConnectorTest {
    private static final Logger log = Logger.getLogger(VimeoConnectorTest.class);

    @Inject
    VimeoConnector connector;

    @Test
    void search() throws ConnectorException {
        SearchQuery query = new SearchQuery();
        query.setQuery("hello world");
        query.addContentType(ContentType.videos);
        query.setPerPage(20);
        // query.setDateFrom("2009-01-01 00:00:00");
        // query.setDateTo("2009-06-01 00:00:00");
        query.setSort(SearchSort.relevance);

        SearchConnectorResults queryResult = connector.search(query).await().indefinitely();

        for (SearchItem res : queryResult.getItems()) {
            log.info(res);
        }

        assertEquals(20, queryResult.getItems().size());
        assertTrue(queryResult.getTotalResults() > 100);
    }
}
