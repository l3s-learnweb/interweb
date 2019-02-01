package de.l3s.bingService.jsonDeserializers;

import static de.l3s.bingService.jsonDeserializers.JsonUtils.getStringValue;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import de.l3s.bingService.models.Media;
import de.l3s.bingService.models.Publisher;
import de.l3s.bingService.models.Video;

public class VideoDeselializer implements JsonDeserializer<Video>
{

    private static final String VIEW_COUNT = "viewCount";
    private static final String PUBLISHER = "publisher";
    private static final String MOTION_THUMBNAIL_URL = "motionThumbnailUrl";
    private static final String EMBED_HTML = "embedHtml";
    private static final String DURATION = "duration";
    private static final String DESCRIPTION = "description";

    @Override
    public Video deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        Video video = new Video();
        JsonObject jsonObject = json.getAsJsonObject();
        video.setMedia(context.deserialize(jsonObject, Media.class));
        video.setDescription(getStringValue(jsonObject.get(DESCRIPTION)));
        video.setDuration(getStringValue(jsonObject.get(DURATION)));
        video.setEmbedHtml(getStringValue(jsonObject.get(EMBED_HTML)));
        video.setMotionThumbnailUrl(getStringValue(jsonObject.get(MOTION_THUMBNAIL_URL)));
        video.setViewCount(getStringValue(jsonObject.get(VIEW_COUNT)));
        JsonArray publishers = jsonObject.getAsJsonArray(PUBLISHER);
        if(publishers != null)
        {
            video.setPublisher(new ArrayList<>());
            for(JsonElement link : publishers)
            {
                video.getPublisher().add(context.deserialize(link, Publisher.class));
            }
        }
        return video;
    }

}
