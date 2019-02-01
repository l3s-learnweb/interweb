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

import de.l3s.bingService.models.NewsHolder;
import de.l3s.bingService.models.WebPage;

public class NewsHolderDeserializer implements JsonDeserializer<NewsHolder>
{

    private static final String VALUE = "value";
    private static final String READ_LINK = "readLink";
    private static final String ID = "id";

    @Override
    public NewsHolder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        NewsHolder holder = new NewsHolder();
        JsonObject jsonObject = json.getAsJsonObject();
        holder.setId(getStringValue(jsonObject.get(ID)));
        holder.setReadLink(getStringValue(jsonObject.get(READ_LINK)));
        JsonArray news = jsonObject.getAsJsonArray(VALUE);
        if(news != null)
        {
            holder.setValue(new ArrayList<>());
            for(JsonElement element : news)
            {
                holder.getValue().add(context.deserialize(element, WebPage.class));
            }
        }
        return holder;
    }

}
