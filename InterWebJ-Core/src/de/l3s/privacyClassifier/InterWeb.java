package de.l3s.privacyClassifier;


import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.FileNameMap;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import com.sun.jersey.oauth.client.OAuthClientFilter;
import com.sun.jersey.oauth.signature.HMAC_SHA1;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;

import de.l3s.interwebj.AuthCredentials;
import de.l3s.interwebj.core.Environment;
import de.l3s.interwebj.util.CoreUtils;
import de.l3s.l3sws.jaxb.picalert.XMLPictureSet;


public class InterWeb implements Serializable
{
	private static final long serialVersionUID = -1621494088505203391L;
	
	private final String consumerKey;
	private final String consumerSecret;
	private final String interwebApiURL;
	
	private int usernameLastCacheTime = 0;
	private int authorizationInformationLastCacheTime = 0;
	
	private String username;
	
	private AuthCredentials iwToken = null;
	private int serviceInformationListCacheTime;

	public InterWeb(String interwebApiURL,
	                String consumerKey,
	                String consumerSecret)
	{
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.interwebApiURL = interwebApiURL;
	}

	public String getConsumerKey()
	{
		return consumerKey;
	}	

	public String getConsumerSecret()
	{
		return consumerSecret;
	}

	public String getInterwebApiURL()
	{
		return interwebApiURL;
	}	

	public AuthCredentials getIWToken()
	{
		return iwToken;
	}

	private void resetAuthorizationCache()
	{
		authorizationInformationLastCacheTime = 0;
	}	

	private void resetUsernameCache()
	{
		usernameLastCacheTime = 0;
	}

	public void setIWToken(AuthCredentials iwToken)
	{
		this.iwToken = iwToken;
		// force to reload
		resetAuthorizationCache();
		resetUsernameCache();
	}
	
	public static WebResource createWebResource(String apiUrl, AuthCredentials consumerAuthCredentials, AuthCredentials userAuthCredentials) {
		Client client = Client.create();
		WebResource resource = client.resource(apiUrl);
		OAuthParameters oauthParams = new OAuthParameters();
		oauthParams.consumerKey(consumerAuthCredentials.getKey());
		if (userAuthCredentials != null) {
			oauthParams.token(userAuthCredentials.getKey());
		}
		oauthParams.signatureMethod(HMAC_SHA1.NAME);
		oauthParams.timestamp();
		oauthParams.nonce();
		oauthParams.version();
		OAuthSecrets oauthSecrets = new OAuthSecrets();
		oauthSecrets.consumerSecret(consumerAuthCredentials.getSecret());
		if (userAuthCredentials != null && userAuthCredentials.getSecret() != null) {
			oauthSecrets.tokenSecret(userAuthCredentials.getSecret());
		}
		OAuthClientFilter filter = new OAuthClientFilter(client.getProviders(), oauthParams, oauthSecrets);
		resource.addFilter(filter);

		// System.out.println("requesting filter url: " + filter.toString());
		return resource;
	}

	private WebResource createWebResource(String apiPath, AuthCredentials userAuthCredentials)
	{
		String apiUrl = getInterwebApiURL() + apiPath;
		AuthCredentials consumerAuthCredentials = new AuthCredentials(getConsumerKey(),
		                                                              getConsumerSecret());
		return createWebResource(apiUrl,
		                              consumerAuthCredentials,
		                              userAuthCredentials);
	}
	
	private WebResource createPublicWebResource(String apiPath)
	{
		return createWebResource(apiPath, null);
	}
	
	


	public AuthCredentials getAccessToken(AuthCredentials authCredentials)  throws IllegalResponseException
	{
		WebResource resource = createWebResource("oauth/OAuthGetAccessToken",
		                                         authCredentials);
		ClientResponse response = resource.get(ClientResponse.class);
		Element root = asXML(response);
		if (!root.attribute("stat").getValue().equals("ok"))
		{
			throw new IllegalResponseException(root.asXML());
		}
		Element element = root.element("access_token");
		String token = element.element("oauth_token").getStringValue();
		String tokenSecret = element.element("oauth_token_secret").getStringValue();
		return new AuthCredentials(token, tokenSecret);
	}




	private Element asXML(ClientResponse response) throws IllegalResponseException
	{
		Document doc;
		try
		{
			doc = new SAXReader().read(response.getEntityInputStream());
		}
		catch (Exception e)
		{
			throw new IllegalResponseException(e.getMessage());
		}
		Element root = doc.getRootElement();
		if (!root.attributeValue("stat").equals("ok"))
		{			
			throw new IllegalResponseException(root.asXML());
		}
		return root;
	}
	


	public String buildSignature(String string, TreeMap<String, String> params) {
		System.err.println("Interweb.buildSignature hat olex nicht implementiert");
		return null;
	}
	

	
	public XMLPictureSet privacy(TreeMap<String, String> params,
			XMLPictureSet object, File file) {
		return privacy(params, object,file, null);
	}
	
	public XMLPictureSet privacy(TreeMap<String, String> params,
			XMLPictureSet object, File imagefile, byte[] data) {

		WebResource resource = createWebResource("getprivacy", getIWToken());
		for (String key : params.keySet()) {
			String value = params.get(key);
			resource = resource.queryParam(key, value);
		}

		XMLPictureSet response = null;
		try {
			if (imagefile != null) {

				FormDataMultiPart fdmp = new FormDataMultiPart();
				fdmp.bodyPart(new FileDataBodyPart("file", imagefile,
						MediaType.APPLICATION_OCTET_STREAM_TYPE));

				fdmp.bodyPart(new FormDataBodyPart("name", imagefile.getName()));
				fdmp.bodyPart(new FormDataBodyPart("description",
						"ingredientDesc"));

				if (object != null) {
					fdmp.bodyPart(new FormDataBodyPart("pictureset", object,
							MediaType.APPLICATION_XML_TYPE));
				}
				response = resource.type(MediaType.MULTIPART_FORM_DATA_TYPE)
						.post(XMLPictureSet.class, fdmp);
			} else {
				if (data != null) {
					// FormDataMultiPart fdmp = new FormDataMultiPart();
					FormDataMultiPart fdmp = new FormDataMultiPart().field(
							"file", new ByteArrayInputStream(data),
							MediaType.APPLICATION_OCTET_STREAM_TYPE);
					/*
					 * fdmp.bodyPart(new FileDataBodyPart("file", new
					 * ByteArrayInputStream(data),
					 * MediaType.APPLICATION_OCTET_STREAM_TYPE));
					 */
					fdmp.bodyPart(new FormDataBodyPart("name", "ingredientName"));
					fdmp.bodyPart(new FormDataBodyPart("description",
							"ingredientDesc"));
					response = resource
							.type(MediaType.MULTIPART_FORM_DATA_TYPE).post(
									XMLPictureSet.class, fdmp);
				} else if (object != null) {
					FormDataMultiPart fdmp = new FormDataMultiPart();

					fdmp.bodyPart(new FormDataBodyPart("pictureset", object,
							MediaType.APPLICATION_XML_TYPE));
					response = resource
							.type(MediaType.MULTIPART_FORM_DATA_TYPE).post(
									XMLPictureSet.class, fdmp);

				} else {
					resource.getURI();
					response = resource.get(XMLPictureSet.class);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;

	}

}
