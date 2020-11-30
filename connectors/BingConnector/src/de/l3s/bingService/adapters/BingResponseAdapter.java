package de.l3s.bingService.adapters;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import de.l3s.bingService.models.BingResponse;
import de.l3s.bingService.models.ImageHolder;
import de.l3s.bingService.models.VideoHolder;
import de.l3s.bingService.models.WebPagesHolder;
import de.l3s.bingService.models.Error;

public class BingResponseAdapter implements JsonDeserializer<BingResponse> {

    @Override
    public BingResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("_type").getAsString();

        BingResponse bingResponse = new BingResponse();
        if (type.equals("SearchResponse")) {
            if (jsonObject.has("webPages")) {
                bingResponse.setWebPages(context.deserialize(jsonObject.get("webPages"), WebPagesHolder.class));
            }
            if (jsonObject.has("images")) {
                bingResponse.setImages(context.deserialize(jsonObject.get("images"), ImageHolder.class));
            }
            if (jsonObject.has("videos")) {
                bingResponse.setVideos(context.deserialize(jsonObject.get("videos"), VideoHolder.class));
            }
        } else if (type.equals("Images")) {
            bingResponse.setImages(context.deserialize(json, ImageHolder.class));
        } else if (type.equals("Videos")) {
            bingResponse.setVideos(context.deserialize(json, VideoHolder.class));
        } else if (type.equals("ErrorResponse")) {
            List<Error> errorList = new ArrayList<>();
            jsonObject.getAsJsonArray("errors").forEach(e -> errorList.add(context.deserialize(e, Error.class)));
            bingResponse.setErrors(errorList);
        }
        return bingResponse;
    }
}
