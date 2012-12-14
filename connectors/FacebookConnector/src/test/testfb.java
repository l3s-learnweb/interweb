package test;

import java.util.TreeMap;

import javax.ws.rs.core.MediaType;

import l3s.facebook.listresponse.photos.Photos;
import l3s.facebook.listresponse.status.Statuses;

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
		//ClientResponse response = resource.get(ClientResponse.class);
		//InputStream in = response.getEntityInputStream();
		Facebook fbapi= new Facebook(accessToken);
		String userid="me";
		Statuses statuslist=fbapi.getStatusUpdateList(userid);
		System.out.println(statuslist.getData().get(1).getMessage());
		if(statuslist.getData().get(1).getPlace()!=null)
		System.out.println(statuslist.getData().get(1).getPlace().getName());
		
	}
}
