package de.l3s.interweb.connector.youtube;

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
class YouTubeConnectorTest {
    private static final Logger log = Logger.getLogger(YouTubeConnectorTest.class);

    @Inject
    YouTubeConnector connector;

    @Test
    void search() throws ConnectorException {
        SearchQuery query = new SearchQuery();
        query.setQuery("hannover");
        query.addContentType(ContentType.videos);
        query.setPerPage(10);
        // query.setDateFrom("2009-01-01 00:00:00");
        // query.setDateTo("2009-06-01 00:00:00");
        query.setSort(SearchSort.relevance);
        // query.addExtra(SearchExtra.statistics);
        query.addExtra(SearchExtra.duration);
        query.addExtra(SearchExtra.tags);

        SearchConnectorResults page = connector.search(query).await().indefinitely();
        assertEquals(10, page.getItems().size());
        assertTrue(page.getTotalResults() > 100);

        for (SearchItem result : page.getItems()) {
            assertTrue(result.getDuration() > 0);
            log.infov("{0}: {1} {2}", result.getRank(), result.getTitle(), result.getUrl());
        }
    }

    @Test
    void searchChannel() throws ConnectorException {
        SearchQuery query = new SearchQuery();
        query.setQuery("user::ukrainernet kharkiv");
        query.addContentType(ContentType.videos);
        query.setPerPage(10);

        SearchConnectorResults page = connector.search(query).await().indefinitely();
        assertEquals(10, page.getItems().size());

        for (SearchItem result : page.getItems()) {
            assertEquals("Ukraїner", result.getAuthor());
            log.infov("{0}: {1} {2}", result.getRank(), result.getTitle(), result.getUrl());
        }
    }

    @Test
    void searchPaging() throws ConnectorException {
        SearchQuery query = new SearchQuery();
        query.setQuery("hannover");
        query.setPerPage(10);
        query.addContentType(ContentType.videos);

        for (int i = 1; i < 4; ++i) {
            query.setPage(i);
            SearchConnectorResults page = connector.search(query).await().indefinitely();

            assertEquals(10, page.getItems().size());
            assertTrue(page.getTotalResults() > 100);
        }
    }
}
