package de.l3s.bingService.jsonDeserializers;

import com.google.gson.JsonElement;

public class JsonUtils
{

    public static String getStringValue(JsonElement jsonElement)
    {
	return jsonElement != null ? jsonElement.getAsString() : null;
    }

    public static Boolean getBoolean(JsonElement object)
    {
	if(object == null)
	    return null;
	return Boolean.valueOf(object.getAsString());
    }
}
