package de.l3s.interwebj.connector.slideshare;


import static de.l3s.interwebj.util.Assertions.*;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.ws.rs.core.*;

import org.apache.commons.codec.digest.*;

import com.sun.jersey.api.client.*;
import com.sun.jersey.core.util.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.config.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.query.*;
import de.l3s.interwebj.query.Query.SearchScope;
import de.l3s.interwebj.query.Query.SortOrder;
import de.l3s.interwebj.util.*;


public class SlideShareConnector
    extends AbstractServiceConnector
{
	
	public SlideShareConnector(Configuration configuration)
	{
		this(configuration, null);
	}
	

	public SlideShareConnector(Configuration configuration,
	                           AuthCredentials consumerAuthCredentials)
	{
		super(configuration);
		setAuthCredentials(consumerAuthCredentials);
	}
	

	@Override
	public Parameters authenticate(String callbackUrl)
	    throws InterWebException
	{
		Parameters params = new Parameters();
		params.add(Parameters.AUTHORIZATION_URL, callbackUrl);
		return params;
	}
	

	@Override
	public ServiceConnector clone()
	{
		return new SlideShareConnector(getConfiguration(), getAuthCredentials());
	}
	

	@Override
	public AuthCredentials completeAuthentication(Parameters params)
	    throws InterWebException
	{
		notNull(params, "params");
		String key = params.get(Parameters.USER_KEY);
		String secret = params.get(Parameters.USER_SECRET);
		return new AuthCredentials(key, secret);
	}
	

	@Override
	public QueryResult get(Query query, AuthCredentials authCredentials)
	    throws InterWebException
	{
		notNull(query, "query");
		QueryResult queryResult = new QueryResult(query);
		//		WebResource resource = createResource("http://www.slideshare.net/api/2/search_slideshows");
		Client client = Client.create();
		WebResource resource = client.resource("http://www.slideshare.net/api/2/search_slideshows");
		resource = resource.queryParam("q", query.getQuery());
		resource = resource.queryParam("items_per_page",
		                               Integer.toString(query.getResultCount()));
		resource = resource.queryParam("sort",
		                               createSortOrder(query.getSortOrder()));
		String searchScope = createSearchScope(query.getSearchScopes());
		if (searchScope != null)
		{
			resource = resource.queryParam("what", searchScope);
		}
		resource = resource.queryParam("file_type",
		                               createFileType(query.getContentTypes()));
		//		resource = resource.queryParam("detailed", "1");
		System.out.println("querying URL: " + resource.toString());
		ClientResponse response = postQuery(resource);
		SearchResponse sr = response.getEntity(SearchResponse.class);
		queryResult.setTotalResultCount(sr.getMeta().getTotalResults());
		int count = sr.getMeta().getResultOffset() - 1;
		for (SearchResultEntity sre : sr.getSearchResults())
		{
			ResultItem resultItem = new ResultItem(getName());
			resultItem.setType(createType(sre.getSlideshowType()));
			resultItem.setId(Integer.toString(sre.getId()));
			resultItem.setTitle(sre.getTitle());
			resultItem.setDescription(sre.getDescription());
			resultItem.setUrl(sre.getUrl());
			resultItem.setThumbnails(createThumbnails(sre));
			String date = CoreUtils.formatDate(parseDate(sre.getUpdated()));
			resultItem.setDate(date);
			resultItem.setRank(count++);
			resultItem.setEmbedded(sre.getEmbed());
			resultItem.setTotalResultCount(sr.getMeta().getTotalResults());
			queryResult.addResultItem(resultItem);
		}
		return queryResult;
	}
	

	@Override
	public String getEmbedded(AuthCredentials authCredentials,
	                          String url,
	                          int maxWidth,
	                          int maxHeight)
	    throws InterWebException
	{
		notNull(url, "url");
		Client client = Client.create();
		WebResource resource = client.resource("http://www.slideshare.net/api/2/get_slideshow");
		resource = resource.queryParam("slideshow_url", url);
		System.out.println("querying URL: " + resource.toString());
		ClientResponse response = postQuery(resource);
		SearchResultEntity sre = response.getEntity(SearchResultEntity.class);
		return sre.getEmbed();
	}
	

	@Override
	public String getUserId(AuthCredentials authCredentials)
	    throws InterWebException
	{
		return authCredentials.getKey();
	}
	

	@Override
	public boolean isConnectorRegistrationDataRequired()
	{
		return true;
	}
	

	@Override
	public boolean isUserRegistrationDataRequired()
	{
		return true;
	}
	

	@Override
	public void put(byte[] data,
	                String contentType,
	                Parameters params,
	                AuthCredentials authCredentials)
	    throws InterWebException
	{
		// TODO: to implement
	}
	

	@Override
	public void revokeAuthentication()
	    throws InterWebException
	{
		// SlideShare doesn't provide api for token revokation
	}
	

	private String createFileType(List<String> contentTypes)
	{
		return "all";
	}
	

	private String createSearchScope(Set<SearchScope> searchScopes)
	{
		if (!searchScopes.contains(SearchScope.TEXT))
		{
			return "tag";
		}
		return null;
	}
	

	private String createSortOrder(SortOrder sortOrder)
	{
		switch (sortOrder)
		{
			case RELEVANCE:
				return "relevance";
			case DATE:
				return "latest";
			case INTERESTINGNESS:
				return "mostviewed";
			default:
				return "relevance";
		}
	}
	

	private Set<Thumbnail> createThumbnails(SearchResultEntity searchResultEntity)
	{
		Set<Thumbnail> thumbnails = new TreeSet<Thumbnail>();
		thumbnails.add(new Thumbnail(searchResultEntity.getThumbnailSmallURL(),
		                             -1,
		                             -1));
		thumbnails.add(new Thumbnail(searchResultEntity.getThumbnailURL(),
		                             -1,
		                             -1));
		return thumbnails;
	}
	

	private String createType(int slideshowType)
	{
		switch (slideshowType)
		{
			case 0:
				return Query.CT_PRESENTATION;
			case 1:
				return Query.CT_TEXT;
			case 2:
				return Query.CT_IMAGE;
			case 3:
				return Query.CT_VIDEO;
		}
		return null;
	}
	

	private Date parseDate(String dateString)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
		try
		{
			return dateFormat.parse(dateString);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	

	private ClientResponse postQuery(WebResource resource)
	{
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		AuthCredentials authCredentials = getAuthCredentials();
		long timestamp = System.currentTimeMillis() / 1000;
		String toHash = authCredentials.getSecret() + Long.toString(timestamp);
		params.add("api_key", authCredentials.getKey());
		params.add("ts", Long.toString(timestamp));
		params.add("hash", DigestUtils.shaHex(toHash));
		ClientResponse response = resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class,
		                                                                                    params);
		return response;
	}
	

	public static void main(String[] args)
	    throws Exception
	{
		File configFile = new File("connector-config.xml");
		Configuration configuration = new Configuration(new FileInputStream(configFile));
		AuthCredentials consumerAuthCredentials = new AuthCredentials("***REMOVED***",
		                                                              "***REMOVED***");
		SlideShareConnector ssc = new SlideShareConnector(configuration,
		                                                  consumerAuthCredentials);
		QueryFactory queryFactory = new QueryFactory();
		Query query = queryFactory.createQuery("people");
		query.addContentType(Query.CT_VIDEO);
		query.addContentType(Query.CT_IMAGE);
		query.addContentType(Query.CT_TEXT);
		query.addSearchScope(SearchScope.TEXT);
		query.addSearchScope(SearchScope.TAGS);
		query.setResultCount(5);
		//		query.addParam("date_from", "2009-01-01 00:00:00");
		//		query.addParam("date_till", "2009-06-01 00:00:00");
		query.setSortOrder(SortOrder.RELEVANCE);
		QueryResult queryResult = ssc.get(query, null);
		String id = queryResult.getQuery().getId();
		System.out.println(id);
		String embedded = ssc.getEmbedded(null,
		                                  "http://www.slideshare.net/riyadisan/finding-missing-persons",
		                                  -1,
		                                  -1);
		System.out.println(embedded);
	}
}
