package de.l3s.privacyClassifier;


import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.oauth.client.OAuthClientFilter;
import com.sun.jersey.oauth.signature.HMAC_SHA1;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;

import de.l3s.interwebj.AuthCredentials;
import de.l3s.l3sws.jaxb.picalert.XMLPictureSet;


public class PicalertClient
{
	private final String consumerKey;
	private final String consumerSecret;
	private final String interwebApiURL;

	public PicalertClient(String interwebApiURL, String consumerKey, String consumerSecret)
	{
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.interwebApiURL = interwebApiURL;
	}
	
	private static WebResource createWebResource(String apiUrl, AuthCredentials consumerAuthCredentials) 
	{
		Client client = Client.create();
		WebResource resource = client.resource(apiUrl);
		OAuthParameters oauthParams = new OAuthParameters();
		oauthParams.consumerKey(consumerAuthCredentials.getKey());

		oauthParams.signatureMethod(HMAC_SHA1.NAME);
		oauthParams.timestamp();
		oauthParams.nonce();
		oauthParams.version();
		OAuthSecrets oauthSecrets = new OAuthSecrets();
		oauthSecrets.consumerSecret(consumerAuthCredentials.getSecret());
		OAuthClientFilter filter = new OAuthClientFilter(client.getProviders(), oauthParams, oauthSecrets);
		resource.addFilter(filter);

		return resource;
	}

	public XMLPictureSet privacy(XMLPictureSet object) 
	{
		AuthCredentials consumerAuthCredentials = new AuthCredentials(consumerKey, consumerSecret);
		WebResource resource = createWebResource(interwebApiURL + "getprivacy", consumerAuthCredentials);	

	
		XMLPictureSet response = null;
		try {				
			FormDataMultiPart fdmp = new FormDataMultiPart();

			fdmp.bodyPart(new FormDataBodyPart("pictureset", object, MediaType.APPLICATION_XML_TYPE));
			response = resource.type(MediaType.MULTIPART_FORM_DATA_TYPE).post(XMLPictureSet.class, fdmp);			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
}
