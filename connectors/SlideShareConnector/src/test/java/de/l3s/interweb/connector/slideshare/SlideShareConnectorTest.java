package de.l3s.interweb.connector.slideshare;

import static org.junit.jupiter.api.Assertions.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.InterWebException;
import de.l3s.interweb.core.search.SearchResults;
import de.l3s.interweb.core.search.SearchProvider;
import de.l3s.interweb.core.query.ContentType;
import de.l3s.interweb.core.query.Query;
import de.l3s.interweb.core.query.QueryFactory;
import de.l3s.interweb.core.search.SearchItem;
import de.l3s.interweb.core.query.SearchRanking;
import de.l3s.interweb.core.query.SearchScope;

@Disabled
class SlideShareConnectorTest {
    private static final Logger log = LogManager.getLogger(SlideShareConnectorTest.class);

    private static final String TEST_KEY = "***REMOVED***";
    private static final String TEST_SECRET = "***REMOVED***";

    private static SearchProvider connector;

    @BeforeAll
    public static void initialize() {
        AuthCredentials consumerAuthCredentials = new AuthCredentials(TEST_KEY, TEST_SECRET);
        connector = new SlideShareConnector(consumerAuthCredentials);
    }

    @Test
    void get() throws InterWebException {
        Query query = QueryFactory.createQuery("hello world");
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

        SearchResults queryResult = connector.get(query, null);

        for (SearchItem res : queryResult.getItems()) {
            log.info(res);
        }

        assertEquals(18, queryResult.getItems().size());
        assertTrue(queryResult.getTotalResults() > 100);

        String embedded = connector.getEmbedded(null, "https://www.slideshare.net/pacific2000/flowers-presentation-715934", 240, 240);
        System.out.println(embedded);
    }

    @Test
    void getEmbedded() throws InterWebException {
        String embedded = connector.getEmbedded(null, "https://www.slideshare.net/pacific2000/flowers-presentation-715934", 240, 240);
        System.out.println(embedded);
    }

    @Test
    void parseDate() throws InterWebException {
        ZonedDateTime localDateTime = SlideShareConnector.parseDate("2015-09-23 16:15:57 UTC");
        assertEquals(ZonedDateTime.of(2015, 9, 23, 16, 15, 57, 0, ZoneId.of("Etc/UTC")), localDateTime);
    }
}
