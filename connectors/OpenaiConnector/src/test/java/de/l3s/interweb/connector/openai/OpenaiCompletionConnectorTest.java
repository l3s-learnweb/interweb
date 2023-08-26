package de.l3s.interweb.connector.openai;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.completion.Choice;
import de.l3s.interweb.core.completion.CompletionQuery;
import de.l3s.interweb.core.completion.CompletionResults;
import de.l3s.interweb.core.completion.Message;

@Disabled
@QuarkusTest
class OpenaiCompletionConnectorTest {
    private static final Logger log = Logger.getLogger(OpenaiCompletionConnectorTest.class);

    @ConfigProperty(name = "connectors.openai.key")
    String host;

    @ConfigProperty(name = "connectors.openai.secret")
    String apikey;

    @Inject
    OpenaiCompletionConnector connector;

    @Test
    void query() throws ConnectorException {
        CompletionQuery query = new CompletionQuery();
        query.addMessage("You are Learnweb Assistant, a helpful chat bot.", Message.Role.system);
        query.addMessage("What is your name?.", Message.Role.user);

        CompletionResults results = connector.complete(query, new AuthCredentials(host, apikey)).await().indefinitely();

        assertEquals(1, results.getChoices().size());
        System.out.println("Results for '" + query.getMessages().get(query.getMessages().size() - 1).getContent() + "':");
        for (Choice result : results.getChoices()) {
            System.out.println(result.getMessage().getContent());
        }
    }
}
