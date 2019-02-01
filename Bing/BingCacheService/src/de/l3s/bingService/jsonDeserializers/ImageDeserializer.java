package de.l3s.bingService.jsonDeserializers;

import static de.l3s.bingService.jsonDeserializers.JsonUtils.getStringValue;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import de.l3s.bingService.models.Image;
import de.l3s.bingService.models.Media;

public class ImageDeserializer implements JsonDeserializer<Image>
{

    private static final String CONTENT_SIZE = "contentSize";

    @Override
    public Image deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        Image image = new Image();
        JsonObject jsonObject = json.getAsJsonObject();
        image.setContentSize(getStringValue(jsonObject.get(CONTENT_SIZE)));
        image.setMedia(context.deserialize(jsonObject, Media.class));
        return image;
    }

}
