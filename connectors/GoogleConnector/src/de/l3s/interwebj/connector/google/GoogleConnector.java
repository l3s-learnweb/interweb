package de.l3s.interwebj.connector.google;


import static de.l3s.interwebj.util.Assertions.*;

import java.io.*;
import java.util.*;

import com.googleapis.ajax.common.*;
import com.googleapis.ajax.schema.*;
import com.googleapis.ajax.services.*;
import com.googleapis.ajax.services.enumeration.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.config.*;
import de.l3s.interwebj.core.*;
import de.l3s.interwebj.query.*;
import de.l3s.interwebj.util.*;


public class GoogleConnector
    extends AbstractServiceConnector
{
	
	private static final int MAX_RESULT_COUNT = 64;
	
	private static final String API_KEY = "***REMOVED***";
	

	public GoogleConnector(Configuration configuration)
	{
		this(configuration, null);
	}
	

	public GoogleConnector(de.l3s.interwebj.config.Configuration configuration,
	                       AuthCredentials consumerAuthCredentials)
	{
		super(configuration);
		setAuthCredentials(consumerAuthCredentials);
	}
	

	@Override
	public Parameters authenticate(String callbackUrl)
	    throws InterWebException
	{
		// authentication is not supported. do nothing
		return null;
	}
	

	@Override
	public ServiceConnector clone()
	{
		return new GoogleConnector(getConfiguration(), getAuthCredentials());
	}
	

	@Override
	public AuthCredentials completeAuthentication(Parameters params)
	    throws InterWebException
	{
		// authentication is not supported. do nothing
		return null;
	}
	

	@Override
	public QueryResult get(Query query, AuthCredentials authCredentials)
	    throws InterWebException
	{
		notNull(query, "query");
		QueryResult queryResult = new QueryResult(query);
		if (query.getContentTypes().contains(Query.CT_TEXT))
		{
			QueryResult webQueryResult = getWebResults(query, authCredentials);
			queryResult.addQueryResult(webQueryResult);
		}
		if (query.getContentTypes().contains(Query.CT_IMAGE))
		{
			QueryResult imageQueryResult = getImageResults(query,
			                                               authCredentials);
			queryResult.addQueryResult(imageQueryResult);
		}
		if (query.getContentTypes().contains(Query.CT_VIDEO))
		{
			QueryResult imageQueryResult = getVideoResults(query,
			                                               authCredentials);
			queryResult.addQueryResult(imageQueryResult);
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
		// TODO: generate somehow site thumbnails
		return null;
	}
	

	@Override
	public String getUserId(AuthCredentials authCredentials)
	    throws InterWebException
	{
		// not supported. do nothing
		return null;
	}
	

	@Override
	public boolean isConnectorRegistrationDataRequired()
	{
		return false;
	}
	

	@Override
	public boolean isRegistered()
	{
		return true;
	}
	

	@Override
	public boolean isUserRegistrationDataRequired()
	{
		return false;
	}
	

	@Override
	public boolean isUserRegistrationRequired()
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
		// not supported. do nothing
	}
	

	@Override
	public void revokeAuthentication()
	    throws InterWebException
	{
		// not supported. do nothing
	}
	

	private ResultItem convertImageResult(ImageResult imageResult,
	                                      int index,
	                                      long totalResultCount)
	{
		ResultItem resultItem = new ResultItem(getName());
		resultItem.setType(createType(imageResult.getGsearchResultClass()));
		resultItem.setTitle(imageResult.getTitleNoFormatting());
		resultItem.setDescription(imageResult.getContentNoFormatting());
		resultItem.setUrl(imageResult.getOriginalContextUrl());
		resultItem.setRank(index);
		resultItem.setTotalResultCount(totalResultCount);
		resultItem.setThumbnails(createThumbnails(imageResult));
		return resultItem;
	}
	

	private QueryResult convertImageResultResponse(Query query,
	                                               PagedList<ImageResult> imageResultResponse,
	                                               int index)
	{
		QueryResult queryResult = new QueryResult(query);
		long totalResultCount = imageResultResponse.getEstimatedResultCount();
		queryResult.setTotalResultCount(totalResultCount);
		for (ImageResult imageResult : imageResultResponse)
		{
			ResultItem resultItem = convertImageResult(imageResult,
			                                           index,
			                                           totalResultCount);
			if (index < query.getResultCount())
			{
				queryResult.addResultItem(resultItem);
			}
			index++;
		}
		return queryResult;
	}
	

	private ResultItem convertVideoResult(VideoResult videoResult,
	                                      int index,
	                                      long totalResultCount)
	{
		ResultItem resultItem = new ResultItem(getName());
		resultItem.setType(createType(videoResult.getGsearchResultClass()));
		resultItem.setTitle(videoResult.getTitleNoFormatting());
		resultItem.setDescription(videoResult.getContent());
		resultItem.setUrl(videoResult.getUrl());
		resultItem.setRank(index);
		resultItem.setTotalResultCount(totalResultCount);
		resultItem.setThumbnails(createThumbnails(videoResult));
		resultItem.setViewCount(videoResult.getViewCount());
		resultItem.setDate(CoreUtils.formatDate(videoResult.getPublished().getTime()));
		return resultItem;
	}
	

	private QueryResult convertVideoResultResponse(Query query,
	                                               PagedList<VideoResult> videoResultResponse,
	                                               int index)
	{
		QueryResult queryResult = new QueryResult(query);
		long totalResultCount = videoResultResponse.getEstimatedResultCount();
		queryResult.setTotalResultCount(totalResultCount);
		for (VideoResult videoResult : videoResultResponse)
		{
			ResultItem resultItem = convertVideoResult(videoResult,
			                                           index,
			                                           totalResultCount);
			if (index < query.getResultCount())
			{
				queryResult.addResultItem(resultItem);
			}
			index++;
		}
		return queryResult;
	}
	

	private ResultItem convertWebResult(WebResult webResult,
	                                    int index,
	                                    long totalResultCount)
	{
		ResultItem resultItem = new ResultItem(getName());
		resultItem.setType(createType(webResult.getGsearchResultClass()));
		resultItem.setTitle(webResult.getTitleNoFormatting());
		resultItem.setDescription(webResult.getContent());
		resultItem.setUrl(webResult.getUrl());
		resultItem.setRank(index);
		resultItem.setTotalResultCount(totalResultCount);
		return resultItem;
	}
	

	private QueryResult convertWebResultResponse(Query query,
	                                             PagedList<WebResult> webResultResponse,
	                                             int index)
	{
		QueryResult queryResult = new QueryResult(query);
		long totalResultCount = webResultResponse.getEstimatedResultCount();
		queryResult.setTotalResultCount(totalResultCount);
		for (WebResult webResult : webResultResponse)
		{
			ResultItem resultItem = convertWebResult(webResult,
			                                         index,
			                                         totalResultCount);
			if (index < query.getResultCount())
			{
				queryResult.addResultItem(resultItem);
			}
			index++;
		}
		return queryResult;
	}
	

	private Set<Thumbnail> createThumbnails(ImageResult imageResult)
	{
		SortedSet<Thumbnail> thumbnails = new TreeSet<Thumbnail>();
		thumbnails.add(new Thumbnail(imageResult.getTbUrl(),
		                             imageResult.getTbWidth(),
		                             imageResult.getTbHeight()));
		thumbnails.add(new Thumbnail(imageResult.getUrl(),
		                             imageResult.getWidth(),
		                             imageResult.getHeight()));
		return thumbnails;
	}
	

	private Set<Thumbnail> createThumbnails(VideoResult videoResult)
	{
		SortedSet<Thumbnail> thumbnails = new TreeSet<Thumbnail>();
		thumbnails.add(new Thumbnail(videoResult.getTbUrl(),
		                             videoResult.getTbWidth(),
		                             videoResult.getTbHeight()));
		return thumbnails;
	}
	

	private String createType(GsearchResultClass gsearchResultClass)
	{
		switch (gsearchResultClass)
		{
			case GblogSearch:
				return Query.CT_TEXT;
			case GbookSearch:
				return Query.CT_TEXT;
			case GimageSearch:
				return Query.CT_IMAGE;
			case GnewsSearch:
				return Query.CT_TEXT;
			case GvideoSearch:
				return Query.CT_VIDEO;
			case GwebSearch:
				return Query.CT_TEXT;
			default:
				return null;
		}
	}
	

	private QueryResult getImageResults(Query query,
	                                    AuthCredentials authCredentials)
	{
		notNull(query, "query");
		QueryResult queryResult = new QueryResult(query);
		GoogleSearchQueryFactory factory = GoogleSearchQueryFactory.newInstance(API_KEY);
		int start = 0;
		while (start < Math.min(MAX_RESULT_COUNT, query.getResultCount()))
		{
			ImageSearchQuery imageSearchQuery = factory.newImageSearchQuery();
			GoogleSearchQuery<ImageResult> googleSearchQuery = imageSearchQuery.withResultSetSize(ResultSetSize.LARGE);
			googleSearchQuery = googleSearchQuery.withStartIndex(start);
			googleSearchQuery = googleSearchQuery.withLocale(Locale.US);
			googleSearchQuery = googleSearchQuery.withQuery(query.getQuery());
			try
			{
				PagedList<ImageResult> imageResultResponse = googleSearchQuery.list();
				QueryResult partialQueryResult = convertImageResultResponse(query,
				                                                            imageResultResponse,
				                                                            start);
				if (imageResultResponse.size() == 0)
				{
					break;
				}
				queryResult.addQueryResult(partialQueryResult);
				queryResult.setTotalResultCount(partialQueryResult.getTotalResultCount());
				start += imageResultResponse.size();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return queryResult;
			}
		}
		return queryResult;
	}
	

	private QueryResult getVideoResults(Query query,
	                                    AuthCredentials authCredentials)
	{
		notNull(query, "query");
		QueryResult queryResult = new QueryResult(query);
		GoogleSearchQueryFactory factory = GoogleSearchQueryFactory.newInstance(API_KEY);
		int start = 0;
		while (start < Math.min(MAX_RESULT_COUNT, query.getResultCount()))
		{
			VideoSearchQuery videoSearchQuery = factory.newVideoSearchQuery();
			GoogleSearchQuery<VideoResult> googleSearchQuery = videoSearchQuery.withResultSetSize(ResultSetSize.LARGE);
			googleSearchQuery = googleSearchQuery.withStartIndex(start);
			googleSearchQuery = googleSearchQuery.withLocale(Locale.US);
			googleSearchQuery = googleSearchQuery.withQuery(query.getQuery());
			try
			{
				PagedList<VideoResult> imageResultResponse = googleSearchQuery.list();
				QueryResult partialQueryResult = convertVideoResultResponse(query,
				                                                            imageResultResponse,
				                                                            start);
				if (imageResultResponse.size() == 0)
				{
					break;
				}
				queryResult.addQueryResult(partialQueryResult);
				queryResult.setTotalResultCount(partialQueryResult.getTotalResultCount());
				start += imageResultResponse.size();
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return queryResult;
			}
		}
		return queryResult;
	}
	

	private QueryResult getWebResults(Query query,
	                                  AuthCredentials authCredentials)
	{
		notNull(query, "query");
		QueryResult queryResult = new QueryResult(query);
		GoogleSearchQueryFactory factory = GoogleSearchQueryFactory.newInstance(API_KEY);
		WebSearchQuery webSearchQuery = factory.newWebSearchQuery();
		int start = 0;
		while (start < Math.min(MAX_RESULT_COUNT, query.getResultCount()))
		{
			GoogleSearchQuery<WebResult> googleSearchQuery = webSearchQuery.withResultSetSize(ResultSetSize.LARGE);
			googleSearchQuery = googleSearchQuery.withStartIndex(start);
			googleSearchQuery = googleSearchQuery.withLocale(Locale.US);
			googleSearchQuery = googleSearchQuery.withQuery(query.getQuery());
			googleSearchQuery.getRequestHeaders().put("Referer",
			                                          "http://***REMOVED***");
			try
			{
				PagedList<WebResult> webResultResponse = googleSearchQuery.list();
				QueryResult partialQueryResult = convertWebResultResponse(query,
				                                                          webResultResponse,
				                                                          start);
				if (webResultResponse.size() == 0)
				{
					break;
				}
				queryResult.addQueryResult(partialQueryResult);
				queryResult.setTotalResultCount(partialQueryResult.getTotalResultCount());
				start += webResultResponse.size();
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return queryResult;
			}
		}
		return queryResult;
	}
	

	public static void main(String[] args)
	    throws Exception
	{
		long time = System.currentTimeMillis();
		File configFile = new File("connector-config.xml");
		Configuration configuration = new Configuration(new FileInputStream(configFile));
		GoogleConnector gc = new GoogleConnector(configuration);
		QueryFactory queryFactory = new QueryFactory();
		Query query = queryFactory.createQuery("people");
		query.addContentType(Query.CT_TEXT);
		query.addContentType(Query.CT_VIDEO);
		query.addContentType(Query.CT_IMAGE);
		query.setResultCount(10);
		QueryResult queryResult = gc.get(query, null);
		System.out.println(queryResult.getTotalResultCount());
		System.out.println(queryResult.getResultItems().size());
		System.out.println(System.currentTimeMillis() - time);
	}
}
