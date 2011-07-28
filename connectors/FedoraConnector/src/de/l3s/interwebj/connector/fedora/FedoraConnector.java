package de.l3s.interwebj.connector.fedora;


import static de.l3s.interwebj.util.Assertions.*;

import java.io.*;
import java.net.URLEncoder;
import java.text.*;
import java.util.*;

import javax.ws.rs.core.*;

import org.apache.commons.codec.digest.*;


import com.sun.jersey.api.client.*;
import com.sun.jersey.core.util.*;

import de.l3s.fedora.FedoraResource;
import de.l3s.fedora.FedoraSearchResult;
import de.l3s.fedora.FedoraTag;
import de.l3s.fedora.FedoraTagList;
import de.l3s.fedora.FedoraTitledLink;
import de.l3s.interwebj.*;
import de.l3s.interwebj.config.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.query.*;
import de.l3s.interwebj.query.Query.SearchScope;
import de.l3s.interwebj.query.Query.SortOrder;
import de.l3s.interwebj.util.*;



public class FedoraConnector
    extends AbstractServiceConnector
{
	
	public FedoraConnector(Configuration configuration)
	{
		this(configuration, null);
	}
	

	public FedoraConnector(Configuration configuration,
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
		return new FedoraConnector(getConfiguration(), getAuthCredentials());
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
		WebResource resource=null;
		try {
			resource = client.resource("http://learnweb.l3s.uni-hannover.de/FedoraKRSM/fedora/search/terms="+URLEncoder.encode(query.getQuery(),"UTF-8")+"&maxResults="+query.getResultCount());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/*
		resource = resource.queryParam("terms", query.getQuery());
		resource = resource.queryParam("maxResults",
		                               Integer.toString(query.getResultCount()));
		resource = resource.queryParam("repositories",
                "fedora liferay");
                */
		/*
		resource = resource.queryParam("sort",
		                               createSortOrder(query.getSortOrder()));
		*/
		
		
		
		/*
		
		Tag tag = getTag(terms);
		
		if(null != tag)
			return getResourcesByTagId(tag.getId());			
		
		if(tagSearch)
			return null;		
		
		Element response = null;
		try {
			response = sendGetRequest("search?terms="+ URLEncoder.encode(query.getQuery(),"UTF-8")+ "&maxResults=" + query.getResultCount() +"&repositories=fedora+liferay");
		}
		catch (UnsupportedEncodingException e) {}
		
		if(null == response) 
			return null;
		
		List<Resource> result = new LinkedList<Resource>();
		List<Element> resourcesEl = response.elements("resource");

		for (Element resourceEl : resourcesEl) 
		{
			result.add(parseResource(resourceEl));
		}
		
		*/
	
		//		resource = resource.queryParam("detailed", "1");
		System.out.println("querying URL: " + resource.toString());
		FedoraSearchResult response = getQuery(resource);
		System.out.println(response);
		queryResult.setTotalResultCount(response.getResource().size());
		int count = response.getResource().size() - 1;
		
		for (FedoraResource sre : response.getResource())
		{
			ResultItem resultItem = new ResultItem(getName());
			resultItem.setType(sre.getCanonical().getDc().getType());
			resultItem.setId(sre.getCanonical().getDc().getIdentifier());
			resultItem.setTitle(sre.getCanonical().getDc().getTitle());
			resultItem.setDescription(sre.getCanonical().getDc().getDescription());
			resultItem.setUrl(sre.getCanonical().getLink().getHref());
			resultItem.setThumbnails(createThumbnails(sre));
			String date = CoreUtils.formatDate(parseDate(sre.getCanonical().getDc().getDate()));
			resultItem.setDate(date);
			resultItem.setRank(count++);
			resultItem.setEmbedded(sre.getCanonical().getLink().getHref());
			resultItem.setTotalResultCount(100);
			resultItem.setTags(getTags(sre));
			queryResult.addResultItem(resultItem);
		}
		return queryResult;
	}
	

	private String getTags(FedoraResource sre) {
		FedoraTagList list = sre.getCanonical().getTaglist();
	if(list==null) return "";
	Vector<FedoraTitledLink> tags = list.getTag();
	if(tags==null) return "";
	StringBuffer sb=new StringBuffer();
	for(FedoraTitledLink tag:tags)
	{
	
		if(sb.length()>0){sb.append(",");}
		sb.append(tag.getTitle());
	}
		return sb.toString();
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
		notNull(authCredentials, "authCredentials");
		Client client = Client.create();
		WebResource resource = client.resource("http://www.slideshare.net/api/2/get_user_tags");
		resource = resource.queryParam("username", authCredentials.getKey());
		resource = resource.queryParam("password", authCredentials.getSecret());
		System.out.println("querying URL: " + resource.toString());
		ClientResponse response = null;//getQuery(resource);
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
	

	private Set<Thumbnail> createThumbnails(FedoraResource sre)
	{
		Set<Thumbnail> thumbnails = new TreeSet<Thumbnail>();
		thumbnails.add(new Thumbnail("",
		                             -1,
		                             -1));
		thumbnails.add(new Thumbnail("",
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
	

	private FedoraSearchResult getQuery(WebResource resource)
	{
		AuthCredentials authCredentials = getAuthCredentials();
		long timestamp = System.currentTimeMillis() / 1000;
		String toHash = authCredentials.getSecret() + Long.toString(timestamp);
		
		FedoraSearchResult response = resource.get(FedoraSearchResult.class);
		System.out.println(response);
		return response;
	}
	

	private Date parseDate(String dateString)
	    throws InterWebException
	{
		if(dateString.trim().length()==0) {dateString="01/01/2005";}
		dateString=dateString.replaceAll("\\/", " ");
		String tmpdate = new String(dateString);
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM dd yyyy");
try
		{
			return dateFormat.parse(dateString);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			throw new InterWebException("dateString: [" + tmpdate + "] "
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
	

	public static void main(String[] args)
	    throws Exception
	{
		File configFile = new File("connector-config.xml");
		Configuration configuration = new Configuration(new FileInputStream(configFile));
		AuthCredentials consumerAuthCredentials = new AuthCredentials("***REMOVED***",
		                                                              "***REMOVED***");
		FedoraConnector ssc = new FedoraConnector(configuration,
		                                                  consumerAuthCredentials);
		QueryFactory queryFactory = new QueryFactory();
		Query query = queryFactory.createQuery("salsa");
		
		query.setResultCount(10);
		//		query.addParam("date_from", "2009-01-01 00:00:00");
		//		query.addParam("date_till", "2009-06-01 00:00:00");
	
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
