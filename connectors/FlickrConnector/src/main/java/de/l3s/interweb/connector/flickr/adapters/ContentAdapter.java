package de.l3s.interweb.connector.flickr.adapters;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class ContentAdapter extends StdDeserializer<String> {

    protected ContentAdapter() {
        super(String.class);
    }

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        if (node.isObject() && node.has("_content")) {
            return node.get("_content").asText();
        }

        if (node.isTextual()) {
            return node.asText();
        }

        return null;
    }
}
