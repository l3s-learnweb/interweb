package de.l3s.interwebj.connector.slideshare;


import static de.l3s.interwebj.util.Assertions.*;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.*;

import org.apache.commons.codec.digest.*;
import org.apache.commons.lang.NotImplementedException;

import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.ClientResponse.Status;
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
		resource = resource.queryParam("lang", query.getLanguage());
		resource = resource.queryParam("page", Integer.toString(query.getPage()));
		resource = resource.queryParam("items_per_page", Integer.toString(query.getResultCount()));
		resource = resource.queryParam("sort", createSortOrder(query.getSortOrder()));
		String searchScope = createSearchScope(query.getSearchScopes());
		if (searchScope != null)
		{
			resource = resource.queryParam("what", searchScope);
		}
		String fileType = createFileType(query.getContentTypes());
		if (fileType == null)
		{
			return queryResult;
		}
		resource = resource.queryParam("file_type", fileType);
		//		resource = resource.queryParam("detailed", "1");
		ClientResponse response = postQuery(resource);

		SearchResponse sr;
		try { // macht oft probleme. womöglich liefert slideshare einen fehler im html format oder jersey spinnt
			sr = response.getEntity(SearchResponse.class);
		}
		catch(Exception e) {
			/*
			try {
				System.out.println("response:"+CoreUtils.getClientResponseContent(response));
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}*/
			e.printStackTrace();
			return queryResult;
		}
		queryResult.setTotalResultCount(sr.getMeta().getTotalResults());
		int count = sr.getMeta().getResultOffset() - 1;
		List<SearchResultEntity> searchResults = sr.getSearchResults();
		if (searchResults == null)
		{
			return queryResult;
		}
		for (SearchResultEntity sre : searchResults)
		{
			ResultItem resultItem = new ResultItem(getName());
			resultItem.setType(createType(sre.getSlideshowType()));
			resultItem.setId(Integer.toString(sre.getId()));
			resultItem.setTitle(sre.getTitle());
			resultItem.setDescription(sre.getDescription());
			resultItem.setUrl(sre.getUrl());			
			resultItem.setDate(CoreUtils.formatDate(parseDate(sre.getUpdated())));
			resultItem.setRank(count++);			
			resultItem.setTotalResultCount(sr.getMeta().getTotalResults());
			
			Set<Thumbnail> thumbnails = new TreeSet<Thumbnail>();
			thumbnails.add(new Thumbnail(sre.getThumbnailSmallURL(), 120, 90));
			thumbnails.add(new Thumbnail(sre.getThumbnailURL(), 170, 128));			
			resultItem.setThumbnails(thumbnails);
			
			resultItem.setEmbeddedSize1(CoreUtils.createImageCode(sre.getThumbnailSmallURL(), 120, 90, 100, 100));
			resultItem.setEmbeddedSize2("<img src=\""+ sre.getThumbnailURL() +"\" width=\"170\" height=\"128\" />");
			resultItem.setImageUrl(sre.getThumbnailURL());
			
			// remove spam from the embedded code
			Pattern pattern = Pattern.compile("(<object.*</object>)"); 
			Matcher matcher = pattern.matcher(sre.getEmbed()); 
			
		    if(matcher.find()) 
		    	resultItem.setEmbeddedSize3(matcher.group(0));				
		    else
		    	resultItem.setEmbeddedSize3(sre.getEmbed());	
			
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
		URI uri = URI.create(url);
		URI baseUri = URI.create(getBaseUrl());
		if (!baseUri.getHost().endsWith(uri.getHost()))
		{
			throw new InterWebException("URL: [" + url
			                            + "] doesn't belong to connector");
		}
		Client client = Client.create();
		WebResource resource = client.resource("http://www.slideshare.net/api/2/get_slideshow");
		resource = resource.queryParam("slideshow_url", url);
		System.out.println("querying URL: " + resource.toString());
		ClientResponse response = postQuery(resource);
		if (response.getClientResponseStatus() != Status.OK)
		{
			throw new InterWebException("URL: [" + url
			                            + "] doesn't belong to connector ["
			                            + getName() + "]");
		}
		SearchResultEntity sre = response.getEntity(SearchResultEntity.class);
		String embedded = sre.getEmbed();
		embedded = embedded.replaceAll("width:\\d+px", "width:" + maxWidth
		                                               + "px");
		embedded = embedded.replaceAll("width=\"\\d+\"", "width=\"" + maxWidth
		                                                 + "\"");
		embedded = embedded.replaceAll("height=\"\\d+\"", "height=\""
		                                                  + maxHeight + "\"");
		embedded = embedded.replaceAll("<strong.+</strong>", "");
		return embedded;
	}
	

	@Override
	public String getUserId(AuthCredentials authCredentials)
	    throws InterWebException
	{
		notNull(authCredentials, "authCredentials");
		Client client = Client.create();
		WebResource resource = client.resource("http://www.slideshare.net/api/2/get_user_tags");
		resource = resource.queryParam("username", authCredentials.getKey());
		resource = resource.queryParam("password", authCredentials.getSecret());
		System.out.println("querying URL: " + resource.toString());
		ClientResponse response = getQuery(resource);
		try
		{
			response.getEntity(TagsResponse.class);
		}
		catch (Exception e)
		{
			throw new InterWebException("User authentication failed on SlideShare");
		}
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
	public boolean isUserRegistrationRequired()
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
		List<String> fileTypes = new ArrayList<String>();
		if (contentTypes.contains(Query.CT_PRESENTATION))
		{
			fileTypes.add("presentations");
		}
		if (contentTypes.contains(Query.CT_TEXT))
		{
			fileTypes.add("documents");
		}
		if (contentTypes.contains(Query.CT_VIDEO))
		{
			fileTypes.add("videos");
		}
		if (fileTypes.size() == 0)
		{
			return null;
		}
		if (fileTypes.size() == 1)
		{
			return fileTypes.get(0);
		}
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
	

	private ClientResponse getQuery(WebResource resource)
	{
		AuthCredentials authCredentials = getAuthCredentials();
		long timestamp = System.currentTimeMillis() / 1000;
		String toHash = authCredentials.getSecret() + Long.toString(timestamp);
		resource = resource.queryParam("api_key", authCredentials.getKey());
		resource = resource.queryParam("ts", Long.toString(timestamp));
		resource = resource.queryParam("hash", DigestUtils.shaHex(toHash));
		ClientResponse response = resource.get(ClientResponse.class);
		return response;
	}
	

	private Date parseDate(String dateString)
	    throws InterWebException
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy",
		                                                   Locale.US);
		try
		{
			return dateFormat.parse(dateString);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			throw new InterWebException("dateString: [" + dateString + "] "
			                            + e.getMessage());
		}
	}
	

	private ClientResponse postQuery(WebResource resource)
	{
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		return postQuery(resource, params);
	}
	

	private ClientResponse postQuery(WebResource resource,
	                                 MultivaluedMap<String, String> params)
	{
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
	
	@Override
	public Set<String> getTags(String username, int maxCount) throws IllegalArgumentException, IOException {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}


	@Override
	public Set<String> getUsers(Set<String> tags, int maxCount) throws IOException, InterWebException {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
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
		query.addContentType(Query.CT_PRESENTATION);
		query.addContentType(Query.CT_AUDIO);
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
		                                  "http://www.slideshare.net/pacific2000/flowers-presentation-715934",
		                                  240,
		                                  240);
		System.out.println(embedded);
	}
}
