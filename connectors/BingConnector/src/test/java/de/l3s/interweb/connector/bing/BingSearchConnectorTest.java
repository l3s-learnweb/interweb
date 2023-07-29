package de.l3s.interweb.connector.bing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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
class BingSearchConnectorTest {
    private static final Logger log = Logger.getLogger(BingSearchConnectorTest.class);
    private static final BingSearchConnector connector = new BingSearchConnector();

    @ConfigProperty(name = "connectors.bing.key")
    String apikey;

    @Test
    void search() throws ConnectorException {
        SearchQuery query = new SearchQuery();
        query.setQuery("hello world");
        query.setPerPage(20);
        query.addContentType(ContentType.text);
        query.setDateFrom(LocalDate.of(2009, 1, 1));
        query.setDateTill(LocalDate.of(2010, 6, 1));

        SearchConnectorResults queryResult = connector.search(query, new AuthCredentials(apikey));

        for (SearchItem res : queryResult.getItems()) {
            log.info(res.toString());
        }

        assertEquals(20, queryResult.getItems().size());
        assertTrue(queryResult.getTotalResults() > 100);
    }

    @Test
    void getImages() throws ConnectorException {
        SearchQuery query = new SearchQuery();
        query.setQuery("hannover");
        query.addContentType(ContentType.image);
        query.setPerPage(30);
        query.setPage(2);

        SearchConnectorResults queryResult = connector.search(query, new AuthCredentials(apikey));

        for (SearchItem res : queryResult.getItems()) {
            log.info(res);
        }

        assertEquals(30, queryResult.getItems().size());
        assertTrue(queryResult.getTotalResults() > 100);
    }

    @Test
    void getVideos() throws ConnectorException {
        SearchQuery query = new SearchQuery();
        query.setQuery("hannover");
        query.addContentType(ContentType.video);
        query.setPerPage(30);
        query.setPage(2);

        SearchConnectorResults queryResult = connector.search(query, new AuthCredentials(apikey));

        for (SearchItem res : queryResult.getItems()) {
            log.info(res.toString());
        }

        assertEquals(30, queryResult.getItems().size());
        assertTrue(queryResult.getTotalResults() > 100);
    }

    @Test
    void parseDate() throws ConnectorException {
        ZonedDateTime date0 = BingSearchConnector.parseDate("2020-01-28T17:18:45");
        ZonedDateTime date1 = BingSearchConnector.parseDate("2020-01-28T17:18:45.0000000");
        ZonedDateTime date2 = BingSearchConnector.parseDate("2019-11-12T12:37:00.0000000Z");
        ZonedDateTime date3 = BingSearchConnector.parseDate("2020-11-28T23:17:00.0000000Z");

        assertEquals(ZonedDateTime.of(2020, 1, 28, 17, 18, 45, 0, ZoneId.systemDefault()), date0);
        assertEquals(ZonedDateTime.of(2020, 1, 28, 17, 18, 45, 0, ZoneId.systemDefault()), date1);
        assertEquals(ZonedDateTime.of(2019, 11, 12, 12, 37, 0, 0, ZoneId.systemDefault()), date2);
        assertEquals(ZonedDateTime.of(2020, 11, 28, 23, 17, 0, 0, ZoneId.systemDefault()), date3);
    }
}
