package de.l3s.interwebj.connector;


import java.nio.charset.*;
import java.util.*;

import javax.ws.rs.core.*;
import javax.xml.bind.*;

import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.*;

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
	

	public InterWebConnector(AuthCredentials consumerAuthCredentials)
	{
		super("interweb", "http://athena.l3s.uni-hannover.de:8000");
		setConsumerAuthCredentials(consumerAuthCredentials);
	}
	

	private void addUriParameters(UriBuilder uriBuilder,
	                              Map<String, String> params)
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
		Map<String, String> params = new TreeMap<String, String>();
		params.put("iw_consumer_key", consumerAuthCredentials.getKey());
		String iwSignature = generateSignature(API_REQUEST_TOKEN_PATH, params);
		params.put("iw_signature", iwSignature);
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
			params = new TreeMap<String, String>();
			params.put("iw_token", requestToken);
			params.put("iw_consumer_key", consumerAuthCredentials.getKey());
			iwSignature = generateSignature(API_AUTHORIZE_TOKEN_PATH, params);
			Environment.logger.debug("iwSignature: [" + iwSignature + "]");
			params.put("iw_signature", iwSignature);
			params.put("callback", callbackUrl);
			uriBuilder = UriBuilder.fromUri(getBaseUrl());
			uriBuilder.path("api").path(API_AUTHORIZE_TOKEN_PATH);
			addUriParameters(uriBuilder, params);
			Parameters parameters = new Parameters();
			parameters.add(Parameters.OAUTH_AUTHORIZATION_URL,
			               uriBuilder.build().toASCIIString());
			return parameters;
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
	

	private String generateSignature(String path, Map<String, String> params)
	{
		AuthCredentials consumerAuthCredentials = getConsumerAuthCredentials();
		params.put("iw_path", path);
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
		Map<String, String> params = new TreeMap<String, String>();
		params.put("iw_consumer_key", consumerAuthCredentials.getKey());
		params.put("iw_token", authCredentials.getKey());
		params.put("q", query.getQuery());
		params.put("search_in", getSearchIn(query.getSearchScopes()));
		params.put("media_types", getMediaTypes(query.getContentTypes()));
		params.put("number_of_results", String.valueOf(query.getResultCount()));
		params.put("ranking", getRanking(query.getSortOrder()));
		params.put("date_from", query.getParam("date_from", ""));
		params.put("date_till", query.getParam("date_till", ""));
		params.put("services", getServices());
		String iwSignature = generateSignature(API_SEARCH_PATH, params);
		params.put("iw_signature", iwSignature);
		addUriParameters(uriBuilder, params);
		WebResource resource = client.resource(uriBuilder.build());
		Environment.logger.debug("querying interweb search: "
		                         + resource.toString());
		ClientResponse response = resource.accept(MediaType.TEXT_XML).get(ClientResponse.class);
		//		try
		//		{
		//			CoreUtils.printClientResponse(response);
		//		}
		//		catch (IOException e)
		//		{
		//			e.printStackTrace();
		//		}
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
		// Used only unimplemented by InterWebJ services
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
		// TODO Auto-generated method stub
		
	}
	

	public static void main(String[] args)
	    throws Exception
	{
		AuthCredentials consumerAuthCredentials = new AuthCredentials("***REMOVED***",
		                                                              "***REMOVED***");
		AuthCredentials userAuthCredentials = new AuthCredentials("***REMOVED***");
		InterWebConnector iwc = new InterWebConnector(consumerAuthCredentials);
		QueryFactory queryFactory = new QueryFactory();
		Query query = queryFactory.createQuery("auto");
		query.addContentType(Query.CT_VIDEO);
		query.addContentType(Query.CT_IMAGE);
		query.addContentType(Query.CT_TEXT);
		query.addContentType(Query.CT_AUDIO);
		query.addSearchScope(SearchScope.TEXT);
		query.addSearchScope(SearchScope.TAGS);
		query.setResultCount(5);
		query.addParam("date_from", "2009-01-01 00:00:00");
		query.addParam("date_till", "2009-06-01 00:00:00");
		query.setSortOrder(SortOrder.RELEVANCE);
		QueryResult queryResult = iwc.get(query, userAuthCredentials);
		String id = queryResult.getQuery().getId();
		System.out.println(id);
	}
}
