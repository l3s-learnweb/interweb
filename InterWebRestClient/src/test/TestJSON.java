package test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.TreeMap;

import javax.ws.rs.core.MediaType;

import l3s.facebook.listresponse.notes.Notes;
import l3s.facebook.listresponse.photos.Photos;
import l3s.facebook.listresponse.profilefeed.ProfileFeed;
import l3s.facebook.listresponse.userlocations.UserLocationObjects;
import l3s.facebook.listresponse.userphotoalbums.Photoalbums;
import l3s.facebook.listresponse.videosuploadedandtagged.Videolist;
import l3s.facebook.objects.video.Video;
import l3s.facebook.search.response.Results;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import de.l3s.interweb.InterWeb;

public class TestJSON {

	public static void main(String[] args) throws JSONException {

		ClientConfig clientConfig = new DefaultClientConfig();
		
		
		Client client = Client.create(clientConfig);

		WebResource resource = client.resource("https://graph.facebook.com/ramitmalhotra/photos?access_token=***REMOVED***");

		TreeMap<String, String> params = new TreeMap<String, String>();

		for (String key : params.keySet()) {
			String value = params.get(key);
			resource = resource.queryParam(key, value);
		}
		
		
		
		
	
		
		
		//ClientResponse response = resource.get(ClientResponse.class);
		//InputStream in = response.getEntityInputStream();
		
		Photos photos= resource.accept(MediaType.APPLICATION_JSON).get(Photos.class);
		System.out.println(photos.toString());
		Gson gson = new Gson();
		
	
		BufferedReader br
    	= new BufferedReader(
    		new InputStreamReader(null));

	StringBuilder sb = new StringBuilder();

	String line;
	try {
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	
	
		JSONObject obj= new JSONObject(sb.toString());
		JSONArray t = obj.getJSONArray("data");
		
		
		Video vid= gson.fromJson(t.get(0).toString(), Video.class);
		vid.getIcon();
	
		Videolist vidlist=gson.fromJson(sb.toString(), Videolist.class);
		vidlist.toString();
		
		try {
			print(null);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

	}

	private static void print(InputStream xmlstream) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(xmlstream));
		String line;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}

	}

	static InputStream call(InterWeb interweb, String method,
			TreeMap<String, String> params) {

		System.out.println("iwToken: [" + interweb.getIWToken() + "]");

		WebResource resource = interweb.createWebResource(method,
				interweb.getIWToken());

		for (String key : params.keySet()) {
			String value = params.get(key);
			resource = resource.queryParam(key, value);
		}
		ClientResponse response = resource.get(ClientResponse.class);
		InputStream in = response.getEntityInputStream();

		return in;
	}

	static InputStream getSN(InterWeb interweb, String method, String userid) {

		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("userid", userid);
		return call(interweb, method, params);

	}

}