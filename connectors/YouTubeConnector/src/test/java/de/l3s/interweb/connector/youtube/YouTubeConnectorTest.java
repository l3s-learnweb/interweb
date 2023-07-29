package de.l3s.interweb.connector.youtube;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZoneId;
import java.time.ZonedDateTime;

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
class YouTubeConnectorTest {
    private static final Logger log = Logger.getLogger(YouTubeConnectorTest.class);
    private static final YouTubeConnector connector = new YouTubeConnector();

    @ConfigProperty(name = "connectors.youtube.key")
    String apikey;

    @Test
    void search() throws ConnectorException {
        SearchQuery query = new SearchQuery();
        query.setQuery("spacex");
        query.addContentType(ContentType.video);
        query.setPerPage(10);
        // query.setDateFrom("2009-01-01 00:00:00");
        // query.setDateTill("2009-06-01 00:00:00");
        query.setRanking(SearchRanking.relevance);
        // query.addSearchExtra(SearchExtra.statistics);
        query.addSearchExtra(SearchExtra.duration);
        query.addSearchExtra(SearchExtra.tags);

        for (int i = 1; i < 4; ++i) {
            query.setPage(i);
            SearchConnectorResults page = connector.search(query, new AuthCredentials(apikey));

            assertEquals(10, page.getItems().size());
            assertTrue(page.getTotalResults() > 100);

            for (SearchItem result : page.getItems()) {
                log.infov("{0}: {1} {2}", result.getRank(), result.getTitle(), result.getUrl());
            }
        }
    }

    @Test
    void parseDate() throws ConnectorException {
        ZonedDateTime localDateTime = YouTubeConnector.parseDate("2020-10-04T20:23:33Z");
        assertEquals(ZonedDateTime.of(2020, 10, 4, 20, 23, 33, 0, ZoneId.of("+0")), localDateTime);
    }
}
