package test;


import java.io.*;
import java.util.*;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import de.l3s.interweb.*;
import de.l3s.interwebj.AuthCredentials;
import de.l3s.interwebj.query.UserSocialNetworkResult;


public class TestSearch
{
	
	public static void main(String[] args)
	{
		
		/*	
		InterWeb interweb = new InterWebJImpl("***REMOVED***_test/api/",
			                                      "***REMOVED***",
			                                      "***REMOVED***");
			
			*/
		
		InterWeb interweb = new InterWebJImpl("http://localhost/InterWebJ/api/",
			                                      "***REMOVED***",
			                                      "***REMOVED***");
		
			TreeMap<String, String> params = new TreeMap<String, String>();
			
			
			
			//interweb.updateAuthorizationCache();
			try {
				System.out.println("user: "+interweb.getUsername());
			} catch (IllegalResponseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			params.put("userid", "jaspreet");
			
			InputStream stream = call(interweb,"getsocialnetwork",params);
			
			try {
				print(stream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

	
	private static void print(InputStream xmlstream) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(xmlstream));
		String line;
		while ((line = br.readLine()) != null)
		{
			System.out.println(line);
		}
		
	}


	static InputStream call(InterWeb interweb, String method, TreeMap<String, String> params)
	{

		
		
		System.out.println("iwToken: [" + interweb.getIWToken() + "]");
		
		WebResource resource = interweb.createWebResource(method, interweb.getIWToken());
		
		
		for (String key : params.keySet())
		{
			String value = params.get(key);
			resource = resource.queryParam(key, value);
		}
		ClientResponse response = resource.get(ClientResponse.class);
		InputStream in = response.getEntityInputStream();

		
		return in;
	}
	static InputStream getSN(InterWeb interweb, String method, String userid)
	{

		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("userid", userid);
		return call(interweb,method,params);
		
	}
	
	
	
	
	

	
}