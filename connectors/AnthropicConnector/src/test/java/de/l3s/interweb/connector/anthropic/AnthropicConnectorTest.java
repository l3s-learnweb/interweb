package de.l3s.interweb.connector.anthropic;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.l3s.interweb.connector.anthropic.entity.CompletionBody;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.chat.Choice;
import de.l3s.interweb.core.chat.CompletionsQuery;
import de.l3s.interweb.core.chat.CompletionsResults;
import de.l3s.interweb.core.chat.Role;

@Disabled
@QuarkusTest
class AnthropicConnectorTest {
    private static final Logger log = Logger.getLogger(AnthropicConnectorTest.class);

    @Inject
    AnthropicConnector connector;

    @Test
    void validate() throws ConnectorException {
        assertTrue(connector.validate());
    }

    @Test
    void completions() throws ConnectorException {
        CompletionsQuery query = new CompletionsQuery();
        query.addMessage("You are Interweb Assistant, a helpful chat bot. Your name is not Claude it is Interweb Assistant.", Role.system);
        query.addMessage("What is your name?", Role.user);
        query.setMaxTokens(100);
        query.setModel("claude-3-haiku-20240307");

        CompletionsResults results = connector.completions(query).await().indefinitely();

        assertEquals(1, results.getChoices().size());
        log.infov("user: {0}", query.getMessages().getLast().getContent());
        for (Choice result : results.getChoices()) {
            log.infov("assistant: {0}", result.getMessage().getContent());
        }
    }

    @Test
    void jsonBody() throws JsonProcessingException {
        CompletionsQuery query = new CompletionsQuery();
        query.setModel("claude-3-haiku-20240307");
        query.addMessage("You are Interweb Assistant, a helpful chat bot.", Role.system);
        query.addMessage("What is your name?", Role.user);
        query.addMessage("My name is Interweb Assistant.", Role.assistant);
        query.addMessage("Hi Interweb Assistant, I am a user.", Role.user);

        CompletionBody body = new CompletionBody(query);

        // Print body as json
        ObjectMapper mapper = new ObjectMapper();

        String jsonString = mapper.writeValueAsString(body);
        assertEquals("{\"messages\":[{\"role\":\"user\",\"content\":\"What is your name?.\"},{\"role\":\"assistant\",\"content\":\"My name is Interweb Assistant.\"},{\"role\":\"user\",\"content\":\"Hi Interweb Assistant, I am a user.\"}],\"model\":\"claude-3-haiku-20240307\",\"system\":\"You are Interweb Assistant, a helpful chat bot.\",\"max_tokens\":800}", jsonString);
    }
}
