package de.l3s.interweb.client;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.l3s.interweb.core.completion.Choice;
import de.l3s.interweb.core.completion.CompletionQuery;
import de.l3s.interweb.core.completion.CompletionResults;
import de.l3s.interweb.core.completion.Message;
import de.l3s.interweb.core.search.*;
import de.l3s.interweb.core.suggest.SuggestConnectorResults;
import de.l3s.interweb.core.suggest.SuggestQuery;
import de.l3s.interweb.core.suggest.SuggestResults;

@Disabled
@QuarkusTest
class InterwebTest {

    private final Interweb interweb;

    @Inject
    public InterwebTest(@ConfigProperty(name = "interweb.server") String server, @ConfigProperty(name = "interweb.apikey") String apikey) {
        this.interweb = new Interweb(server, apikey);
    }

    @Test
    void searchTest() throws InterwebException {
        SearchQuery query = new SearchQuery();
        query.setQuery("hannover");
        query.setLanguage("en");
        query.setContentTypes(Set.of(ContentType.videos));
        query.setServices(Set.of("Vimeo", "YouTube"));
        query.setPerPage(32);
        query.setPage(1);
        query.setExtras(Set.of(SearchExtra.duration, SearchExtra.tags));

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

    @Test
    void suggestTest() throws InterwebException {
        SuggestQuery query = new SuggestQuery();
        query.setQuery("hannover");
        query.setLanguage("de");

        SuggestResults response = interweb.suggest(query);
        assertEquals(response.getResults().size(), 2);

        for (SuggestConnectorResults result : response.getResults()) {
            assertFalse(result.getItems().isEmpty());

            System.out.println(result.getService() + ":");
            for (String item : result.getItems()) {
                System.out.println(item);
            }
            System.out.println();
        }
    }

    @Test
    void chatCompletionsTest() throws InterwebException {
        CompletionQuery query = new CompletionQuery();
        query.setModel("gpt-35-turbo");
        query.addMessage("You are Interweb Assistant, a helpful chat bot.", Message.Role.system);
        query.addMessage("What is your name?.", Message.Role.user);

        CompletionResults response = interweb.chatCompletions(query);
        assertFalse(response.getResults().isEmpty());

        for (Choice result : response.getResults()) {
            assertNotNull(result.getMessage());
            System.out.println(result.getMessage().getContent());
        }
    }
}
