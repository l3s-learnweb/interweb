package de.l3s.interweb.client;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.l3s.interweb.core.completion.*;

@Disabled
@QuarkusTest
class InterwebCompletionTest {

    private final Interweb interweb;

    @Inject
    public InterwebCompletionTest(@ConfigProperty(name = "interweb.server") String server, @ConfigProperty(name = "interweb.apikey") String apikey) {
        this.interweb = new Interweb(server, apikey);
    }

    @Test
    void conversationsTest() throws InterwebException {
        List<Conversation> response = interweb.conversations("user1");

        assertFalse(response.isEmpty());
        assertFalse(response.get(0).getTitle().isEmpty());
    }

    @Test
    void chatCompletionsTest() throws InterwebException {
        CompletionQuery query = new CompletionQuery();
        query.setUser("user1");
        query.setGenerateTitle(true);
        query.setModel("gpt-35-turbo");
        query.addMessage("You are Interweb Assistant, a helpful chat bot.", Message.Role.system);
        query.addMessage("What is your name?.", Message.Role.user);

        CompletionResults response = interweb.completion(query);
        assertFalse(response.getResults().isEmpty());

        for (Choice result : response.getResults()) {
            assertNotNull(result.getMessage());
            System.out.println(result.getMessage().getContent());
        }
    }

    @Test
    void conversationTest() throws InterwebException {
        Conversation query = new Conversation();
        query.setUser("user1");
        query.setGenerateTitle(true);
        query.setModel("gpt-35-turbo");
        query.addMessage("You are Interweb Assistant, a helpful chat bot.", Message.Role.system);
        query.addMessage("What is your name?.", Message.Role.user);

        assertNull(query.getTitle());
        assertNull(query.getEstimatedCost());
        assertEquals(2, query.getMessages().size());

        interweb.completion(query);

        assertNotNull(query.getTitle());
        assertNotNull(query.getEstimatedCost());
        assertEquals(3, query.getMessages().size());

        query.addMessage("That's time now?", Message.Role.user);
        interweb.completion(query);

        assertEquals(5, query.getMessages().size());

        for (Message result : query.getMessages()) {
            System.out.println(result.getContent());
        }
    }
}
