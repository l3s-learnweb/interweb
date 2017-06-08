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

import de.l3s.bingService.models.WebPage;
import de.l3s.bingService.models.WebPagesMainHolder;

public class WebPageHolderDeserializer implements JsonDeserializer<WebPagesMainHolder> {

	private static final String WEB_SEARCH_URL_PING_SUFFIX = "webSearchUrlPingSuffix";
	private static final String WEB_SEARCH_URL = "webSearchUrl";
	private static final String TOTAL_ESTIMATED_MATCHES = "totalEstimatedMatches";
	private static final String VALUE = "value";

	@Override
	public WebPagesMainHolder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		WebPagesMainHolder holder = new WebPagesMainHolder();
		JsonObject jsonObject = json.getAsJsonObject();
		holder.setTotalEstimatedMatches(getStringValue(jsonObject.get(TOTAL_ESTIMATED_MATCHES)));
		holder.setWebSearchUrl(getStringValue(jsonObject.get(WEB_SEARCH_URL)));
		holder.setWebSearchUrlPingSuffix(getStringValue(jsonObject.get(WEB_SEARCH_URL_PING_SUFFIX)));
		JsonArray webPages = jsonObject.getAsJsonArray(VALUE);
		if (webPages != null) {
			holder.setValue(new ArrayList<>());
			for (JsonElement webPage : webPages) {
				holder.getValue().add(context.deserialize(webPage, WebPage.class));
			}
		}
		return holder;
	}

}
