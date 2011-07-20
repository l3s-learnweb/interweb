package de.l3s.interweb;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.TreeMap;

import org.xml.sax.SAXException;


import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import de.l3s.interweb.AuthorizationInformation.ServiceInformation;
import de.l3s.interwebj.AuthCredentials;

public abstract class InterWeb {

	public static final int BLOGGER = 0;
	public static final int DELICIOUS = 1;
	public static final int FACEBOOK = 2;
	public static final int FLICKR = 3;
	public static final int GROUPME = 4;
	public static final int IPERNITY = 5;
	public static final int LASTFM = 6;
	public static final int SLIDESHARE = 7;
	public static final int VIMEO = 8;
	public static final int YOUTUBE = 9;
	public static final int BIBSONOMY = 10;

	private final String consumerKey;
	private final String consumerSecret;
	private final String interwebApiURL;

	private int usernameLastCacheTime = 0;
	private int authorizationInformationLastCacheTime = 0;

	private String cachedUsername;
	private AuthorizationInformation cachedAuthorizationInformation;

	private AuthCredentials iwToken = null;
	private int serviceInformationListCacheTime;
	private List<ServiceInformation> serviceInformationListCache;

	public InterWeb(String interwebApiURL, String consumerKey,
			String consumerSecret) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.interwebApiURL = interwebApiURL;
	}

	public abstract void authorizeService(ServiceInformation service,
			String callback) throws IllegalResponseException;

	public abstract String buildSignature(String path,
			TreeMap<String, String> params);

	/**
	 * Creates a new instance of interweb with the same consumer key and secret
	 */
	@Override
	public abstract InterWeb clone();

	public abstract AuthCredentials getAccessToken(
			AuthCredentials authCredentials) throws IllegalResponseException;

	public int getAuthorizationCache() {
		return authorizationInformationLastCacheTime;
	}

	/**
	 * 
	 * @param useCache
	 * @return
	 * @throws IOException
	 * @throws IllegalResponseException
	 */
	public abstract AuthorizationInformation getAuthorizationInformation(
			boolean useCache) throws IOException, IllegalResponseException;

	public abstract String getAuthorizeUrl(String callback)
			throws IllegalResponseException;

	public AuthorizationInformation getCachedAuthorizationInformation() {
		return cachedAuthorizationInformation;
	}

	public String getCachedUsername() {
		return cachedUsername;
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public abstract Embedded getEmbedded(String href, int maxWidth,
			int maxHeight) throws IllegalResponseException;

	public String getInterwebApiURL() {
		return interwebApiURL;
	}

	public AuthCredentials getIWToken() {
		return iwToken;
	}

	public abstract String getUsername() throws IllegalResponseException;

	public int getUsernameCache() {
		return usernameLastCacheTime;
	}

	public boolean isAuthorizationCacheTimedOut(int idleTime) {
		return authorizationInformationLastCacheTime < Util.time() - idleTime;
	}

	public boolean isUsernameCacheTimedOut(int idleTime) {
		return usernameLastCacheTime < Util.time() - idleTime;
	}

	/**
	 * Registers a new user at interweb and returns his interweb_token
	 * 
	 * @param username
	 * @param password
	 * @param defaultToken
	 *            Returns null if username is already taken
	 * @return
	 * @throws IllegalResponseException
	 */
	public abstract AuthCredentials registerUser(String username,
			String password, String defaultUserName, String defaultPassword)
			throws IllegalResponseException;

	public void resetAuthorizationCache() {
		authorizationInformationLastCacheTime = 0;
	}

	public void resetUsernameCache() {
		usernameLastCacheTime = 0;
	}

	public abstract void revokeAuthorizationOnService(String serviceId)
			throws IOException, IllegalResponseException;

	/**
	 * 
	 * @param query
	 *            The query string
	 * @param params
	 *            see http://athena.l3s.uni-hannover.de:8000/doc/search
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws IllegalResponseException
	 */
	public abstract SearchQuery search(String query,
			TreeMap<String, String> params) throws IOException,
			IllegalResponseException;

	/**
	 * 
	 * @param query
	 *            The query string
	 * @param params
	 *            see http://athena.l3s.uni-hannover.de:8000/doc/search
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws IllegalResponseException
	 */
	public abstract InputStream searchAsXML(String query,
			TreeMap<String, String> params) throws IOException,
			IllegalResponseException;
	
	public void setCachedAuthorizationInformation(
			AuthorizationInformation cachedAuthorizationInformation) {
		this.cachedAuthorizationInformation = cachedAuthorizationInformation;
	}

	public void setCachedUsername(String cachedUsername) {
		this.cachedUsername = cachedUsername;
	}

	public void setIWToken(AuthCredentials iwToken) {
		System.out.println("Setting new iwToken: [" + iwToken + "]");
		this.iwToken = iwToken;
		// force to reload
		resetAuthorizationCache();
		resetUsernameCache();
	}

	public void updateAuthorizationCache() {
		authorizationInformationLastCacheTime = Util.time();
	}

	public void updateUsernameCache() {
		usernameLastCacheTime = Util.time();
	}

	public WebResource createWebResource(String apiPath,
			AuthCredentials userAuthCredentials) {
		String apiUrl = getInterwebApiURL() + apiPath;
		AuthCredentials consumerAuthCredentials = new AuthCredentials(
				getConsumerKey(), getConsumerSecret());
		return Util.createWebResource(apiUrl, consumerAuthCredentials,
				userAuthCredentials);
	}

	protected WebResource createPublicWebResource(String apiPath) {
		return createWebResource(apiPath, null);
	}

	public synchronized List<ServiceInformation> getServiceInformation(
			boolean useCache) throws IllegalResponseException {

		if (serviceInformationListCacheTime < Util.time() - 86500) {
			WebResource resource = createPublicWebResource("services");

			ClientResponse response = resource.get(ClientResponse.class);
			AuthorizationInformation temp = new AuthorizationInformation(
					response.getEntityInputStream());

			serviceInformationListCache = temp.getServices();
			serviceInformationListCacheTime = Util.time();
		}
		return serviceInformationListCache;
	}
}
