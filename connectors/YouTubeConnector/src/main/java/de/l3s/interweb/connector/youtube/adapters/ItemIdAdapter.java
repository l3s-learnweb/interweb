package de.l3s.interweb.connector.youtube.adapters;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class ItemIdAdapter extends StdDeserializer<String> {

    protected ItemIdAdapter() {
        super(String.class);
    }

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        if (node.isObject() && node.has("videoId")) {
            return node.get("videoId").asText();
        }

        if (node.isTextual()) {
            return node.asText();
        }

        return null;
    }
}
