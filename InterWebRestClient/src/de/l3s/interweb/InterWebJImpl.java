package de.l3s.interweb;


import java.io.*;
import java.nio.charset.*;
import java.util.*;

import javax.ws.rs.core.*;

import org.apache.commons.lang.*;
import org.dom4j.*;
import org.dom4j.io.*;
import org.xml.sax.*;

import com.sun.jersey.api.client.*;
import com.sun.jersey.core.util.*;

import de.l3s.interweb.AuthorizationInformation.ServiceInformation;
import de.l3s.interwebj.*;
import de.l3s.interwebj.query.*;
import de.l3s.interwebj.util.*;


public class InterWebJImpl
    extends InterWeb
{
	
	public static boolean DEBUG = true;
	
	private String getUrl;
	

	public InterWebJImpl(String interwebApiURL,
	                     String consumerKey,
	                     String consumerSecret)
	{
		super(interwebApiURL, consumerKey, consumerSecret);
	}
	

	@Override
	public void authorizeService(ServiceInformation service, String callback)
	    throws IllegalResponseException
	{
		WebResource resource = createWebResource("users/default/services/"
		                                             + service.getId()
		                                             + "/auth",
		                                         getIWToken());
		resource = resource.queryParam("callback", callback);
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		if (service.getKey() != null && service.getSecret() != null)
		{
			params.add("username", service.getKey());
			params.add("password", service.getSecret());
		}
		ClientResponse response = resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class,
		                                                                                    params);
		CoreUtils.printClientResponse(response);
		Element root = asXML(response);
		if (!root.attribute("stat").getValue().equals("ok"))
		{
			throw new IllegalResponseException(root.asXML());
		}
		String link = root.element("link").getStringValue();
		try
		{
			System.out.println("redirecting to: [" + link + "]");
			Util.redirect(link);
		}
		catch (IOException e)
		{
			throw new IllegalResponseException(e);
		}
	}
	

	/* (non-Javadoc)
	 * @see de.l3s.interweb.InterWeb#buildSignature(java.lang.String, java.util.TreeMap)
	 */
	@Override
	public String buildSignature(String path, TreeMap<String, String> params)
	{
		return null;
	}
	

	/* (non-Javadoc)
	 * @see de.l3s.interweb.InterWeb#clone()
	 */
	@Override
	public InterWeb clone()
	{
		System.out.println("InterWebJImpl.clone() iwToken is null");
		return new InterWebJImpl(getInterwebApiURL(),
		                         getConsumerKey(),
		                         getConsumerSecret());
	}
	

	public void deleteToken()
	{
		setIWToken(null);
	}
	

	@Override
	public AuthCredentials getAccessToken(AuthCredentials authCredentials)
	    throws IllegalResponseException
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
	

	/* (non-Javadoc)
	 * @see de.l3s.interweb.InterWeb#getAuthorizationInformation(boolean)
	 */
	@Override
	public synchronized AuthorizationInformation getAuthorizationInformation(boolean useCache)
	    throws IOException, IllegalResponseException
	{
		if (null == getIWToken())
		{
			return null;
		}
		if (!useCache || isAuthorizationCacheTimedOut(6000))
		{
			WebResource resource = createWebResource("users/default/services",
			                                         getIWToken());
			System.out.println(getIWToken());
			ClientResponse response = resource.get(ClientResponse.class);
			CoreUtils.printClientResponse(response);
			AuthorizationInformation cachedAuthorizationInformation = new AuthorizationInformation(response.getEntityInputStream());
			setCachedAuthorizationInformation(cachedAuthorizationInformation);
			updateAuthorizationCache();
			return cachedAuthorizationInformation;
		}
		return getCachedAuthorizationInformation();
	}
	

	/* (non-Javadoc)
	 * @see de.l3s.interweb.InterWeb#getAuthorizeUrl(java.lang.String)
	 */
	@Override
	public String getAuthorizeUrl(String callback)
	    throws IllegalResponseException
	{
		System.out.println("callback: [" + callback + "]");
		WebResource resource = createPublicWebResource("oauth/OAuthGetRequestToken");
		ClientResponse response = resource.get(ClientResponse.class);
		Element root = asXML(response);
		if (!root.attribute("stat").getValue().equals("ok"))
		{
			throw new IllegalResponseException(root.asXML());
		}
		String iw_token = root.element("request_token").element("oauth_token").getStringValue();
		return getInterwebApiURL() + "oauth/OAuthAuthorizeToken"
		       + "?oauth_token=" + iw_token + "&oauth_callback=" + callback;
	}
	

	/* (non-Javadoc)
	 * @see de.l3s.interweb.InterWeb#getEmbedded(java.lang.String)
	 */
	@Override
	public Embedded getEmbedded(String href, int maxWidth, int maxHeight)
	    throws IllegalResponseException
	{
		WebResource resource = createWebResource("embedded", getIWToken());
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		params.add("url", href);
		params.add("max_width", Integer.toString(maxWidth));
		params.add("max_height", Integer.toString(maxHeight));
		System.out.println(params);
		ClientResponse response = resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class,
		                                                                                    params);
		CoreUtils.printClientResponse(response);
		Element root = asXML(response);
		if (!root.attribute("stat").getValue().equals("ok"))
		{
			throw new IllegalResponseException(root.asXML());
		}
		String embedded = root.element("embedded").getStringValue();
		System.out.println(embedded);
		embedded = StringEscapeUtils.unescapeHtml(embedded);
		System.out.println(embedded);
		return new Embedded(embedded);
	}
	

	/**
	 * 
	 * @return The URL that was used for the last get request
	 * @deprecated exists only for debugging
	 */
	@Deprecated
	public String getGetUrl()
	{
		return getUrl;
	}
	

	/* (non-Javadoc)
	 * @see de.l3s.interweb.InterWeb#getUsername()
	 */
	@Override
	public synchronized String getUsername()
	    throws IllegalResponseException
	{
		if (null == getIWToken())
		{
			return null;
		}
		if (isUsernameCacheTimedOut(6000)) // cached value older then 100 minutes
		{
			WebResource resource = createWebResource("users/default",
			                                         getIWToken());
			ClientResponse response = resource.get(ClientResponse.class);
			String content;
			try
			{
				content = CoreUtils.getClientResponseContent(response);
			}
			catch (IOException e)
			{
				throw new IllegalResponseException(e);
			}
			Element root = null;
			try
			{
				root = asXML(content);
			}
			catch (IllegalResponseException e)
			{
				// do nothing
			}
			if (root != null && !root.attributeValue("stat").equals("ok"))
			{
				throw new IllegalResponseException(root.asXML());
			}
			setCachedUsername(content);
			updateUsernameCache();
		}
		return getCachedUsername();
	}
	

	/* (non-Javadoc)
	 * @see de.l3s.interweb.InterWeb#registerUser(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public AuthCredentials registerUser(String username,
	                                    String password,
	                                    String defaultUserName,
	                                    String defaultPassword)
	    throws IllegalResponseException
	{
		MultivaluedMapImpl params = new MultivaluedMapImpl();
		params.add("username", username);
		params.add("password", password);
		if (StringUtils.isNotEmpty(defaultUserName))
		{
			params.add("mediator_username", defaultUserName);
		}
		if (StringUtils.isNotEmpty(defaultPassword))
		{
			params.add("mediator_password", defaultPassword);
		}
		WebResource resource = createPublicWebResource("oauth/register");
		ClientResponse response = resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class,
		                                                                                    params);
		Element root = asXML(response);
		System.out.println(root.asXML());
		if (root.attributeValue("stat").equals("ok"))
		{
			Element element = root.element("access_token");
			String token = element.element("oauth_token").getStringValue();
			String tokenSecret = element.element("oauth_token_secret").getStringValue();
			return new AuthCredentials(token, tokenSecret);
		}
		if (Integer.parseInt(root.element("error").attributeValue("code")) == 110)
		{
			return null;
		}
		throw new IllegalResponseException(root.asXML());
	}
	

	/* (non-Javadoc)
	 * @see de.l3s.interweb.InterWeb#revokeAuthorizationOnService(java.lang.String)
	 */
	@Override
	public void revokeAuthorizationOnService(String serviceId)
	    throws IOException, IllegalResponseException
	{
		WebResource resource = createWebResource("users/default/services/"
		                                             + serviceId + "/auth",
		                                         getIWToken());
		ClientResponse response = resource.delete(ClientResponse.class);
		Element root = asXML(response);
		if (!root.attributeValue("stat").equals("ok"))
		{
			throw new IllegalResponseException(root.asXML());
		}
		//force reload
		resetAuthorizationCache();
	}
	

	/**
	 * This is just a shortcut for SearchQuery search(String query,
	 * TreeMap<String,String> params) with default parameters
	 * 
	 * @param query The query string
	 * @return
	 * @throws IllegalResponseException
	 * @throws SAXException
	 * @throws IOException
	 */
	public SearchQuery search(String query)
	    throws IllegalResponseException, IOException
	{
		WebResource resource = createWebResource("search", getIWToken());
		resource = resource.queryParam("q", query);
		resource = resource.queryParam("media_types", "image,video,audio,text");
		resource = resource.queryParam("number_of_results", "2");
		ClientResponse response = resource.get(ClientResponse.class);
		SearchQuery searchQuery = new SearchQuery();
		searchQuery.parse(response.getEntityInputStream());
		return searchQuery;
	}
	

	/* (non-Javadoc)
	 * @see de.l3s.interweb.InterWeb#search(java.lang.String, java.util.TreeMap)
	 */
	@Override
	public SearchQuery search(String query, TreeMap<String, String> params)
	    throws IOException, IllegalResponseException
	{
		if (null == query || query.length() == 0)
		{
			throw new IllegalArgumentException("empty query");
		}
		//		convertMediaTypesParams(params);
		System.out.println("iwToken: [" + getIWToken() + "]");
		WebResource resource = createWebResource("search", getIWToken());
		resource = resource.queryParam("q", query);
		for (String key : params.keySet())
		{
			String value = params.get(key);
			resource = resource.queryParam(key, value);
		}
		ClientResponse response = resource.get(ClientResponse.class);
		SearchQuery searchQuery = new SearchQuery();
		searchQuery.parse(response.getEntityInputStream());
		return searchQuery;
	}
	

	@Override
	public InputStream searchAsXML(String query, TreeMap<String, String> params)
	    throws IOException, IllegalResponseException
	{
		
		if (null == query || query.length() == 0)
		{
			throw new IllegalArgumentException("empty query");
		}
		//		convertMediaTypesParams(params);
		System.out.println("iwToken: [" + getIWToken() + "]");
		WebResource resource = createWebResource("search", getIWToken());
		resource = resource.queryParam("q", query);
		for (String key : params.keySet())
		{
			String value = params.get(key);
			resource = resource.queryParam(key, value);
		}
		ClientResponse response = resource.get(ClientResponse.class);
		InputStream in = response.getEntityInputStream();
		
		/*String myString = response.getLanguage();
		ByteArrayInputStream in = new ByteArrayInputStream(myString.getBytes());
		*/
		/*InputSource is = new InputSource();
		is.setByteStream(in);
		*/
		return in;
		
	}
	

	public void setMediator()
	{
		
	}
	

	private Element asXML(ClientResponse response)
	    throws IllegalResponseException
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
	

	private Element asXML(String content)
	    throws IllegalResponseException
	{
		Document doc;
		try
		{
			ByteArrayInputStream is = new ByteArrayInputStream(content.getBytes(Charset.forName("UTF8")));
			doc = new SAXReader().read(is);
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
	

	private void convertMediaTypesParams(TreeMap<String, String> params)
	{
		Set<String> newMediaTypes = new TreeSet<String>();
		String mediaTypeParam = params.get("media_types");
		if (mediaTypeParam == null)
		{
			return;
		}
		String[] mediaTypes = mediaTypeParam.split(",");
		for (String mediaType : mediaTypes)
		{
			if (mediaType.equals("photos"))
			{
				newMediaTypes.add(Query.CT_IMAGE);
			}
			else if (mediaType.equals("videos"))
			{
				newMediaTypes.add(Query.CT_VIDEO);
			}
			else if (mediaType.equals("slideshows"))
			{
				newMediaTypes.add(Query.CT_IMAGE);
			}
			else if (mediaType.equals("audio"))
			{
				newMediaTypes.add(Query.CT_AUDIO);
			}
			else if (mediaType.equals("music"))
			{
				newMediaTypes.add(Query.CT_AUDIO);
			}
			else if (mediaType.equals("bookmarks"))
			{
				newMediaTypes.add(Query.CT_TEXT);
			}
		}
		params.put("media_types", StringUtils.join(newMediaTypes, ','));
	}
	
}
