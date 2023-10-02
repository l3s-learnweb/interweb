package de.l3s.interweb.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.l3s.interweb.core.search.*;

@Disabled
@QuarkusTest
class InterwebSearchTest {

    private final Interweb interweb;

    @Inject
    public InterwebSearchTest(@ConfigProperty(name = "interweb.server") String server, @ConfigProperty(name = "interweb.apikey") String apikey) {
        this.interweb = new Interweb(server, apikey);
    }

    @Test
    void searchTest() throws InterwebException {
        SearchQuery query = new SearchQuery();
        query.setQuery("hannover");
        query.setLanguage("en");
        query.setContentTypes(ContentType.video);
        query.setServices("Vimeo", "YouTube");
        query.setPerPage(32);
        query.setPage(1);
        query.setExtras(SearchExtra.duration, SearchExtra.tags);

        SearchResults response = interweb.search(query);
        assertEquals(response.getResults().size(), 2);

        for (SearchConnectorResults result : response.getResults()) {
            assertTrue(result.getTotalResults() > 0);

            for (SearchItem item : result.getItems()) {
                System.out.println(item.getTitle() + " [" + item.getDuration() + "]");
                System.out.println(item.getUrl());
            }
        }
    }
}
