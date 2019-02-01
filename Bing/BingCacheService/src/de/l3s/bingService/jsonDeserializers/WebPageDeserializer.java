package de.l3s.bingService.jsonDeserializers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import static de.l3s.bingService.jsonDeserializers.JsonUtils.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import de.l3s.bingService.models.About;
import de.l3s.bingService.models.WebPage;

public class WebPageDeserializer implements JsonDeserializer<WebPage>
{

    private static final String ABOUT = "about";
    private static final String DEEP_LINKS = "deepLinks";
    private static final String DATE_LAST_CRAWLED = "dateLastCrawled";
    private static final String DISPLAY_URL = "displayUrl";
    private static final String SNIPPET = "snippet";
    //private static final String ID = "id";
    private static final String URL = "url";
    private static final String NAME = "name";

    @Override
    public WebPage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        WebPage webPage = new WebPage();
        JsonObject jsonObject = json.getAsJsonObject();
        webPage.setUrl(getStringValue(jsonObject.get(URL)));
        //webPage.setId(getStringValue(jsonObject.get(ID)));  THE ID is useless
        webPage.setSnippet(getStringValue(jsonObject.get(SNIPPET)));
        webPage.setDisplayUrl(getStringValue(jsonObject.get(DISPLAY_URL)));
        webPage.setDateLastCrawled(getStringValue(jsonObject.get(DATE_LAST_CRAWLED)));
        webPage.setName(getStringValue(jsonObject.get(NAME)));
        JsonArray deepLinks = jsonObject.getAsJsonArray(DEEP_LINKS);
        if(deepLinks != null)
        {
            webPage.setDeepLinks(new ArrayList<>());
            for(JsonElement link : deepLinks)
            {
                webPage.getDeepLinks().add(context.deserialize(link, WebPage.class));
            }
        }
        JsonArray about = jsonObject.getAsJsonArray(ABOUT);
        if(about != null)
        {
            webPage.setAbout(new ArrayList<>());
            for(JsonElement aboutElement : about)
            {
                webPage.getAbout().add(context.deserialize(aboutElement, About.class));
            }
        }
        return webPage;
    }
}
