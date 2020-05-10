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

import de.l3s.bingService.models.About;
import de.l3s.bingService.models.Image;
import de.l3s.bingService.models.New;
import de.l3s.bingService.models.Provider;

public class NewsDeserializer implements JsonDeserializer<New>
{

    private static final String IMAGE = "image";
    private static final String PROVIDER = "provider";
    private static final String URL_PING_SUFFIX = "urlPingSuffix";
    private static final String URL = "url";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String DATE_PUBLISHED = "datePublished";
    private static final String CATEGORY = "category";
    private static final String ABOUT = "about";

    @Override
    public New deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
	New news = new New();
	JsonObject jsonObject = json.getAsJsonObject();
	news.setCategory(getStringValue(jsonObject.get(CATEGORY)));
	news.setDatePublished(getStringValue(jsonObject.get(DATE_PUBLISHED)));
	news.setDescription(getStringValue(jsonObject.get(DESCRIPTION)));
	news.setName(getStringValue(jsonObject.get(NAME)));
	news.setUrl(getStringValue(jsonObject.get(URL)));
	news.setUrlPingSuffix(getStringValue(jsonObject.get(URL_PING_SUFFIX)));
	news.setImage(context.deserialize(jsonObject.getAsJsonObject(IMAGE), Image.class));
	JsonArray providers = jsonObject.getAsJsonArray(PROVIDER);
	if(providers != null)
	{
	    news.setProviders(new ArrayList<>());
	    for(JsonElement provider : providers)
	    {
		news.getProviders().add(context.deserialize(provider, Provider.class));
	    }
	}
	JsonArray about = jsonObject.getAsJsonArray(ABOUT);
	if(about != null)
	{
	    news.setAbout(new ArrayList<>());
	    for(JsonElement aboutElement : about)
	    {
		news.getAbout().add(context.deserialize(aboutElement, About.class));
	    }
	}
	return news;
    }
}
