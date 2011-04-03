package de.l3s.interwebj.connector;


import java.io.*;
import java.nio.charset.*;
import java.util.*;

import javax.ws.rs.core.*;
import javax.xml.bind.*;

import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.*;
import com.sun.jersey.multipart.*;
import com.sun.jersey.multipart.file.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.jaxb.*;
import de.l3s.interwebj.query.*;
import de.l3s.interwebj.query.Query.SearchScope;
import de.l3s.interwebj.query.Query.SortOrder;
import de.l3s.interwebj.util.*;


public class InterWebConnector
    extends ServiceConnector
{
	
	private static final String API_REQUEST_TOKEN_PATH = "auth/request_token";
	private static final String API_AUTHORIZE_TOKEN_PATH = "auth/authorize";
	private static final String API_SEARCH_PATH = "search";
	private static final String API_UPLOAD_PATH = "users/default/uploads";
	

	public InterWebConnector(AuthCredentials consumerAuthCredentials)
	{
		super("interweb", "http://athena.l3s.uni-hannover.de:8000");
		setConsumerAuthCredentials(consumerAuthCredentials);
	}
	

	private void addUriParameters(UriBuilder uriBuilder, Parameters params)
	{
		for (String name : params.keySet())
		{
			if (!name.equals("data"))
			{
				uriBuilder.queryParam(name, params.get(name));
			}
		}
	}
	

	@Override
	public Parameters authenticate(PermissionLevel permissionLevel,
	                               String callbackUrl)
	    throws InterWebException
	{
		if (!isRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		AuthCredentials consumerAuthCredentials = getConsumerAuthCredentials();
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		UriBuilder uriBuilder = UriBuilder.fromUri(getBaseUrl());
		uriBuilder.path("api").path(API_REQUEST_TOKEN_PATH + ".xml");
		Parameters params = new Parameters();
		params.add("iw_consumer_key", consumerAuthCredentials.getKey());
		String iwSignature = generateSignature(API_REQUEST_TOKEN_PATH, params);
		params.add("iw_signature", iwSignature);
		addUriParameters(uriBuilder, params);
		WebResource resource = client.resource(uriBuilder.build());
		Environment.logger.debug("querying interweb request token: "
		                         + resource.toString());
		ClientResponse response = resource.accept(MediaType.TEXT_XML).get(ClientResponse.class);
		Environment.logger.debug(response);
		if (response.getStatus() != 200)
		{
			throw new InterWebException(response.toString());
		}
		try
		{
			IWRequestTokenResponse requestTokenResponse = CoreUtils.<IWRequestTokenResponse> create(IWRequestTokenResponse.class,
			                                                                                        response.getEntityInputStream());
			Environment.logger.debug(requestTokenResponse);
			if ("fail".equals(requestTokenResponse.getStat()))
			{
				IWError responseError = requestTokenResponse.getError();
				throw new InterWebException("InterWeb service returned respose: "
				                            + responseError.getMessage()
				                            + " (error code "
				                            + responseError.getCode() + ")");
			}
			String requestToken = requestTokenResponse.getRequestToken().getToken();
			params = new Parameters();
			params.add("iw_token", requestToken);
			params.add("iw_consumer_key", consumerAuthCredentials.getKey());
			iwSignature = generateSignature(API_AUTHORIZE_TOKEN_PATH, params);
			Environment.logger.debug("iwSignature: [" + iwSignature + "]");
			params.add("iw_signature", iwSignature);
			params.add("callback", callbackUrl);
			uriBuilder = UriBuilder.fromUri(getBaseUrl());
			uriBuilder.path("api").path(API_AUTHORIZE_TOKEN_PATH);
			addUriParameters(uriBuilder, params);
			params.add(Parameters.OAUTH_AUTHORIZATION_URL,
			           uriBuilder.build().toASCIIString());
			return params;
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
	}
	

	@Override
	public ServiceConnector clone()
	{
		return new InterWebConnector(getConsumerAuthCredentials());
	}
	

	@Override
	public AuthCredentials completeAuthentication(Parameters params)
	    throws InterWebException
	{
		if (params == null)
		{
			throw new NullPointerException("Argument [params] can not be null");
		}
		AuthCredentials authCredentials = null;
		if (!isRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		String iwToken = params.get("iw_token");
		Environment.logger.debug("iw_token: " + iwToken);
		if (iwToken != null)
		{
			authCredentials = new AuthCredentials(iwToken);
		}
		return authCredentials;
	}
	

	private File createTempFile(String fileName, byte[] data)
	    throws IOException
	{
		File file = new File(fileName);
		FileOutputStream os = new FileOutputStream(file);
		os.write(data);
		os.close();
		return file;
	}
	

	private String generateSignature(String path, Parameters params)
	{
		AuthCredentials consumerAuthCredentials = getConsumerAuthCredentials();
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
		Environment.logger.debug("hashing string: [" + sb.toString() + "]");
		return CoreUtils.generateMD5Hash(sb.toString().getBytes(Charset.forName("UTF-8")));
	}
	

	@Override
	public QueryResult get(Query query, AuthCredentials authCredentials)
	    throws InterWebException
	{
		if (query == null)
		{
			throw new NullPointerException("Argument [query] can not be null");
		}
		if (!isRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		QueryResult queryResult = new QueryResult(query);
		AuthCredentials consumerAuthCredentials = getConsumerAuthCredentials();
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
		addUriParameters(uriBuilder, params);
		WebResource resource = client.resource(uriBuilder.build());
		Environment.logger.debug("querying interweb search: "
		                         + resource.toString());
		ClientResponse response = resource.accept(MediaType.TEXT_XML).get(ClientResponse.class);
		if (response.getStatus() != 200)
		{
			throw new InterWebException(response.toString());
		}
		try
		{
			IWSearchResponse iwSearchResponse = CoreUtils.<IWSearchResponse> create(IWSearchResponse.class,
			                                                                        response.getEntityInputStream());
			if ("fail".equals(iwSearchResponse.getStat()))
			{
				IWError responseError = iwSearchResponse.getError();
				throw new InterWebException("InterWeb service returned respose: "
				                            + responseError.getMessage()
				                            + " (error code "
				                            + responseError.getCode() + ")");
			}
			Environment.logger.debug("standing link: "
			                         + iwSearchResponse.getQuery().getLink());
			parseIWSearchResponse(queryResult, iwSearchResponse);
			Environment.logger.debug("results count: ["
			                         + iwSearchResponse.getQuery().getResults().size()
			                         + "]");
			Environment.logger.debug("time elapsed: ["
			                         + iwSearchResponse.getQuery().getElapsedTime()
			                         + "]");
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
			throw new InterWebException(e);
		}
		return queryResult;
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
	

	private String getRanking(SortOrder sortOrder)
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
		// TODO: Used only unimplemented by InterWebJ services
		return "Flickr,YouTube";
		//		return "Delicious,Ipernity,LastFm,SlideShare,Vimeo,Blogger,Facebook,GroupMe";
	}
	

	@Override
	protected void init()
	{
		// TODO: Stub. Read from configuration file
		TreeSet<String> contentTypes = new TreeSet<String>();
		contentTypes.add("image");
		contentTypes.add("video");
		contentTypes.add("text");
		contentTypes.add("audio");
		setContentTypes(contentTypes);
	}
	

	private boolean isFileUpload(String contentType)
	{
		return Query.CT_IMAGE.equals(contentType)
		       || Query.CT_VIDEO.equals(contentType)
		       || Query.CT_AUDIO.equals(contentType);
	}
	

	@Override
	public boolean isRegistrationRequired()
	{
		return true;
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
	

	private void parseIWSearchResponse(QueryResult queryResult,
	                                   IWSearchResponse iwSearchResponse)
	{
		List<IWSearchResult> iwSearchResults = iwSearchResponse.getQuery().getResults();
		for (IWSearchResult iwSearchResult : iwSearchResults)
		{
			ResultItem resultItem = new InterWebResultItem(getName());
			resultItem.setServiceName(iwSearchResult.getService());
			resultItem.setId(iwSearchResult.getIdAtService());
			resultItem.setType(mediaTypeToContentType(iwSearchResult.getType()));
			resultItem.setTitle(iwSearchResult.getTitle());
			resultItem.setDescription(iwSearchResult.getDescription());
			resultItem.setUrl(iwSearchResult.getUrl());
			resultItem.setImageUrl(iwSearchResult.getImage());
			resultItem.setDate(iwSearchResult.getDate());
			resultItem.setTags(iwSearchResult.getTags());
			resultItem.setRank(iwSearchResult.getRankAtService());
			resultItem.setTotalResultCount(iwSearchResult.getTotalResultsAtService());
			resultItem.setViewCount(iwSearchResult.getNumberOfViews());
			resultItem.setCommentCount(iwSearchResult.getNumberOfComments());
			queryResult.addResultItem(resultItem);
		}
	}
	

	@Override
	public void put(byte[] data,
	                String contentType,
	                Parameters params,
	                AuthCredentials authCredentials)
	    throws InterWebException
	{
		if (data == null)
		{
			throw new NullPointerException("Argument [data] can not be null");
		}
		if (contentType == null)
		{
			throw new NullPointerException("Argument [contentType] can not be null");
		}
		if (params == null)
		{
			throw new NullPointerException("Argument [params] can not be null");
		}
		if (!isRegistered())
		{
			throw new InterWebException("Service is not yet registered");
		}
		if (authCredentials == null)
		{
			throw new InterWebException("Upload is forbidden for non-authorized users");
		}
		AuthCredentials consumerAuthCredentials = getConsumerAuthCredentials();
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		UriBuilder uriBuilder = UriBuilder.fromUri(getBaseUrl());
		uriBuilder.path("api").path(API_UPLOAD_PATH + ".xml");
		String title = params.get(Parameters.TITLE, "No Title");
		String description = params.get(Parameters.DESCRIPTION,
		                                "No Description");
		String tags = params.get(Parameters.TAGS);
		Integer privacy = Integer.valueOf(params.get(Parameters.PRIVACY, "0"));
		MultiPart multiPart = new MultiPart();
		Parameters iwParams = new Parameters();
		iwParams.add("iw_consumer_key", consumerAuthCredentials.getKey());
		iwParams.add("iw_token", authCredentials.getKey());
		iwParams.add(Parameters.TITLE, title);
		iwParams.add("is_private", privacy.toString());
		multiPart = multiPart.bodyPart(new FormDataBodyPart(Parameters.TITLE,
		                                                    title));
		iwParams.add(Parameters.DESCRIPTION, description);
		multiPart = multiPart.bodyPart(new FormDataBodyPart(Parameters.DESCRIPTION,
		                                                    description));
		if (tags != null)
		{
			iwParams.add(Parameters.TAGS, tags);
			multiPart = multiPart.bodyPart(new FormDataBodyPart(Parameters.TAGS,
			                                                    tags));
		}
		multiPart = multiPart.bodyPart(new FormDataBodyPart("is_private",
		                                                    privacy.toString()));
		String iwSignature = generateSignature(API_UPLOAD_PATH, iwParams);
		iwParams.add("iw_signature", iwSignature);
		iwParams.remove(Parameters.TITLE);
		iwParams.remove(Parameters.DESCRIPTION);
		iwParams.remove(Parameters.TAGS);
		iwParams.remove("is_private");
		addUriParameters(uriBuilder, iwParams);
		WebResource resource = client.resource(uriBuilder.build());
		Environment.logger.debug("querying interweb search: "
		                         + resource.toString());
		if (isFileUpload(contentType))
		{
			try
			{
				File f = createTempFile(params.get("filename"), data);
				FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("data",
				                                                         f);
				multiPart.bodyPart(fileDataBodyPart);
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
		ClientResponse response = resource.type(MediaType.MULTIPART_FORM_DATA).accept(MediaType.TEXT_XML).post(ClientResponse.class,
		                                                                                                       multiPart);
		if (response.getStatus() != 200)
		{
			throw new InterWebException(response.toString());
		}
		System.out.println(response.toString());
		System.out.println(CoreUtils.printclientResponse(response));
		
	}
	

	public static void main(String[] args)
	    throws Exception
	{
		String[] words = "water people live boy air play land light house picture animal earth country school food sun city tree sea night life paper music book letter car rain friend horse girl bird family leave rock fire king travel war love person money road star street object moon island test gold game".split(" ");
		for (String word : words)
		{
			testGet();
			
		}
	}
	

	public static void testGet()
	    throws Exception
	{
		AuthCredentials consumerAuthCredentials = new AuthCredentials("***REMOVED***",
		                                                              "***REMOVED***");
		AuthCredentials userAuthCredentials = new AuthCredentials("***REMOVED***");
		InterWebConnector iwc = new InterWebConnector(consumerAuthCredentials);
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
}
