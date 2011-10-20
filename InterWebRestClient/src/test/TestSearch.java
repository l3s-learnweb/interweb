package test;


import java.io.*;
import java.util.*;

import de.l3s.interweb.*;


public class TestSearch
{
	
	public static void main(String[] args)
	{
		
			
		InterWeb interweb = new InterWebJImpl("***REMOVED***_test/api/",
			                                      "***REMOVED***",
			                                      "***REMOVED***");
			
			
			//InterWeb interweb = new InterWebImpl("http://athena.l3s.uni-hannover.de:8000/api/", "71ed49ec4185a50cb0f47257c0d56e0104b7be4cb", "57e6bd162ffb2af298bbab930681870a");
			TreeMap<String, String> params = new TreeMap<String, String>();
			
			String services = "Flickr,YouTube,SlideShare,Google";
			
			String mediatypes = "image,presentation,text,video";
			
			params.put("media_types", mediatypes);
			params.put("number_of_results", "10"); //pro service
		//	params.put("parallel", "1");
			params.put("services", services);
			String query = "whale";
			
			doSearch("whale",interweb,params);
		doSearch("whale",interweb,params);
		doSearch("tree",interweb,params);
	}

	private static void doSearch(String query, InterWeb interweb, TreeMap<String, String> params) {
		try{
		InputStream xmlstream = interweb.searchAsXML(query, params);
	
	BufferedReader br = new BufferedReader(new InputStreamReader(xmlstream));
	String line;
	while ((line = br.readLine()) != null)
	{
		System.out.println(line);
	}
	
	/*
	 * Alternatively use the built in parser
	 */
	SearchQuery parsedquery = interweb.search(query, params);
	
	List<SearchResult> results = parsedquery.getResults();
	if (results.size() == 0)
	{
		System.err.print("No results!");
	}
	for (SearchResult result : results)
	{
		System.out.println(result.getServiceName() + ":"
		                   + result.getTitle());
	}
	
}
catch (Exception e)
{
	e.printStackTrace();
}}
	
}
