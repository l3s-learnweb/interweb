package de.l3s.interweb.connector.slideshare;

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
class SlideShareConnectorTest {
    private static final Logger log = Logger.getLogger(SlideShareConnectorTest.class);
    private static final SlideShareConnector connector = new SlideShareConnector();

    @ConfigProperty(name = "connector.slideshare.key")
    String apikey;

    @ConfigProperty(name = "connector.slideshare.secret")
    String secret;

    @Test
    void search() throws ConnectorException {
        SearchQuery query = new SearchQuery();
        query.setQuery("hello world");
        query.addContentType(ContentType.video);
        query.addContentType(ContentType.image);
        query.addContentType(ContentType.text);
        query.addContentType(ContentType.presentation);
        query.addContentType(ContentType.audio);
        query.setSearchScope(SearchScope.text);
        query.setPerPage(5);
        // query.setDateFrom("2009-01-01 00:00:00");
        // query.setDateTill("2009-06-01 00:00:00");
        query.setRanking(SearchRanking.relevance);

        SearchConnectorResults queryResult = connector.search(query, new AuthCredentials(apikey, secret));

        for (SearchItem res : queryResult.getItems()) {
            log.info(res);
        }

        assertEquals(18, queryResult.getItems().size());
        assertTrue(queryResult.getTotalResults() > 100);
    }

    @Test
    void parseDate() throws ConnectorException {
        ZonedDateTime localDateTime = SlideShareConnector.parseDate("2015-09-23 16:15:57 UTC");
        assertEquals(ZonedDateTime.of(2015, 9, 23, 16, 15, 57, 0, ZoneId.of("Etc/UTC")), localDateTime);
    }
}
