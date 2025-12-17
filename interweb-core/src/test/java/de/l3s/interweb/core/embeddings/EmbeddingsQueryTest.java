package de.l3s.interweb.core.embeddings;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EmbeddingsQueryTest {

    @Test
    public void testDeserialization() throws Exception {
        String json = "{\"model\": \"text-embedding-3-small\", \"input\": \"test\"}";
        ObjectMapper mapper = new ObjectMapper();
        EmbeddingsQuery query = mapper.readValue(json, EmbeddingsQuery.class);

        assertNotNull(query);
        assertEquals("text-embedding-3-small", query.getModel());
        assertNotNull(query.getInput());
        assertEquals(1, query.getInput().size());
        assertEquals("test", query.getInput().get(0));
    }

    @Test
    public void testDeserializationArray() throws Exception {
        String json = "{\"model\": \"text-embedding-3-small\", \"input\": [\"test1\", \"test2\"]}";
        ObjectMapper mapper = new ObjectMapper();
        EmbeddingsQuery query = mapper.readValue(json, EmbeddingsQuery.class);

        assertNotNull(query);
        assertEquals("text-embedding-3-small", query.getModel());
        assertNotNull(query.getInput());
        assertEquals(2, query.getInput().size());
        assertEquals("test1", query.getInput().get(0));
        assertEquals("test2", query.getInput().get(1));
    }
}

