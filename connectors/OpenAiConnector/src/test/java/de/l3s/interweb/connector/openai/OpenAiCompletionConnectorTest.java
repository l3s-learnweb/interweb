package de.l3s.interweb.connector.openai;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.junit.QuarkusTest;
import org.jboss.logging.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.completion.*;

@Disabled
@QuarkusTest
class OpenAiCompletionConnectorTest {
    private static final Logger log = Logger.getLogger(OpenAiCompletionConnectorTest.class);
    private static final CompletionConnector connector = new OpenAiCompletionConnector();

    @ConfigProperty(name = "connectors.openai.key")
    String host;

    @ConfigProperty(name = "connectors.openai.secret")
    String apikey;

    @Test
    void query() throws ConnectorException {
        CompletionQuery query = new CompletionQuery();
        query.addMessage("You are Learnweb Assistant, a helpful chat bot.", Message.Role.system);
        query.addMessage("What is your name?.", Message.Role.user);

        CompletionResults results = connector.complete(query, new AuthCredentials(host, apikey));

        assertEquals(1, results.getChoices().size());
        System.out.println("Results for '" + query.getMessages().get(query.getMessages().size() - 1).getContent() + "':");
        for (Choice result : results.getChoices()) {
            System.out.println(result.getMessage().getContent());
        }
    }
}
