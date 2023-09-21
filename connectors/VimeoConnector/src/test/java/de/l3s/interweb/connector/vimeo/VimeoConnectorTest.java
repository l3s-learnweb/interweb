package de.l3s.interweb.connector.vimeo;

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
class VimeoConnectorTest {
    private static final Logger log = Logger.getLogger(VimeoConnectorTest.class);

    @Inject
    VimeoConnector connector;

    @Test
    void search() throws ConnectorException {
        SearchQuery query = new SearchQuery();
        query.setQuery("hello world");
        query.addContentType(ContentType.video);
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

    @Test
    void findIds() {
        assertEquals("524933864", connector.findId("https://vimeo.com/524933864"));
        assertEquals("524933864", connector.findId("https://player.vimeo.com/video/524933864?h=1ac4fd9fb4"));
    }

    @Test
    void describe() throws ConnectorException {
        DescribeQuery query = new DescribeQuery();
        query.setId("524933864");

        DescribeResults queryResult = connector.describe(query).await().indefinitely();

        assertEquals("524933864", queryResult.getEntity().getId());
        assertEquals("https://vimeo.com/524933864", queryResult.getEntity().getUrl());
        assertEquals(ContentType.video, queryResult.getEntity().getType());
        assertEquals("Vimeo | Video Power", queryResult.getEntity().getTitle());
        assertEquals("We build the most powerful video tools in the world to power everyone to change it.", queryResult.getEntity().getDescription());
        assertEquals(95, queryResult.getEntity().getDuration());
        assertEquals(Instant.ofEpochMilli(1615989407000L), queryResult.getEntity().getDate());
        assertEquals(1920, queryResult.getEntity().getWidth());
        assertEquals(1080, queryResult.getEntity().getHeight());
        assertEquals("https://player.vimeo.com/video/524933864?h=1ac4fd9fb4", queryResult.getEntity().getEmbedUrl());
        assertEquals("Vimeo Staff", queryResult.getEntity().getAuthor());
        assertEquals("https://vimeo.com/staff", queryResult.getEntity().getAuthorUrl());
        assertEquals(0, queryResult.getEntity().getCommentsCount());
        assertEquals(new Thumbnail("https://i.vimeocdn.com/video/1087013990-9d803c0d769b72de8a4ba06555519017e3f8604630e48ae27711c7c8401be4a3-d_295x166?r=pad", 295, 166), queryResult.getEntity().getThumbnailSmall());
        assertEquals(new Thumbnail("https://i.vimeocdn.com/video/1087013990-9d803c0d769b72de8a4ba06555519017e3f8604630e48ae27711c7c8401be4a3-d_640x360?r=pad", 640, 360), queryResult.getEntity().getThumbnailMedium());
        assertEquals(new Thumbnail("https://i.vimeocdn.com/video/1087013990-9d803c0d769b72de8a4ba06555519017e3f8604630e48ae27711c7c8401be4a3-d_1280x720?r=pad", 1280, 720), queryResult.getEntity().getThumbnailLarge());
        assertEquals(new Thumbnail("https://i.vimeocdn.com/video/1087013990-9d803c0d769b72de8a4ba06555519017e3f8604630e48ae27711c7c8401be4a3-d_1920x1080?r=pad", 1920, 1080), queryResult.getEntity().getThumbnailOriginal());
    }

    @Test
    void describeFail() throws ConnectorException {
        DescribeQuery query = new DescribeQuery();
        query.setId("1");

        assertThrows(ConnectorException.class, () -> connector.describe(query).await().indefinitely());
    }
}
