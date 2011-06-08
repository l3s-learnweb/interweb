package de.l3s.interwebj.connector.interweb;


import static de.l3s.interwebj.util.Assertions.*;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;
import java.util.List;

import javax.ws.rs.core.*;

import org.apache.commons.codec.digest.*;

import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.*;
import com.sun.jersey.core.util.*;
import com.sun.jersey.multipart.*;
import com.sun.jersey.multipart.file.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.config.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.query.*;
import de.l3s.interwebj.query.Query.SearchScope;
import de.l3s.interwebj.query.Query.SortOrder;
import de.l3s.interwebj.util.*;


public class InterWebConnector
    extends AbstractServiceConnector
{
	
	private static final String API_REQUEST_TOKEN_PATH = "auth/request_token";
	private static final String API_EMBEDDED_PATH = "embedded";
	private static final String API_AUTHORIZE_TOKEN_PATH = "auth/authorize";
	private static final String API_SEARCH_PATH = "search";
	private static final String API_UPLOAD_PATH = "users/default/uploads";
	private static final String API_CURRENT_USER_INFO = "users/default";
	

	public InterWebConnector(Configuration configuration)
	{
		this(configuration, null);
	}
	

	public InterWebConnector(Configuration configuration,
	                         AuthCredentials consumerAuthCredentials)
	{
		super(configuration);
		setAuthCredentials(consumerAuthCredentials);
	}
	

	@Override
	public Parameters authenticate(String callbackUrl)
	    throws InterWebException
	{
		if (!isConnectorRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		AuthCredentials consumerAuthCredentials = getAuthCredentials();
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		UriBuilder uriBuilder = UriBuilder.fromUri(getBaseUrl());
		uriBuilder.path("api").path(API_REQUEST_TOKEN_PATH + ".xml");
		Parameters params = new Parameters();
		params.add("iw_consumer_key", consumerAuthCredentials.getKey());
		String iwSignature = generateSignature(API_REQUEST_TOKEN_PATH, params);
		params.add("iw_signature", iwSignature);
		addQueryParameters(uriBuilder, params);
		WebResource resource = client.resource(uriBuilder.build());
		WebResource.Builder builder = resource.accept(javax.ws.rs.core.MediaType.TEXT_XML);
		ClientResponse response = builder.get(ClientResponse.class);
		System.out.println(response);
		if (response.getStatus() != 200)
		{
			throw new InterWebException(response.toString());
		}
		IWRequestTokenResponse requestTokenResponse = response.getEntity(IWRequestTokenResponse.class);
		if (IWXMLResponse.FAILED.equals(requestTokenResponse.getStat()))
		{
			IWErrorEntity error = requestTokenResponse.getError();
			throw new InterWebException("InterWeb service returned respose: "
			                            + error.getMessage() + " (error code "
			                            + error.getCode() + ")");
		}
		String requestToken = requestTokenResponse.getRequestToken().getToken();
		params = new Parameters();
		params.add("iw_token", requestToken);
		params.add("iw_consumer_key", consumerAuthCredentials.getKey());
		iwSignature = generateSignature(API_AUTHORIZE_TOKEN_PATH, params);
		params.add("iw_signature", iwSignature);
		params.add("callback", callbackUrl);
		uriBuilder = UriBuilder.fromUri(getBaseUrl());
		uriBuilder.path("api").path(API_AUTHORIZE_TOKEN_PATH);
		addQueryParameters(uriBuilder, params);
		params.add(Parameters.AUTHORIZATION_URL,
		           uriBuilder.build().toASCIIString());
		return params;
	}
	

	@Override
	public ServiceConnector clone()
	{
		return new InterWebConnector(getConfiguration(), getAuthCredentials());
	}
	

	@Override
	public AuthCredentials completeAuthentication(Parameters params)
	    throws InterWebException
	{
		notNull(params, "params");
		AuthCredentials authCredentials = null;
		if (!isConnectorRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		String iwToken = params.get("iw_token");
		if (iwToken != null)
		{
			authCredentials = new AuthCredentials(iwToken);
		}
		return authCredentials;
	}
	

	@Override
	public QueryResult get(Query query, AuthCredentials authCredentials)
	    throws InterWebException
	{
		notNull(query, "query");
		if (!isConnectorRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		QueryResult queryResult = new QueryResult(query);
		if (authCredentials == null)
		{
			return queryResult;
		}
		AuthCredentials consumerAuthCredentials = getAuthCredentials();
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		UriBuilder uriBuilder = UriBuilder.fromUri(getBaseUrl());
		uriBuilder.path("api").path(API_SEARCH_PATH + ".xml");
		Parameters params = new Parameters();
		params.add("iw_consumer_key", consumerAuthCredentials.getKey());
		params.add("iw_token", authCredentials.getKey());
		params.add("q", query.getQuery());
		params.add("search_in", getSearchIn(query.getSearchScopes()));
		params.add("media_types", getMediaTypes(query.getContentTypes()));
		params.add("number_of_results", String.valueOf(query.getResultCount()));
		params.add("ranking", getRanking(query.getSortOrder()));
		params.add("date_from", query.getParam("date_from", ""));
		params.add("date_till", query.getParam("date_till", ""));
		params.add("services", getServices());
		String iwSignature = generateSignature(API_SEARCH_PATH, params);
		params.add("iw_signature", iwSignature);
		addQueryParameters(uriBuilder, params);
		WebResource resource = client.resource(uriBuilder.build());
		WebResource.Builder builder = resource.accept(javax.ws.rs.core.MediaType.TEXT_XML);
		ClientResponse response = builder.get(ClientResponse.class);
		if (response.getStatus() != 200)
		{
			throw new InterWebException(response.toString());
		}
		IWSearchResponse searchResponse = response.getEntity(IWSearchResponse.class);
		if (IWXMLResponse.FAILED.equals(searchResponse.getStat()))
		{
			IWErrorEntity error = searchResponse.getError();
			throw new InterWebException("InterWeb service returned respose: "
			                            + error.getMessage() + " (error code "
			                            + error.getCode() + ")");
		}
		parseSearchResponse(queryResult, searchResponse);
		System.out.println("[InterWeb] query time elapsed: ["
		                   + searchResponse.getQuery().getElapsedTime() + "]");
		return queryResult;
	}
	

	@Override
	public String getEmbedded(AuthCredentials authCredentials,
	                          String url,
	                          int maxWidth,
	                          int maxHeight)
	    throws InterWebException
	{
		AuthCredentials consumerAuthCredentials = getAuthCredentials();
		Client client = Client.create();
		UriBuilder uriBuilder = UriBuilder.fromUri(getBaseUrl());
		uriBuilder.path("api").path(API_EMBEDDED_PATH + ".xml");
		Parameters params = new Parameters();
		params.add("url", new String(Base64.encode(url)));
		params.add("iw_consumer_key", consumerAuthCredentials.getKey());
		String iwSignature = generateSignature(API_EMBEDDED_PATH, params);
		params.add("iw_signature", iwSignature);
		addQueryParameters(uriBuilder, params);
		WebResource resource = client.resource(uriBuilder.build());
		WebResource.Builder builder = resource.accept(MediaType.TEXT_XML);
		ClientResponse response = builder.get(ClientResponse.class);
		System.out.println(response);
		IWEmbeddedResponse iwEmbeddedResponse = response.getEntity(IWEmbeddedResponse.class);
		if (iwEmbeddedResponse.getStat().equals(IWXMLResponse.FAILED))
		{
			throw new InterWebException("URL: [" + url
			                            + "] doesn't belong to connector");
		}
		return iwEmbeddedResponse.getEmbedded();
	}
	

	@Override
	public String getUserId(AuthCredentials authCredentials)
	    throws InterWebException
	{
		UriBuilder uriBuilder = UriBuilder.fromUri(getBaseUrl());
		uriBuilder.path("api").path(API_CURRENT_USER_INFO + ".xml");
		Parameters params = new Parameters();
		params.add("iw_consumer_key", getAuthCredentials().getKey());
		params.add("iw_token", authCredentials.getKey());
		String iwSignature = generateSignature(API_CURRENT_USER_INFO, params);
		params.add("iw_signature", iwSignature);
		addQueryParameters(uriBuilder, params);
		Client client = Client.create();
		WebResource resource = client.resource(uriBuilder.build());
		WebResource.Builder builder = resource.accept(MediaType.APPLICATION_XML);
		ClientResponse response = builder.get(ClientResponse.class);
		IWUserResponse userResponse = response.getEntity(IWUserResponse.class);
		if (userResponse.getStat().equals(IWXMLResponse.FAILED))
		{
			IWErrorEntity error = userResponse.getError();
			throw new InterWebException("InterWeb service returned respose: "
			                            + error.getMessage() + " (error code "
			                            + error.getCode() + ")");
		}
		return userResponse.getUser().getUserName();
	}
	

	@Override
	public boolean isConnectorRegistrationRequired()
	{
		return true;
	}
	

	@Override
	public boolean isUserRegistrationDataRequired()
	{
		return false;
	}
	

	@Override
	public void put(byte[] data,
	                String contentType,
	                Parameters params,
	                AuthCredentials authCredentials)
	    throws InterWebException
	{
		notNull(data, "data");
		notNull(contentType, "contentType");
		notNull(params, "params");
		if (!isConnectorRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		if (authCredentials == null)
		{
			throw new InterWebException("Upload is forbidden for non-authorized users");
		}
		AuthCredentials consumerAuthCredentials = getAuthCredentials();
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		UriBuilder uriBuilder = UriBuilder.fromUri(getBaseUrl());
		uriBuilder.path("api").path(API_UPLOAD_PATH + ".xml");
		MultiPart multiPart = new MultiPart();
		Parameters iwParams = new Parameters();
		iwParams.add("iw_consumer_key", consumerAuthCredentials.getKey());
		iwParams.add("iw_token", authCredentials.getKey());
		if (params.containsKey(Parameters.TITLE))
		{
			String title = params.get(Parameters.TITLE);
			iwParams.add(Parameters.TITLE, title);
			multiPart = multiPart.bodyPart(new FormDataBodyPart(Parameters.TITLE,
			                                                    title));
		}
		if (params.containsKey(Parameters.DESCRIPTION))
		{
			String description = params.get(Parameters.DESCRIPTION);
			iwParams.add(Parameters.DESCRIPTION, description);
			multiPart = multiPart.bodyPart(new FormDataBodyPart(Parameters.DESCRIPTION,
			                                                    description));
		}
		if (params.containsKey(Parameters.TAGS))
		{
			String tags = params.get(Parameters.TAGS);
			iwParams.add(Parameters.TAGS, tags);
			multiPart = multiPart.bodyPart(new FormDataBodyPart(Parameters.TAGS,
			                                                    tags));
		}
		String privacy = params.get(Parameters.PRIVACY, "0");
		iwParams.add("is_private", privacy);
		multiPart = multiPart.bodyPart(new FormDataBodyPart("is_private",
		                                                    privacy));
		String iwSignature = generateSignature(API_UPLOAD_PATH, iwParams);
		iwParams.add("iw_signature", iwSignature);
		iwParams.remove(Parameters.TITLE);
		iwParams.remove(Parameters.DESCRIPTION);
		iwParams.remove(Parameters.TAGS);
		iwParams.remove("is_private");
		addQueryParameters(uriBuilder, iwParams);
		WebResource resource = client.resource(uriBuilder.build());
		System.out.println("uploading to interweb: " + resource.toString());
		File f = null;
		if (isFileUpload(contentType))
		{
			try
			{
				f = createTempFile(params.get(Parameters.FILENAME), data);
				multiPart.bodyPart(new FileDataBodyPart("data", f));
			}
			catch (IOException e)
			{
				e.printStackTrace();
				throw new InterWebException(e);
			}
		}
		else
		{
			String dataString = new String(data, Charset.forName("UTF-8"));
			multiPart = multiPart.bodyPart(new FormDataBodyPart("data",
			                                                    dataString));
		}
		WebResource.Builder builder = resource.type(javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA);
		builder = builder.accept(javax.ws.rs.core.MediaType.TEXT_XML);
		ClientResponse response = builder.post(ClientResponse.class, multiPart);
		if (f != null)
		{
			f.delete();
		}
		if (response.getStatus() != 200)
		{
			throw new InterWebException(response.toString());
		}
		// TODO: Remove.
		try
		{
			CoreUtils.printClientResponse(response);
			System.out.println(CoreUtils.getClientResponseContent(response));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	

	@Override
	public void revokeAuthentication()
	    throws InterWebException
	{
		// Interweb doesn't provide api for token revokation
	}
	

	private void addQueryParameters(UriBuilder uriBuilder, Parameters params)
	{
		for (String name : params.keySet())
		{
			if (!name.equals("data"))
			{
				uriBuilder.queryParam(name, params.get(name));
			}
		}
	}
	

	private File createTempFile(String fileName, byte[] data)
	    throws IOException
	{
		File file = new File("./tmp/" + fileName);
		file.getParentFile().mkdirs();
		FileOutputStream os = new FileOutputStream(file);
		os.write(data);
		os.close();
		return file;
	}
	

	@SuppressWarnings("unused")
	private String generateSignature(String path,
	                                 MultivaluedMap<String, String> params)
	{
		AuthCredentials consumerAuthCredentials = getAuthCredentials();
		params.add("iw_path", path);
		StringBuilder sb = new StringBuilder(consumerAuthCredentials.getSecret());
		Set<String> paramNames = new TreeSet<String>(params.keySet());
		for (String name : paramNames)
		{
			if (!name.equals("data"))
			{
				sb.append(name);
				sb.append(params.get(name));
			}
		}
		params.remove("iw_path");
		return DigestUtils.md5Hex(sb.toString().getBytes(Charset.forName("UTF-8")));
	}
	

	private String generateSignature(String path, Parameters params)
	{
		AuthCredentials consumerAuthCredentials = getAuthCredentials();
		params.add("iw_path", path);
		StringBuilder sb = new StringBuilder(consumerAuthCredentials.getSecret());
		Set<String> paramNames = new TreeSet<String>(params.keySet());
		for (String name : paramNames)
		{
			if (!name.equals("data"))
			{
				sb.append(name);
				sb.append(params.get(name));
			}
		}
		params.remove("iw_path");
		return DigestUtils.md5Hex(sb.toString().getBytes(Charset.forName("UTF-8")));
	}
	

	private String getMediaTypes(List<String> contentTypes)
	{
		Set<String> mediaTypes = new HashSet<String>();
		if (contentTypes.contains(Query.CT_IMAGE))
		{
			mediaTypes.add("photos");
			mediaTypes.add("slideshows");
		}
		if (contentTypes.contains(Query.CT_VIDEO))
		{
			mediaTypes.add("videos");
		}
		if (contentTypes.contains(Query.CT_AUDIO))
		{
			mediaTypes.add("audio");
			mediaTypes.add("music");
		}
		if (contentTypes.contains(Query.CT_TEXT))
		{
			mediaTypes.add("bookmarks");
		}
		StringBuilder sb = new StringBuilder();
		for (Iterator<String> i = mediaTypes.iterator(); i.hasNext();)
		{
			String mediaType = i.next();
			sb.append(mediaType);
			if (i.hasNext())
			{
				sb.append(',');
			}
		}
		return sb.toString();
	}
	

	private String getRanking(de.l3s.interwebj.query.Query.SortOrder sortOrder)
	{
		switch (sortOrder)
		{
			case RELEVANCE:
				return "relevance";
			case DATE:
				return "newest";
			case INTERESTINGNESS:
				return "interestingness";
		}
		return "relevance";
	}
	

	private String getSearchIn(Set<SearchScope> searchScopes)
	{
		if (searchScopes.contains(SearchScope.TEXT)
		    && searchScopes.contains(SearchScope.TAGS))
		{
			return "text,tags";
		}
		else if (searchScopes.contains(SearchScope.TAGS))
		{
			return "tags";
		}
		return "text";
	}
	

	private String getServices()
	{
		return getConfiguration().getPropertyValue("properties", "services");
	}
	

	private boolean isFileUpload(String contentType)
	{
		return Query.CT_IMAGE.equals(contentType)
		       || Query.CT_VIDEO.equals(contentType)
		       || Query.CT_AUDIO.equals(contentType);
	}
	

	private String mediaTypeToContentType(String mediaType)
	{
		if ("photos".equals(mediaType))
		{
			return Query.CT_IMAGE;
		}
		if ("videos".equals(mediaType))
		{
			return Query.CT_VIDEO;
		}
		if ("bookmarks".equals(mediaType))
		{
			return Query.CT_TEXT;
		}
		if ("slideshows".equals(mediaType))
		{
			return Query.CT_IMAGE;
		}
		if ("audio".equals(mediaType))
		{
			return Query.CT_AUDIO;
		}
		if ("music".equals(mediaType))
		{
			return Query.CT_AUDIO;
		}
		return null;
	}
	

	private void parseSearchResponse(QueryResult queryResult,
	                                 IWSearchResponse searchResponse)
	{
		int totalResultCount = 0;
		List<IWSearchResultEntity> searchResults = searchResponse.getQuery().getResults();
		for (IWSearchResultEntity searchResult : searchResults)
		{
			ResultItem resultItem = new ResultItem(getName());
			resultItem.setServiceName(searchResult.getService() + " ("
			                          + getName() + ")");
			resultItem.setId(searchResult.getIdAtService());
			resultItem.setType(mediaTypeToContentType(searchResult.getType()));
			resultItem.setTitle(searchResult.getTitle());
			resultItem.setDescription(searchResult.getDescription());
			resultItem.setUrl(searchResult.getUrl());
			String imageUrl = searchResult.getImage();
			Set<Thumbnail> thumbnails = new TreeSet<Thumbnail>();
			thumbnails.add(new Thumbnail(imageUrl, -1, -1));
			resultItem.setThumbnails(thumbnails);
			resultItem.setDate(searchResult.getDate());
			resultItem.setTags(searchResult.getTags());
			resultItem.setRank(searchResult.getRankAtService());
			totalResultCount = searchResult.getTotalResultsAtService();
			resultItem.setTotalResultCount(totalResultCount);
			resultItem.setViewCount(searchResult.getNumberOfViews());
			resultItem.setCommentCount(searchResult.getNumberOfComments());
			queryResult.addResultItem(resultItem);
		}
		queryResult.setTotalResultCount(totalResultCount);
	}
	

	public static void main(String[] args)
	    throws Exception
	{
		//		testAuthenticate("Flickr");
		//		testRevokeFromLearnWeb("Flickr");
		//		testRevokeFromLearnWeb("YouTube");
		testServices();
		//		testUserServices();
		//		testService("Flickr");
		testGet();
		testEmbedded();
	}
	

	public static void testAuthenticate(String service)
	    throws Exception
	{
		File configFile = new File("connector-config.xml");
		Configuration configuration = new Configuration(new FileInputStream(configFile));
		//		AuthCredentials consumerAuthCredentials = new AuthCredentials("***REMOVED***",
		//		                                                              "***REMOVED***");
		//		AuthCredentials userAuthCredentials = new AuthCredentials("***REMOVED***",
		//		                                                          "***REMOVED***");
		//
		AuthCredentials consumerAuthCredentials = new AuthCredentials("***REMOVED***",
		                                                              "***REMOVED***");
		AuthCredentials userAuthCredentials = new AuthCredentials("***REMOVED***");
		InterWebConnector iwc = new InterWebConnector(configuration,
		                                              consumerAuthCredentials);
		UriBuilder uriBuilder = UriBuilder.fromUri(iwc.getBaseUrl());
		uriBuilder.path("api").path("users/default/services/" + service
		                            + "/auth");
		Parameters params = new Parameters();
		params.add("iw_consumer_key", consumerAuthCredentials.getKey());
		params.add("iw_token", userAuthCredentials.getKey());
		String iwSignature = iwc.generateSignature("users/default/services/"
		                                           + service + "/auth", params);
		params.add("iw_signature", iwSignature);
		Client client = Client.create();
		WebResource resource = client.resource(uriBuilder.build());
		System.out.println("querying interweb: " + resource.toString());
		WebResource.Builder builder = resource.accept(MediaType.WILDCARD);
		builder = resource.type(MediaType.APPLICATION_FORM_URLENCODED);
		System.out.println(params.toQueryString());
		ClientResponse response = builder.post(ClientResponse.class,
		                                       params.toQueryString());
		if (response.getStatus() == 302)
		{
			List<String> locations = response.getHeaders().get("location");
			if (locations != null && locations.size() == 1)
			{
				String location = locations.get(0);
				System.out.println("redirecting to: [" + location + "]");
				Desktop.getDesktop().browse(URI.create(location));
			}
		}
		System.out.println(CoreUtils.getClientResponseContent(response));
	}
	

	public static void testEmbedded()
	    throws Exception
	{
		File configFile = new File("connector-config.xml");
		Configuration configuration = new Configuration(new FileInputStream(configFile));
		AuthCredentials consumerAuthCredentials = new AuthCredentials("***REMOVED***",
		                                                              "***REMOVED***");
		//		AuthCredentials userAuthCredentials = new AuthCredentials("***REMOVED***");
		InterWebConnector iwc = new InterWebConnector(configuration,
		                                              consumerAuthCredentials);
		String url = "http://flickr.com/photos/50762179@N06/4873099667";
		String embedded = iwc.getEmbedded(null, url, -1, -1);
		System.out.println(embedded);
	}
	

	public static void testGet()
	    throws Exception
	{
		File configFile = new File("connector-config.xml");
		Configuration configuration = new Configuration(new FileInputStream(configFile));
		AuthCredentials consumerAuthCredentials = new AuthCredentials("***REMOVED***",
		                                                              "***REMOVED***");
		AuthCredentials userAuthCredentials = new AuthCredentials("***REMOVED***");
		InterWebConnector iwc = new InterWebConnector(configuration,
		                                              consumerAuthCredentials);
		QueryFactory queryFactory = new QueryFactory();
		Query query = queryFactory.createQuery("people");
		query.addContentType(Query.CT_VIDEO);
		query.addContentType(Query.CT_IMAGE);
		query.addContentType(Query.CT_TEXT);
		query.addContentType(Query.CT_AUDIO);
		query.addSearchScope(SearchScope.TEXT);
		query.addSearchScope(SearchScope.TAGS);
		query.setResultCount(50);
		//		query.addParam("date_from", "2009-01-01 00:00:00");
		//		query.addParam("date_till", "2009-06-01 00:00:00");
		query.setSortOrder(SortOrder.RELEVANCE);
		QueryResult queryResult = iwc.get(query, userAuthCredentials);
		String id = queryResult.getQuery().getId();
		System.out.println(id);
	}
	

	public static void testRevokeFromLearnWeb(String service)
	    throws Exception
	{
		File configFile = new File("connector-config.xml");
		Configuration configuration = new Configuration(new FileInputStream(configFile));
		AuthCredentials consumerAuthCredentials = new AuthCredentials("***REMOVED***",
		                                                              "***REMOVED***");
		AuthCredentials userAuthCredentials = new AuthCredentials("***REMOVED***");
		InterWebConnector iwc = new InterWebConnector(configuration,
		                                              consumerAuthCredentials);
		UriBuilder uriBuilder = UriBuilder.fromUri(iwc.getBaseUrl());
		uriBuilder.path("api").path("users/default/services/" + service
		                            + "/auth.xml");
		Parameters params = new Parameters();
		params.add("iw_consumer_key", consumerAuthCredentials.getKey());
		params.add("iw_token", userAuthCredentials.getKey());
		String iwSignature = iwc.generateSignature("users/default/services/"
		                                           + service + "/auth", params);
		params.add("iw_signature", iwSignature);
		iwc.addQueryParameters(uriBuilder, params);
		Client client = Client.create();
		WebResource resource = client.resource(uriBuilder.build());
		System.out.println("querying interweb: " + resource.toString());
		WebResource.Builder builder = resource.accept(MediaType.WILDCARD);
		builder = builder.type(MediaType.APPLICATION_FORM_URLENCODED);
		ClientResponse response = builder.delete(ClientResponse.class);
		System.out.println(CoreUtils.getClientResponseContent(response));
	}
	

	public static void testService(String id)
	    throws Exception
	{
		File configFile = new File("connector-config.xml");
		Configuration configuration = new Configuration(new FileInputStream(configFile));
		AuthCredentials consumerAuthCredentials = new AuthCredentials("***REMOVED***",
		                                                              "***REMOVED***");
		AuthCredentials userAuthCredentials = new AuthCredentials("***REMOVED***");
		InterWebConnector iwc = new InterWebConnector(configuration,
		                                              consumerAuthCredentials);
		
		UriBuilder uriBuilder = UriBuilder.fromUri(iwc.getBaseUrl());
		uriBuilder.path("api").path("users/default/services/" + id + ".xml");
		Parameters params = new Parameters();
		params.add("iw_consumer_key", consumerAuthCredentials.getKey());
		params.add("iw_token", userAuthCredentials.getKey());
		String iwSignature = iwc.generateSignature("users/default/services"
		                                           + id, params);
		System.out.println(iwSignature);
		params.add("iw_signature", iwSignature);
		iwc.addQueryParameters(uriBuilder, params);
		Client client = Client.create();
		WebResource resource = client.resource(uriBuilder.build());
		System.out.println("querying interweb search: " + resource.toString());
		WebResource.Builder builder = resource.accept(MediaType.TEXT_XML);
		ClientResponse response = builder.get(ClientResponse.class);
		String responseContent = response.getEntity(String.class);
		System.out.println(responseContent);
	}
	

	public static void testServices()
	    throws Exception
	{
		File configFile = new File("connector-config.xml");
		Configuration configuration = new Configuration(new FileInputStream(configFile));
		AuthCredentials consumerAuthCredentials = new AuthCredentials("***REMOVED***",
		                                                              "***REMOVED***");
		AuthCredentials userAuthCredentials = new AuthCredentials("***REMOVED***");
		InterWebConnector iwc = new InterWebConnector(configuration,
		                                              consumerAuthCredentials);
		
		UriBuilder uriBuilder = UriBuilder.fromUri(iwc.getBaseUrl());
		uriBuilder.path("api").path("services" + ".xml");
		Parameters params = new Parameters();
		params.add("iw_consumer_key", consumerAuthCredentials.getKey());
		params.add("iw_token", userAuthCredentials.getKey());
		String iwSignature = iwc.generateSignature("services", params);
		System.out.println(iwSignature);
		params.add("iw_signature", iwSignature);
		iwc.addQueryParameters(uriBuilder, params);
		Client client = Client.create();
		WebResource resource = client.resource(uriBuilder.build());
		System.out.println("querying interweb search: " + resource.toString());
		WebResource.Builder builder = resource.accept(MediaType.TEXT_XML);
		ClientResponse response = builder.get(ClientResponse.class);
		String responseContent = response.getEntity(String.class);
		System.out.println(responseContent);
	}
	

	public static void testUserServices()
	    throws Exception
	{
		File configFile = new File("connector-config.xml");
		Configuration configuration = new Configuration(new FileInputStream(configFile));
		AuthCredentials consumerAuthCredentials = new AuthCredentials("***REMOVED***",
		                                                              "***REMOVED***");
		AuthCredentials userAuthCredentials = new AuthCredentials("***REMOVED***");
		InterWebConnector iwc = new InterWebConnector(configuration,
		                                              consumerAuthCredentials);
		UriBuilder uriBuilder = UriBuilder.fromUri(iwc.getBaseUrl());
		uriBuilder.path("api").path("users/default/services" + ".xml");
		Parameters params = new Parameters();
		params.add("iw_consumer_key", consumerAuthCredentials.getKey());
		params.add("iw_token", userAuthCredentials.getKey());
		String iwSignature = iwc.generateSignature("users/default/services",
		                                           params);
		System.out.println(iwSignature);
		params.add("iw_signature", iwSignature);
		iwc.addQueryParameters(uriBuilder, params);
		Client client = Client.create();
		WebResource resource = client.resource(uriBuilder.build());
		System.out.println("querying interweb search: " + resource.toString());
		WebResource.Builder builder = resource.accept(MediaType.TEXT_XML);
		ClientResponse response = builder.get(ClientResponse.class);
		String responseContent = response.getEntity(String.class);
		System.out.println(responseContent);
	}
}
