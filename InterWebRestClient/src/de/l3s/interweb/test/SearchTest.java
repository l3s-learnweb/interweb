package de.l3s.interweb.test;


import java.util.*;

import de.l3s.interweb.*;
import de.l3s.interweb.util.*;


public class SearchTest
{
	
	private String mediaTypesSetting;
	
	private String resultsSetting;
	
	private boolean parallelRequestSetting;
	private String servicesSetting;
	private boolean useLandmarkseekerSetting;
	

	public void loadPreferences(SharedPreferences pref, int resultsProservice)
	{
		if (pref == null)
		{
			pref = new SharedPreferences();
		}
		StringBuilder buffer = new StringBuilder();
		
		if (pref.getBoolean("media_photos", true))
		{
			buffer.append(",photos");
		}
		if (pref.getBoolean("media_videos", true))
		{
			buffer.append(",videos");
		}
		if (pref.getBoolean("media_slideshows", false))
		{
			buffer.append(",slideshows");
		}
		if (pref.getBoolean("media_audio", true))
		{
			buffer.append(",audio");
		}
		if (pref.getBoolean("media_music", true))
		{
			buffer.append(",music");
		}
		if (pref.getBoolean("media_bookmarks", true))
		{
			buffer.append(",bookmarks");
		}
		
		if (buffer.length() != 0)
		{
			buffer.deleteCharAt(0); // delete the first ","
		}
		mediaTypesSetting = buffer.toString();
		
		buffer.setLength(0);
		
		if (pref.getBoolean("service_delicious", true))
		{
			buffer.append(",Delicious");
		}
		if (pref.getBoolean("service_flickr", true))
		{
			buffer.append(",Flickr");
		}
		if (pref.getBoolean("service_ipernity", true))
		{
			buffer.append(",Ipernity");
		}
		if (pref.getBoolean("service_lastfm", true))
		{
			buffer.append(",LastFm");
		}
		if (false && pref.getBoolean("service_slideshare", false))
		{
			buffer.append(",SlideShare");
		}
		if (pref.getBoolean("service_vimeo", true))
		{
			buffer.append(",Vimeo");
		}
		if (pref.getBoolean("service_youtube", true))
		{
			buffer.append(",YouTube");
		}
		
		if (buffer.length() != 0)
		{
			buffer.deleteCharAt(0); // delete the first ","
		}
		servicesSetting = buffer.toString();
		
		parallelRequestSetting = pref.getBoolean("parallel_request", true);
		
		resultsSetting = pref.getString("results", resultsProservice + "");
		
		useLandmarkseekerSetting = pref.getBoolean("use_landmarkseeker", false);
	}
	

	private SearchQuery test(String query,
	                         int results_proservice,
	                         boolean landmarkseeker,
	                         String mediatypes,
	                         String services)
	{
		
		loadPreferences(null, results_proservice);
		
		InterWeb interweb = new InterWebImpl("http://athena.l3s.uni-hannover.de:8000/api/",
		                                     "71ed49ec4185a50cb0f47257c0d56e0104b7be4cb",
		                                     "57e6bd162ffb2af298bbab930681870a");
		TreeMap<String, String> params = new TreeMap<String, String>();
		
		params.put("media_types", mediatypes == null
		    ? mediaTypesSetting : mediatypes);
		
		params.put("number_of_results", resultsSetting);
		
		params.put("parallel", parallelRequestSetting
		    ? "1" : "0");
		
		params.put("services", services == null
		    ? servicesSetting : services);
		
		params.put("use_landmarkseeker", landmarkseeker
		    ? "1" : "0");
		
		try
		{
			SearchQuery ret = interweb.search(query, params);
			int z = 0;
			z++;
			z++;
			return ret;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
		
	}
	

	public static SearchQuery loadSearchResults(String query,
	                                            int results_proservice,
	                                            boolean landmarkseeker,
	                                            String mediatypes,
	                                            String services)
	{
		SearchTest t = new SearchTest();
		SearchQuery q = t.test(query,
		                       results_proservice,
		                       landmarkseeker,
		                       mediatypes,
		                       services);
		return q;
		
	}
	

	public static void main(String[] args)
	{
		SearchTest t = new SearchTest();
		SearchQuery q = t.test("london", 3, true, "videos", "YouTube");
		List<SearchResult> results = q.getResults();
		
		System.out.println(results);
	}
}
