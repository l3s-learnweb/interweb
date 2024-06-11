package de.l3s.interweb.connector.anthropic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.completion.Choice;
import de.l3s.interweb.core.completion.CompletionQuery;
import de.l3s.interweb.core.completion.CompletionResults;
import de.l3s.interweb.core.completion.Message;

import de.l3s.interweb.connector.anthropic.entity.CompletionBody;

@Disabled
@QuarkusTest
class AnthropicConnectorTest {
    private static final Logger log = Logger.getLogger(AnthropicConnectorTest.class);

    @Inject
    AnthropicConnector connector;

    @Test
    void complete() throws ConnectorException {
        CompletionQuery query = new CompletionQuery();
        query.addMessage("You are Interweb Assistant, a helpful chat bot. Your name is not Claude it is Interweb Assistant.", Message.Role.system);
        query.addMessage("What is your name?.", Message.Role.user);
        query.setMaxTokens(100);
        query.setModel("claude-3-haiku-20240307");


        CompletionResults results = connector.complete(query).await().indefinitely();


        assertEquals(1, results.getChoices().size());
        System.out.println("Results for '" + query.getMessages().get(query.getMessages().size() - 1).getContent() + "':");
        for (Choice result : results.getChoices()) {
            System.out.println(result.getMessage().getContent());
        }
    }

    @Test
    void jsonBody() {
        CompletionQuery query = new CompletionQuery();
        query.addMessage("You are Interweb Assistant, a helpful chat bot.", Message.Role.system);
        query.addMessage("What is your name?.", Message.Role.user);
        query.addMessage("My name is Interweb Assistant.", Message.Role.assistant);
        query.addMessage("Hi Interweb Assistant, I am a user.", Message.Role.user);

        CompletionBody body = new CompletionBody("haiku", query);

        // Print body as json
        ObjectMapper mapper = new ObjectMapper();

        try {
            String jsonString = mapper.writeValueAsString(body);
            System.out.println(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
