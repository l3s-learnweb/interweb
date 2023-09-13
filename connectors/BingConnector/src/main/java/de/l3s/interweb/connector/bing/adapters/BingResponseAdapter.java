package de.l3s.interweb.connector.bing.adapters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import de.l3s.interweb.connector.bing.entity.Error;
import de.l3s.interweb.connector.bing.entity.*;

public class BingResponseAdapter extends StdDeserializer<BingResponse> {

    protected BingResponseAdapter() {
        super(BingResponse.class);
    }

    @Override
    public BingResponse deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        BingResponse bingResponse = new BingResponse();
        JsonNode node = p.getCodec().readTree(p);

        if (node.has("error")) {
            bingResponse.setError(ctxt.readTreeAsValue(node.get("error"), Error.class));
            return bingResponse;
        }

        String type = node.get("_type").asText();
        switch (type) {
            case "SearchResponse" -> {
                if (node.has("webPages")) {
                    bingResponse.setWebPages(ctxt.readTreeAsValue(node.get("webPages"), WebPagesHolder.class));
                }
                if (node.has("images")) {
                    bingResponse.setImages(ctxt.readTreeAsValue(node.get("images"), ImageHolder.class));
                }
                if (node.has("videos")) {
                    bingResponse.setVideos(ctxt.readTreeAsValue(node.get("videos"), VideoHolder.class));
                }
            }
            case "Images" -> bingResponse.setImages(ctxt.readTreeAsValue(node, ImageHolder.class));
            case "Videos" -> bingResponse.setVideos(ctxt.readTreeAsValue(node, VideoHolder.class));
            case "ErrorResponse" -> {
                List<Error> errorList = new ArrayList<>();
                for (JsonNode err : node.withArray("errors")) {
                    errorList.add(ctxt.readTreeAsValue(err, Error.class));
                }
                bingResponse.setError(errorList.get(0));
            }
            default -> throw new IOException("Unknown response type: " + type);
        }
        return bingResponse;
    }
}
