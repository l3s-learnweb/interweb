package test;

import java.util.TreeMap;

import javax.ws.rs.core.MediaType;

import l3s.facebook.listresponse.usergroups.Groups;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class testfb {

	public static void main(String[] args) {

		ClientConfig clientConfig = new DefaultClientConfig();
		
		
		Client client = Client.create(clientConfig);

		WebResource resource = client.resource("***REMOVED***");

		TreeMap<String, String> params = new TreeMap<String, String>();

		for (String key : params.keySet()) {
			String value = params.get(key);
			resource = resource.queryParam(key, value);
		}
		
		
		
		
	
		
		
		//ClientResponse response = resource.get(ClientResponse.class);
		//InputStream in = response.getEntityInputStream();
		
		Groups photos= resource.accept(MediaType.APPLICATION_JSON).get(Groups.class);
		System.out.println(photos.toString());
	}
}
