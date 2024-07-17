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
    void completionsTest() throws InterwebException {
        CompletionQuery query = new CompletionQuery();
        query.setUser("user1");
        query.setGenerateTitle(true);
        query.setModel("gpt-35-turbo");
        query.addMessage("You are Interweb Assistant, a helpful chat bot.", Message.Role.system);
        query.addMessage("What is your name?", Message.Role.user);

        CompletionResults response = interweb.completions(query);
        assertFalse(response.getResults().isEmpty());

        for (Choice result : response.getResults()) {
            assertNotNull(result.getMessage());
            System.out.println(result.getMessage().getContent());
        }
    }

    @Test
    void chatAllTest() throws InterwebException {
        List<Conversation> response = interweb.chatAll("user1");

        assertFalse(response.isEmpty());
        assertFalse(response.get(0).getTitle().isEmpty());
    }

    @Test
    void chatByIdTest() throws InterwebException {
        Conversation conversation = interweb.chatById("ef235b94-09a0-4b0e-b1cb-c06b6a3adf6c");
        assertNotNull(conversation.getTitle());
        assertNotNull(conversation.getModel());

        for (Message message : conversation.getMessages()) {
            assertNotNull(message.getContent());
            System.out.println(message.getContent());
        }
    }

    @Test
    void chatCompleteTest() throws InterwebException {
        Conversation conversation = new Conversation();
        conversation.setUser("user1");
        conversation.setGenerateTitle(true);
        conversation.setModel("gpt-35-turbo");
        conversation.addMessage("You are Interweb Assistant, a helpful chat bot.", Message.Role.system);
        conversation.addMessage("What is your name?", Message.Role.user);

        assertNull(conversation.getTitle());
        assertNull(conversation.getEstimatedCost());
        assertEquals(2, conversation.getMessages().size());

        interweb.chatComplete(conversation);

        assertNotNull(conversation.getTitle());
        assertNotNull(conversation.getEstimatedCost());
        assertEquals(3, conversation.getMessages().size());

        conversation.addMessage("That's time now?", Message.Role.user);
        interweb.chatComplete(conversation);

        assertEquals(5, conversation.getMessages().size());

        for (Message result : conversation.getMessages()) {
            System.out.println(result.getContent());
        }
    }
}
