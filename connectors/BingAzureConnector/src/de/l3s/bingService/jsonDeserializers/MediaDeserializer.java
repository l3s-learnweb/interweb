package de.l3s.bingService.jsonDeserializers;

import static de.l3s.bingService.jsonDeserializers.JsonUtils.getStringValue;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import de.l3s.bingService.models.Media;
import de.l3s.bingService.models.Thumbnail;

public class MediaDeserializer implements JsonDeserializer<Media> {

	private static final String HOST_PAGE_DISPLAY_URL = "hostPageDisplayUrl";
	private static final String WEB_SEARCH_URL = "webSearchUrl";
	private static final String HOST_PAGE_URL = "hostPageUrl";
	private static final String WEB_SEARCH_URL_PING_SUFFIX = "webSearchUrlPingSuffix";
	private static final String HOST_PAGE_URL_PING_SUFFIX = "hostPageUrlPingSuffix";
	private static final String THUMBNAIL = "thumbnail";
	private static final String NAME = "name";
	private static final String WIDTH = "width";
	private static final String THUMBNAIL_URL = "thumbnailUrl";
	private static final String HEIGHT = "height";
	private static final String ENCODING_FORMAT = "encodingFormat";
	private static final String DATE_PUBLISHED = "datePublished";
	private static final String CONTENT_URL = "contentUrl";

	@Override
	public Media deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		Media media = new Media();
		JsonObject jsonObject = json.getAsJsonObject();
		media.setContentUrl(getStringValue(jsonObject.get(CONTENT_URL)));
		media.setDatePublished(getStringValue(jsonObject.get(DATE_PUBLISHED)));
		media.setEncodingFormat(getStringValue(jsonObject.get(ENCODING_FORMAT)));
		media.setHeight(getStringValue(jsonObject.get(HEIGHT)));
		media.setHostPageDisplayUrl(getStringValue(jsonObject.get(HOST_PAGE_DISPLAY_URL)));
		media.setHostPageUrlPingSuffix(getStringValue(jsonObject.get(HOST_PAGE_URL_PING_SUFFIX)));
		media.setHostPageUrl(getStringValue(jsonObject.get(HOST_PAGE_URL)));
		media.setName(getStringValue(jsonObject.get(NAME)));
		media.setWidth(getStringValue(jsonObject.get(WIDTH)));
		media.setThumbnailUrl(getStringValue(jsonObject.get(THUMBNAIL_URL)));
		media.setWebSearchUrl(getStringValue(jsonObject.get(WEB_SEARCH_URL)));
		media.setWebSearchUrlPingSuffix(getStringValue(jsonObject.get(WEB_SEARCH_URL_PING_SUFFIX)));
		media.setThumbnail(context.deserialize(jsonObject.getAsJsonObject(THUMBNAIL), Thumbnail.class));
		return media;
	}
	
	

}
