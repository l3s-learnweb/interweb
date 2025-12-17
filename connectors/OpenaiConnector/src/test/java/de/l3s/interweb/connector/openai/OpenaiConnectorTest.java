package de.l3s.interweb.connector.openai;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import de.l3s.interweb.core.embeddings.Embedding;
import de.l3s.interweb.core.embeddings.EmbeddingsQuery;

import de.l3s.interweb.core.embeddings.EmbeddingsResults;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.chat.*;

@Disabled
@QuarkusTest
class OpenaiConnectorTest {
    private static final Logger log = Logger.getLogger(OpenaiConnectorTest.class);

    @Inject
    OpenaiConnector connector;

    @Test
    void validate() throws ConnectorException {
        assertTrue(connector.validate());
    }

    @Test
    void embeddings() throws ConnectorException {
        EmbeddingsQuery query = new EmbeddingsQuery();
        query.setModel("text-embedding-3-small");
        query.setInput("The quick brown fox jumps over the lazy dog.");

        EmbeddingsResults results = connector.embeddings(query).await().indefinitely();

        assertEquals(1, results.getData().size());
        log.infov("input: {0}", query.getInput().getFirst());
        for (Embedding result : results.getData()) {
            log.infov("embedding: {0} (dim: {1})", result.getEmbedding().toString(), result.getEmbedding().size());
        }
    }

    @Test
    void completions() throws ConnectorException {
        CompletionsQuery query = new CompletionsQuery();
        query.setLogprobs(true);
        query.setTopLogprobs(3);
        query.setModel("gpt-5");
        query.addMessage("You are Interweb Assistant, a helpful chat bot.", Role.system);
        query.addMessage("What is your name?", Role.user);

        CompletionsResults results = connector.completions(query).await().indefinitely();

        assertEquals(1, results.getChoices().size());
        log.infov("user: {0}", query.getMessages().getLast().getContent());
        for (Choice result : results.getChoices()) {
            log.infov("assistant: {0}", result.getMessage().getContent());
        }
    }

    @Test
    @Disabled
    void completionsWithTool() throws ConnectorException {
        Tool weatherTool = Tool.functionBuilder()
            .name("get_weather")
            .description("Return the weather in a city.")
            .addProperty(city -> city.name("city").type("string").description("The city name.").required())
            .addProperty(duration -> duration.name("duration").type("integer").description("Number of days to return forecast for.").required())
            .build();

        CompletionsQuery query = new CompletionsQuery();
        query.setModel("gpt-5-mini");
        query.setTools(List.of(weatherTool));
        query.setToolChoice(ToolChoice.required);
        query.addMessage("You are Interweb Assistant, a helpful chat bot.", Role.system);
        query.addMessage("What is the weather in Hannover next week?", Role.user);

        CompletionsResults results = connector.completions(query).await().indefinitely();

        assertEquals(1, results.getChoices().size());
        for (Choice result : results.getChoices()) {
            assertNotNull(result.getMessage().getToolCalls());
            CallFunction fn = result.getMessage().getToolCalls().getFirst().getFunction();
            assertNotNull(fn);
            assertEquals("get_weather", fn.getName());
            assertEquals("Hannover", fn.getArguments().get("city"));
            assertNotNull(fn.getArguments().get("duration"));
        }
    }
}
