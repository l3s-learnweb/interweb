package de.l3s.interweb.connector.google;

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
class GoogleConnectorTest {
    private static final Logger log = Logger.getLogger(GoogleConnectorTest.class);

    @Inject
    GoogleConnector connector;

    @Test
    void suggest() throws ConnectorException {
        SuggestQuery query = new SuggestQuery();
        query.setQuery("hannover");
        query.setLanguage("en");

        SuggestConnectorResults results = connector.suggest(query).await().indefinitely();

        assertEquals(10, results.size());
        log.infov("Results for \"{0}\":", query.getQuery());
        for (String result : results.getItems()) {
            log.infov("  {0}", result);
        }
    }

    @Test
    void search() {
        SearchQuery query = new SearchQuery();
        query.setQuery("hello world");
        query.setPerPage(10);
        query.setContentTypes(ContentType.webpage);
        query.setDateFrom(LocalDate.now().minusDays(3));

        SearchConnectorResults queryResult = connector.search(query).await().indefinitely();

        int rank = 0;
        assertTrue(queryResult.getItems().size() >= 10);

        log.infov("Results for \"{0}\":", query.getQuery());
        for (SearchItem res : queryResult.getItems()) {
            assertEquals(ContentType.webpage, res.getType());
            assertEquals(++rank, res.getRank());
            log.info(res);
        }
    }

    @Test
    void searchImages() {
        SearchQuery query = new SearchQuery();
        query.setQuery("hannover");
        query.setContentTypes(ContentType.image);
        query.setPerPage(10);
        query.setPage(2);

        SearchConnectorResults queryResult = connector.search(query).await().indefinitely();

        int rank = 10;
        assertTrue(queryResult.getItems().size() >= 10);

        log.infov("Results for \"{0}\":", query.getQuery());
        for (SearchItem res : queryResult.getItems()) {
            assertEquals(++rank, res.getRank());
            assertEquals(ContentType.image, res.getType());
            log.info(res);
        }
    }

    @Test
    void searchVideos() {
        SearchQuery query = new SearchQuery();
        query.setQuery("hannover");
        query.setContentTypes(ContentType.video);

        SearchConnectorResults queryResult = connector.search(query).await().indefinitely();

        int rank = 0;
        assertTrue(queryResult.getItems().size() >= 10);

        log.infov("Results for \"{0}\":", query.getQuery());
        for (SearchItem res : queryResult.getItems()) {
            assertEquals(++rank, res.getRank());
            assertEquals(ContentType.video, res.getType());
            log.info(res);
        }
    }

    @Test
    void searchError() {
        SearchQuery query = new SearchQuery();
        query.setPage(999999999);

        assertThrowsExactly(ConnectorException.class, () -> connector.search(query).await().indefinitely());
    }
}
