package de.l3s.fedora;

import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class ReturnObjectTester {
public static void main(String[] args) {
	Client client = Client.create();
	WebResource resource=null;
	
		resource = client.resource("http://learnweb.l3s.uni-hannover.de/FedoraKRSM/fedora/search/terms=salsa&maxResults=10");
	FedoraSearchResult searchresult=resource.get(FedoraSearchResult.class);
	System.out.println(searchresult);
	int z=9;
	z++;
}
}
