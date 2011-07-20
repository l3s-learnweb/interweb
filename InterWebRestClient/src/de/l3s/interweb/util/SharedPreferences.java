package de.l3s.interweb.util;

import java.util.Hashtable;

public class SharedPreferences {

	Hashtable<String, Object> prefs=new Hashtable<String, Object>();
	public SharedPreferences()
	{
		prefs.put("media_photos", true);	
		prefs.put("media_videos", true);
		prefs.put("media_slideshows", true);
		prefs.put("media_audio", true);
		prefs.put("media_music", true);
		prefs.put("media_bookmarks", true);
		prefs.put("service_delicious", true);
		prefs.put("service_flickr", true);
		prefs.put("service_ipernity", true);
		prefs.put("service_lastfm", true);
		prefs.put("service_slideshare", true);
		prefs.put("service_vimeo", true);
		prefs.put("service_youtube", true);

	}
	public boolean getBoolean(String string, boolean b) {
		Boolean obj = (Boolean)prefs.get(string);
		if(obj==null) return b;
		return obj.booleanValue();
	}
	public String getString(String string, String string2) {
		String obj = (String)prefs.get(string);
		if(obj==null) return string2;
		return string;
	}

}
