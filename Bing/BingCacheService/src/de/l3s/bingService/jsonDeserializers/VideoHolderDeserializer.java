package de.l3s.bingService.jsonDeserializers;

import static de.l3s.bingService.jsonDeserializers.JsonUtils.getBoolean;
import static de.l3s.bingService.jsonDeserializers.JsonUtils.getStringValue;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import de.l3s.bingService.models.Video;
import de.l3s.bingService.models.VideoHolder;

public class VideoHolderDeserializer implements JsonDeserializer<VideoHolder>
{

    private static final String VALUE = "value";
    private static final String IS_FAMILY_FRIENDLY = "isFamilyFriendly";
    private static final String WEB_SEARCH_URL = "webSearchUrl";
    private static final String READ_LINK = "readLink";
    private static final String SCENARIO = "scenario";

    @Override
    public VideoHolder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        VideoHolder holder = new VideoHolder();
        JsonObject jsonObject = json.getAsJsonObject();
        holder.setScenario(getStringValue(jsonObject.get(SCENARIO)));
        holder.setReadLink(getStringValue(jsonObject.get(READ_LINK)));
        holder.setWebSearchUrl(getStringValue(jsonObject.get(WEB_SEARCH_URL)));
        holder.setFamilyFriendly(getBoolean(jsonObject.get(IS_FAMILY_FRIENDLY)));
        JsonArray videos = jsonObject.getAsJsonArray(VALUE);
        if(videos != null)
        {
            holder.setValue(new ArrayList<>());
            for(JsonElement video : videos)
            {
                holder.getValue().add(context.deserialize(video, Video.class));
            }
        }
        return holder;
    }

}
