package de.l3s.interweb.client;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.l3s.interweb.core.suggest.SuggestConnectorResults;
import de.l3s.interweb.core.suggest.SuggestQuery;
import de.l3s.interweb.core.suggest.SuggestResults;

@Disabled
@QuarkusTest
class InterwebSuggestTest {

    private final Interweb interweb;

    @Inject
    public InterwebSuggestTest(@ConfigProperty(name = "interweb.server") String server, @ConfigProperty(name = "interweb.apikey") String apikey) {
        this.interweb = new Interweb(server, apikey);
    }

    @Test
    void suggestTest() throws InterwebException {
        SuggestQuery query = new SuggestQuery();
        query.setQuery("hannover");
        query.setLanguage("de");

        SuggestResults response = interweb.suggest(query);
        assertEquals(2, response.getResults().size());

        for (SuggestConnectorResults result : response.getResults()) {
            assertFalse(result.getItems().isEmpty());

            System.out.println(result.getService() + ":");
            for (String item : result.getItems()) {
                System.out.println(item);
            }
            System.out.println();
        }
    }
}
