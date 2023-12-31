package de.l3s.interweb.connector.bing;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

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
import de.l3s.interweb.core.suggest.SuggestConnectorResults;
import de.l3s.interweb.core.suggest.SuggestQuery;

@Disabled
@QuarkusTest
class BingConnectorTest {
    private static final Logger log = Logger.getLogger(BingConnectorTest.class);

    @Inject
    BingConnector connector;

    @Test
    void testSuggest() {
        SuggestQuery query = new SuggestQuery();
        query.setQuery("nikola tesla");
        query.setLanguage("en");

        SuggestConnectorResults results = connector.suggest(query).await().indefinitely();

        assertTrue(results.size() > 10);
        System.out.println("Results for '" + query.getQuery() + "':");
        for (String result : results.getItems()) {
            System.out.println(result);
        }
    }

    @Test
    void search() {
        SearchQuery query = new SearchQuery();
        query.setQuery("hello world");
        query.setPerPage(20);
        query.setContentTypes(ContentType.webpage);
        query.setDateFrom(LocalDate.of(2009, 1, 1));
        query.setDateTo(LocalDate.of(2010, 6, 1));

        SearchConnectorResults queryResult = connector.search(query).await().indefinitely();

        int rank = 0;
        for (SearchItem res : queryResult.getItems()) {
            assertEquals(++rank, res.getRank());
            log.info(res.toString());
        }

        assertEquals(20, queryResult.getItems().size());
        assertTrue(queryResult.getTotalResults() > 100);
    }

    @Test
    void searchImages() {
        SearchQuery query = new SearchQuery();
        query.setQuery("hannover");
        query.setContentTypes(ContentType.image);
        query.setPerPage(30);
        query.setPage(2);

        SearchConnectorResults queryResult = connector.search(query).await().indefinitely();

        int rank = 30;
        for (SearchItem res : queryResult.getItems()) {
            assertEquals(++rank, res.getRank());
            assertEquals(ContentType.image, res.getType());
            log.info(res);
        }

        assertEquals(30, queryResult.getItems().size());
        assertTrue(queryResult.getTotalResults() > 100);
    }

    @Test
    void searchVideos() {
        SearchQuery query = new SearchQuery();
        query.setQuery("hannover");
        query.setContentTypes(ContentType.video);
        query.setPerPage(30);

        SearchConnectorResults queryResult = connector.search(query).await().indefinitely();

        int rank = 0;
        for (SearchItem res : queryResult.getItems()) {
            assertEquals(++rank, res.getRank());
            assertEquals(ContentType.video, res.getType());
            log.info(res.toString());
        }

        assertEquals(30, queryResult.getItems().size());
        assertTrue(queryResult.getTotalResults() > 100);
    }

    @Test
    void bingSearchError() {
        SearchQuery query = new SearchQuery();
        query.setPage(999999999);

        assertThrowsExactly(ConnectorException.class, () -> connector.search(query).await().indefinitely());
    }
}
