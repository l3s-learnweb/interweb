package de.l3s.bingService.jsonDeserializers;

import static de.l3s.bingService.jsonDeserializers.JsonUtils.*;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import de.l3s.bingService.models.Image;
import de.l3s.bingService.models.ImageHolder;

public class ImageMainHolderDeserializer implements JsonDeserializer<ImageHolder>
{

    private static final String VALUE = "value";
    private static final String WEB_SEARCH_URL_PING_SUFFIX = "webSearchUrlPingSuffix";
    private static final String WEB_SEARCH_URL = "webSearchUrl";
    private static final String READ_LINK = "readLink";
    private static final String DISPLAY_SHOPPING_SOURCES_BADGES = "displayShoppingSourcesBadges";
    private static final String DISPLAY_RECIPE_SOURCES_BADGES = "displayRecipeSourcesBadges";
    private static final String ID = "id";

    @Override
    public ImageHolder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        ImageHolder holder = new ImageHolder();
        JsonObject jsonObject = json.getAsJsonObject();
        holder.setId(getStringValue(jsonObject.get(ID)));
        holder.setDisplayRecipeSourcesBadges(getBoolean(jsonObject.get(DISPLAY_RECIPE_SOURCES_BADGES)));
        holder.setDisplayShoppingSourcesBadges(getBoolean(jsonObject.get(DISPLAY_SHOPPING_SOURCES_BADGES)));
        holder.setReadLink(getStringValue(jsonObject.get(READ_LINK)));
        holder.setWebSearchUrl(getStringValue(jsonObject.get(WEB_SEARCH_URL)));
        holder.setWebSearchUrlPingSuffix(getStringValue(jsonObject.get(WEB_SEARCH_URL_PING_SUFFIX)));
        JsonArray images = jsonObject.getAsJsonArray(VALUE);
        if(images != null)
        {
            holder.setValue(new ArrayList<>());
            for(JsonElement image : images)
            {
                holder.getValue().add(context.deserialize(image, Image.class));
            }
        }
        return holder;
    }

}
