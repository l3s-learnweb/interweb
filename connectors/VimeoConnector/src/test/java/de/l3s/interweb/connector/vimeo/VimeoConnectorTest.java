package de.l3s.interweb.connector.vimeo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;

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
class VimeoConnectorTest {
    private static final Logger log = Logger.getLogger(VimeoConnectorTest.class);
    private static final VimeoConnector connector = new VimeoConnector();

    @ConfigProperty(name = "connector.vimeo.key")
    String apikey;

    @Test
    void search() throws ConnectorException {
        SearchQuery query = new SearchQuery();
        query.setQuery("hello world");
        query.addContentType(ContentType.video);
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

    @Test
    void getTest() throws ConnectorException {
        SearchQuery query = new SearchQuery();
        query.setQuery("mass disaster 1941");
        query.addContentType(ContentType.video);
        query.setLanguage("it");
        query.setExtras(Collections.singleton(SearchExtra.duration));
        query.setPerPage(32);
        query.setPage(2);
        SearchConnectorResults queryResult = connector.search(query, null);

        for (SearchItem res : queryResult.getItems()) {
            log.info(res);
        }

        assertEquals(0, queryResult.getItems().size());
    }

    @Test
    void parseDate() throws ConnectorException {
        ZonedDateTime localDateTime = VimeoConnector.parseDate("2020-04-21T09:44:08+00:00");
        assertEquals(ZonedDateTime.of(2020, 4, 21, 9, 44, 8, 0, ZoneId.of("+0")), localDateTime);
    }
}
