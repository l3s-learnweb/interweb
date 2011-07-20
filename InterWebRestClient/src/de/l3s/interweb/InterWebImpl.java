package de.l3s.interweb;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import com.Ostermiller.util.Base64;

import de.l3s.interweb.AuthorizationInformation.ServiceInformation;
import de.l3s.interwebj.AuthCredentials;
import de.l3s.util.MD5;

public class InterWebImpl extends InterWeb {

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
	
	public static boolean DEBUG = true;
	
	private final static int timeoutMs = 20000;

	private String getUrl;
	
	
	public InterWebImpl(String interwebApiURL, String consumerKey, String consumerSecret)
	{
		super(interwebApiURL, consumerKey, consumerSecret);
	}
	
	
	@Override
	public void authorizeService(ServiceInformation service, String callback)
	    throws IllegalResponseException
	{
		// do nothing
	}
	
	@Override
	public AuthCredentials getAccessToken(AuthCredentials authCredentials)
	    throws IllegalResponseException
	{
		return null;
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
	
	public void deleteToken()
	{
		setIWToken(null);
	}	
	
	
	/**
	 * Creates a new instance of interweb with the same consumer key and secret
	 */
	@Override
	public InterWebImpl clone()
	{
		return new InterWebImpl(getInterwebApiURL(),
		                        getConsumerKey(),
		                        getConsumerSecret());
	}

	@Override
	public String buildSignature(String path, TreeMap<String,String> params) 
	{		
		params.put("iw_path", path);		
		StringBuffer signature = new StringBuffer(getConsumerSecret());
		
		for(Iterator<Entry<String, String>> i = params.entrySet().iterator(); i.hasNext();)
		{
			Entry<String, String> entry = i.next();
			if(!entry.getKey().equals("data"))
			{
				signature.append(entry.getKey());
				signature.append(entry.getValue());
			}
		}
		params.remove("iw_path");
		
		return MD5.hash(signature.toString());
	}
	
	private String buildGetUrl(String path, TreeMap<String,String> params, boolean authenticated, String format) 
	{
		if(null == params)
			params = new TreeMap<String, String>();
		AuthCredentials iwToken = getIWToken();
		if (authenticated && iwToken != null)
		{
			params.put("iw_token", iwToken.getKey());
		}

		params.put("iw_consumer_key", getConsumerKey());
		params.put("iw_signature", buildSignature(path, params));

		return this.getInterwebApiURL() + path + "." + format + "?" + getParameterString(params);
	}

	/**
	 * The same as get(...). Returns a Stream instead of a String.
	 * @see #get(String, TreeMap, boolean, String)
	 */
	public InputStream getInputStream(String path, TreeMap<String,String> params, boolean authenticated, String format) throws IOException 
	{
		getUrl = buildGetUrl(path, params, authenticated, format);
		
		System.out.println("geturl "+getUrl);
		try {
			URLConnection conn = new URL(getUrl).openConnection();
			// setting these timeouts ensures the client does not deadlock indefinitely
			// when the server has problems.
			conn.setConnectTimeout(timeoutMs);
			conn.setReadTimeout(timeoutMs);
			return conn.getInputStream();
		} 
		catch (MalformedURLException e) {	
			// should never happen
			throw new RuntimeException(e);
		}		
	}
	
	public InputStream postInputStream(String path, TreeMap<String,String> params, boolean authenticated, String format) throws IOException 
	{
		getUrl = buildGetUrl(path, params, authenticated, format);
		
		System.out.println("geturl "+getUrl);
		try {
			URLConnection conn = new URL(getUrl).openConnection();
			// setting these timeouts ensures the client does not deadlock indefinitely
			// when the server has problems.
			conn.setConnectTimeout(timeoutMs);
			conn.setReadTimeout(timeoutMs);
			return conn.getInputStream();
		} 
		catch (MalformedURLException e) {	
			// should never happen
			throw new RuntimeException(e);
		}		
	}
	
	private Element getXML(String path, TreeMap<String,String> params, boolean authenticated) throws IllegalResponseException
	{
		Document doc;
		try {
			doc = new SAXReader().read(getInputStream(path, params, authenticated, "xml"));
		}
		catch (Exception e) {
			throw new IllegalResponseException(e.getMessage());
		}
		
		Element root = doc.getRootElement();
		
		if(!root.attributeValue("stat").equals("ok"))
			throw new IllegalResponseException(root.asXML());
		
		return root;
	}
	
	public InputStream delete(String path, TreeMap<String,String> params, boolean authenticated, String format) throws IOException 
	{	
		try {
			URL url = new URL(buildGetUrl(path, params, authenticated, format));

			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setDoOutput(true);
			httpCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded" );
			httpCon.setRequestMethod("DELETE");

			return httpCon.getInputStream();		
		} 
		catch (MalformedURLException e) {
			// should never happen
			throw new RuntimeException(e);
		} 
	}
		
	@Override
	public void revokeAuthorizationOnService(String serviceId) throws IOException, IllegalResponseException
	{		
		try {
			Document doc = new SAXReader().read(delete("users/default/services/" + serviceId + "/auth", null, true, "xml"));
			Element root = doc.getRootElement();
			if(!root.attributeValue("stat").equals("ok"))
				throw new IllegalResponseException(root.asXML());
			
		} catch (DocumentException e) {
			throw new IllegalResponseException(e.getMessage());
		}
		
		//force reload
		resetAuthorizationCache();
	}
	
	/**
	 * This is just a shortcut for 
	 * SearchQuery search(String query, TreeMap<String,String> params)
	 * with default parameters
	 * 
	 * @param query The query string
	 * @return 
	 * @throws IllegalResponseException 
	 * @throws SAXException 
	 * @throws IOException 
	 */
	public SearchQuery search(String query) throws IllegalResponseException, IOException 
	{
		TreeMap<String,String> params = new TreeMap<String, String>();
		params.put("q", query);
		params.put("media_types", "photos,videos,audio,music,bookmarks");
		params.put("number_of_results", "2");		

		SearchQuery searchQuery = new SearchQuery();
		searchQuery.parse(getInputStream("search", params, true, "xml"));
		return searchQuery;
	}
	/**
	 * 
	 * @param query The query string
	 * @param params see http://athena.l3s.uni-hannover.de:8000/doc/search
	 * @return returns always the same instance of SearchQuery
	 * @throws SAXException 
	 * @throws IOException 
	 */
	public InputStream searchAsXML(String query, TreeMap<String,String> params) throws IOException,
	IllegalResponseException{
		params.put("q", query);
		Date now=new Date();
		return getInputStream("search", params, true, "xml");
	}
	/**
	 * 
	 * @param query The query string
	 * @param params see http://athena.l3s.uni-hannover.de:8000/doc/search
	 * @return 
	 * @throws SAXException 
	 * @throws IOException 
	 * @throws IllegalResponseException 
	 */
	@Override
	public SearchQuery search(String query, TreeMap<String,String> params) throws IOException, IllegalResponseException
	{
		if(null == query || query.length() == 0)
			throw new IllegalArgumentException("empty query");
		
		params.put("q", query);
		
		SearchQuery searchQuery = new SearchQuery();
		searchQuery.parse(getInputStream("search", params, true, "xml"));		
		return searchQuery;
	}
	
	private TreeMap<String,String> signParameters(String path, TreeMap<String,String> params,boolean authenticated)
	{
		AuthCredentials iwToken = getIWToken();
		if (authenticated && iwToken != null)
		{
			params.put("iw_token", iwToken.getKey());
		}

		params.put("iw_consumer_key", getConsumerKey());
		params.put("iw_signature", buildSignature(path, params));
		return params;
	}


	public Element post(String path, TreeMap<String, String> params, boolean isfile, boolean authenticated, String format) throws IllegalResponseException 
	{		
		params=signParameters(path,params,authenticated);
		String paramString=getParameterString(params);

	    try{

	    	URL url = new URL (getInterwebApiURL() + path + "." + format);
			
			// URL connection channel.
			URLConnection urlConn = url.openConnection();
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);
			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			
			/*
			if(isfile)
			{
				printout=sendFile((HttpURLConnection)urlConn, params);
			}
			
			else{*/
			// Send POST output.
			DataOutputStream writer = new DataOutputStream(urlConn.getOutputStream());
			//}  
			   
			writer.writeBytes(paramString);
			writer.flush();
			writer.close();
			// Get response data.
			return  new SAXReader().read(urlConn.getInputStream()).getRootElement();
	    }
	    catch(IOException e) {
	    	throw new IllegalResponseException(e.getMessage());
	    } catch (DocumentException e) {
	    	throw new IllegalResponseException(e.getMessage());
		}
	}
	/*
private DataOutputStream sendFile(HttpURLConnection conn, TreeMap<String, String> params)
{
	String boundary =  "*****";
	 String lineEnd = "\r\n";
	  String twoHyphens = "--";
	  byte[] buffer;

	  int maxBufferSize = 1*1024*1024;

	 try
	  {
	   //------------------ CLIENT REQUEST

	   FileInputStream fileInputStream = new FileInputStream( params.get("data"));

	   // open a URL connection to the Servlet 



	   // Allow Inputs
	   conn.setDoInput(true);

	   // Allow Outputs
	   conn.setDoOutput(true);

	   // Don't use a cached copy.
	   conn.setUseCaches(false);

	   // Use a post method.
	   conn.setRequestMethod("POST");

	   conn.setRequestProperty("Connection", "Keep-Alive");
	  
	   conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

	   DataOutputStream dos = new DataOutputStream( conn.getOutputStream() );

	   dos.writeBytes(twoHyphens + boundary + lineEnd);
	   dos.writeBytes("Content-Disposition: form-data; name=\"upload\";"
	      + " filename=\"" + params.get("data") +"\"" + lineEnd);
	   dos.writeBytes(lineEnd);

	   

	   // create a buffer of maximum size

	   int bytesAvailable = fileInputStream.available();
	   int bufferSize = Math.min(bytesAvailable, maxBufferSize);
	   buffer = new byte[bufferSize];

	   // read file and write it into form...

	   int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

	   while (bytesRead > 0)
	   {
	    dos.write(buffer, 0, bufferSize);
	    bytesAvailable = fileInputStream.available();
	    bufferSize = Math.min(bytesAvailable, maxBufferSize);
	    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	   }

	   // send multipart form data necesssary after file data...

	   dos.writeBytes(lineEnd);
	   dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

	   // close streams

	   fileInputStream.close();
	  return dos;


	  }
	  catch (MalformedURLException ex)
	  {
	   System.out.println("From ServletCom CLIENT REQUEST:"+ex);
	  }

	  catch (IOException ioe)
	  {
	   System.out.println("From ServletCom CLIENT REQUEST:"+ioe);
	  }
return null;
}
*/
	private static String getParameterString(TreeMap<String, String> params) 
	{
		StringBuffer ret=new StringBuffer();
		for(Entry<String, String> entry : params.entrySet())
		{
			if(!entry.getKey().equals("data"))
			{
				ret.append(entry.getKey());
				ret.append("=");
				
				try {
					ret.append(URLEncoder.encode(entry.getValue(),"UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// Should never happen, becouse UTF-8
					e.printStackTrace();
				}
				ret.append("&");
			}
		}
		return ret.toString();
	}
	
	@Override
	public String getAuthorizeUrl(String callback) throws IllegalResponseException 
	{
		String iw_token = null;
		
		Element root = getXML("auth/request_token", null, false);
		
		if(root.attribute("stat").getValue().equals("ok"))
			iw_token = root.element("token").attribute("token").getStringValue();
		else
			throw new IllegalResponseException(root.asXML());			
		
		TreeMap<String,String> params = new TreeMap<String, String>();
		params.put("iw_token", iw_token);
		params.put("iw_consumer_key", getConsumerKey());
		params.put("iw_signature", buildSignature("auth/authorize", params));
		params.put("callback", callback);

		return getInterwebApiURL() + "auth/authorize" + "?" + getParameterString(params);
	}
	
	@Override
	public synchronized String getUsername() throws IllegalResponseException
	{
		if(null == getIWToken())
			return null;
		
		if (isUsernameCacheTimedOut(6000)) // cached value older then 100 minutes
		{
			Element root = getXML("users/default", null, true);
			
			if (!root.attributeValue("stat").equals("ok"))
			{
				throw new IllegalResponseException(root.asXML());
			}
			String cachedUsername = root.elementText("user");
			setCachedUsername(cachedUsername);
			updateUsernameCache();
		}
		return getCachedUsername();
	}
	
	/**
	 * Registers a new user at interweb and returns his interweb_token
	 * 
	 * @param username
	 * @param password
	 * @param defaultToken Returns null if username is already taken
	 * @return
	 * @throws IllegalResponseException
	 */
	@Override
	public AuthCredentials registerUser(String username, String password, String defaultUserName, String defaultPassword) throws IllegalResponseException
	{
		TreeMap<String,String> params = new TreeMap<String, String>();
		params.put("username", username);
		params.put("password", password);
		// TODO: 
//		if(null != defaultToken && defaultToken.length() > 0)
//			params.put("default_token", defaultToken);			
 
		Element root = post("auth/register", params, false, false, "xml");
		
		System.out.println(root.asXML());
		
		if(root.attributeValue("stat").equals("ok"))
		{
			return new AuthCredentials(root.element("token").attributeValue("token"));	
		}
		else
		{
			if(Integer.parseInt(root.element("error").attributeValue("code")) == 110) // username already taken
				return null;
		
			throw new IllegalResponseException(root.asXML());	
		}		
	}
	
	/**
	 * 
	 * @param useCache
	 * @return
	 * @throws IOException
	 * @throws IllegalResponseException
	 */
	@Override
	public synchronized AuthorizationInformation getAuthorizationInformation(boolean useCache) throws IOException, IllegalResponseException
	{
		if(null == getIWToken())
			return null;
		
		if(!useCache || isAuthorizationCacheTimedOut(6000))
		{
			AuthorizationInformation cachedAuthorizationInformation = new AuthorizationInformation(getInputStream("users/default/services",
			                                                                                                      null,
			                                                                                                      true,
			                                                                                                      "xml"));
			setCachedAuthorizationInformation(cachedAuthorizationInformation);
			updateAuthorizationCache();
			return cachedAuthorizationInformation;
		}
		return getCachedAuthorizationInformation();
	}


	@Override
	public Embedded getEmbedded(String href, int maxWidth, int maxHeight) throws IllegalResponseException 
	{
		TreeMap<String,String> params = new TreeMap<String, String>();
		params.put("url", Base64.encode(href));
		
		return new Embedded(getXML("embedded", params, false));		
	}

	
}
