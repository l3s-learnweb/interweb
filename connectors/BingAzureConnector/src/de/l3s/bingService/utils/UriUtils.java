package de.l3s.bingService.utils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class UriUtils {

	private static final String R = "r";
	private static final String UTF_8 = "UTF-8";

	public static String splitQuery(String url) {
		URL fullUri = null;
		try {
			fullUri = new URL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
		}
		if (fullUri != null) {
			Map<String, String> query_pairs = new LinkedHashMap<String, String>();
			String query = fullUri.getQuery();
			String[] pairs = query.split("&");
			for (String pair : pairs) {
				int idx = pair.indexOf("=");
				try {
					query_pairs.put(URLDecoder.decode(pair.substring(0, idx), UTF_8),
							URLDecoder.decode(pair.substring(idx + 1), UTF_8));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
				}
			}
			return query_pairs.get(R) == null ? url : query_pairs.get(R);
		}
		return null;
	}
}
