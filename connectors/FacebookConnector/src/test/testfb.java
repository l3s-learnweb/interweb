package test;

import java.util.TreeMap;

import javax.ws.rs.core.MediaType;

import l3s.facebook.batch.Batch;
import l3s.facebook.batch.ObjectFactory;
import l3s.facebook.listresponse.links.SharedLinks;
import l3s.facebook.listresponse.photos.Photos;
import l3s.facebook.listresponse.status.Statuses;

import l3s.facebook.listresponse.userevents.Events;
import l3s.facebook.listresponse.usergroups.Groups;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import facebook.api.Facebook;

public class testfb {

	public static void main(String[] args) {
//
//		ClientConfig clientConfig = new DefaultClientConfig();
//		
//		
//		Client client = Client.create(clientConfig);
//
//		WebResource resource = client.resource("https://graph.facebook.com/13757040/groups?access_token=***REMOVED***");
//
//		TreeMap<String, String> params = new TreeMap<String, String>();
//
//		for (String key : params.keySet()) {
//			String value = params.get(key);
//			resource = resource.queryParam(key, value);
//		}
//		
		String accessToken="***REMOVED***";
		String graphAPI = "https://graph.facebook.com/";
		ClientConfig clientConfig = new DefaultClientConfig();
		Client client = Client.create(clientConfig);
		WebResource resource = client.resource(graphAPI);
		
		resource = resource.queryParam("batch", " [{\"method\":\"GET\", \"relative_url\":\"me\"}]");
		resource = resource.queryParam("access_token", accessToken);
		ObjectFactory oj = new ObjectFactory();
		Batch batch = oj.createBatch();
		resource.post();
		 String t = resource.head().getClientResponseStatus().toString();
		 System.out.println(t);
		
		
		
		//ClientResponse response = resource.get(ClientResponse.class);
		//InputStream in = response.getEntityInputStream();
		Facebook fbapi= new Facebook(accessToken);
		String userid="me";
		Events events = fbapi.getEventsUserIsInvolvedIn(userid);
		System.out.println(events.getData().size());
		
		
	}
}
