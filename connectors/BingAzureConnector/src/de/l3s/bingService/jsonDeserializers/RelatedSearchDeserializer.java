package de.l3s.bingService.jsonDeserializers;

import static de.l3s.bingService.jsonDeserializers.JsonUtils.getStringValue;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import de.l3s.bingService.models.RelatedSearch;

public class RelatedSearchDeserializer implements JsonDeserializer<RelatedSearch>
{

    private static final String WEB_SEARCH_URL_PING_SUFFIX = "webSearchUrlPingSuffix";
    private static final String WEB_SEARCH_URL = "webSearchUrl";
    private static final String DISPLAY_TEXT = "displayText";
    private static final String TEXT = "text";

    @Override
    public RelatedSearch deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
	RelatedSearch relatedSearch = new RelatedSearch();
	JsonObject jsonObject = json.getAsJsonObject();
	relatedSearch.setText(getStringValue(jsonObject.get(TEXT)));
	relatedSearch.setDisplayText(getStringValue(jsonObject.get(DISPLAY_TEXT)));
	relatedSearch.setWebSearchUrl(getStringValue(jsonObject.get(WEB_SEARCH_URL)));
	relatedSearch.setWebSearchUrlPingSuffix(getStringValue(jsonObject.get(WEB_SEARCH_URL_PING_SUFFIX)));
	return relatedSearch;
    }

}
