package de.l3s.interweb.connector.youtube;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.describe.DescribeQuery;
import de.l3s.interweb.core.describe.DescribeResults;
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
        query.addContentType(ContentType.video);
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
        query.addContentType(ContentType.video);
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
        query.addContentType(ContentType.video);

        for (int i = 1; i < 4; ++i) {
            query.setPage(i);
            SearchConnectorResults page = connector.search(query).await().indefinitely();

            assertEquals(10, page.getItems().size());
            assertTrue(page.getTotalResults() > 100);
        }
    }

    @Test
    void findIds() {
        assertEquals("0zM3nApSvMg", connector.findId("http://www.youtube.com/watch?v=0zM3nApSvMg&feature=feedrec_grec_index"));
        assertEquals("QdK8U-VIH_o", connector.findId("http://www.youtube.com/user/IngridMichaelsonVEVO#p/a/u/1/QdK8U-VIH_o"));
        assertEquals("0zM3nApSvMg", connector.findId("https://www.youtube.com/v/0zM3nApSvMg?fs=1&amp;hl=en_US&amp;rel=0"));
        assertEquals("0zM3nApSvMg", connector.findId("http://www.youtube.com/watch?v=0zM3nApSvMg#t=0m10s"));
        assertEquals("0zM3nApSvMg", connector.findId("https://www.youtube.com/embed/0zM3nApSvMg?rel=0"));
        assertEquals("0zM3nApSvMg", connector.findId("http://www.youtube.com/watch?v=0zM3nApSvMg"));
        assertEquals("0zM3nApSvMg", connector.findId("http://youtu.be/0zM3nApSvMg"));
        assertEquals("up_lNV-yoK4", connector.findId("//www.youtube-nocookie.com/embed/up_lNV-yoK4?rel=0"));
        assertEquals("dQw4w9WgXcQ", connector.findId("http://youtube.com/?vi=dQw4w9WgXcQ&feature=youtube_gdata_player"));
        assertEquals("dQw4w9WgXcQ", connector.findId("https://youtube.com/shorts/dQw4w9WgXcQ?feature=share"));
        assertEquals("0zM3nApSvMg", connector.findId("http://www.youtube.com/v/0zM3nApSvMg?fs=1&amp;hl=en_US&amp;rel=0"));
    }

    @Test
    void describe() throws ConnectorException {
        DescribeQuery query = new DescribeQuery();
        query.setId("MVu8QbxafJE");

        DescribeResults queryResult = connector.describe(query).await().indefinitely();

        assertEquals("MVu8QbxafJE", queryResult.getEntity().getId());
        assertEquals("https://www.youtube.com/watch?v=MVu8QbxafJE", queryResult.getEntity().getUrl());
        assertEquals(ContentType.video, queryResult.getEntity().getType());
        assertEquals("Putin's war on Ukraine, explained", queryResult.getEntity().getTitle());
        assertTrue(queryResult.getEntity().getDescription().startsWith("Ukraine is under attack. Follow Vox for the latest"));
        assertEquals(Instant.ofEpochMilli(1646257974000L), queryResult.getEntity().getDate());
        assertEquals(529, queryResult.getEntity().getDuration());
        assertEquals(1280, queryResult.getEntity().getWidth());
        assertEquals(720, queryResult.getEntity().getHeight());
        assertEquals("https://www.youtube-nocookie.com/embed/MVu8QbxafJE", queryResult.getEntity().getEmbedUrl());
        assertEquals("Vox", queryResult.getEntity().getAuthor());
        assertEquals("https://www.youtube.com/channel/UCLXo7UDZvByw2ixzpQCufnA", queryResult.getEntity().getAuthorUrl());
        assertTrue(queryResult.getEntity().getViewsCount() > 2000);
        assertEquals(new Thumbnail("https://i.ytimg.com/vi/MVu8QbxafJE/mqdefault.jpg", 320, 180), queryResult.getEntity().getThumbnailSmall());
        assertEquals(new Thumbnail("https://i.ytimg.com/vi/MVu8QbxafJE/hqdefault.jpg", 480, 360), queryResult.getEntity().getThumbnailMedium());
        assertEquals(new Thumbnail("https://i.ytimg.com/vi/MVu8QbxafJE/maxresdefault.jpg", 1280, 720), queryResult.getEntity().getThumbnailLarge());
    }

    @Test
    void describeFail() throws ConnectorException {
        DescribeQuery query = new DescribeQuery();
        query.setId("dsadasdasda");

        assertThrows(ConnectorException.class, () -> connector.describe(query).await().indefinitely());
    }
}
