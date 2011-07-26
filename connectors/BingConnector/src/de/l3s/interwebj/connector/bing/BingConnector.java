package de.l3s.interwebj.connector.bing;


import static de.l3s.interwebj.util.Assertions.notNull;

import java.io.File;
import java.io.FileInputStream;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.code.bing.search.client.BingSearchClient;
import com.google.code.bing.search.client.BingSearchClient.SearchRequestBuilder;
import com.google.code.bing.search.client.BingSearchServiceClientFactory;
import com.google.code.bing.search.schema.AdultOption;
import com.google.code.bing.search.schema.SearchOption;
import com.google.code.bing.search.schema.SearchResponse;
import com.google.code.bing.search.schema.SourceType;
import com.google.code.bing.search.schema.multimedia.ImageResult;
import com.google.code.bing.search.schema.multimedia.VideoResult;
import com.google.code.bing.search.schema.web.WebResult;
import com.google.code.bing.search.schema.web.WebSearchOption;

import de.l3s.interwebj.AuthCredentials;
import de.l3s.interwebj.InterWebException;
import de.l3s.interwebj.Parameters;
import de.l3s.interwebj.config.Configuration;
import de.l3s.interwebj.core.AbstractServiceConnector;
import de.l3s.interwebj.core.ServiceConnector;
import de.l3s.interwebj.query.Query;
import de.l3s.interwebj.query.QueryFactory;
import de.l3s.interwebj.query.QueryResult;
import de.l3s.interwebj.query.ResultItem;
import de.l3s.interwebj.query.Thumbnail;


public class BingConnector
    extends AbstractServiceConnector
{
	
	private static final int MAX_RESULT_COUNT = 50;
	
	private static final String APP_ID = "E42FC069A142DB0CE26C4B3C428854F7C49DA01F";
	

	public BingConnector(Configuration configuration)
	{
		this(configuration, null);
	}
	

	public BingConnector(de.l3s.interwebj.config.Configuration configuration,
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
		return new BingConnector(getConfiguration(), getAuthCredentials());
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
		//if (query.getContentTypes().contains(Query.CT_TEXT))
		//{
			QueryResult webQueryResult = getWebResults(query, authCredentials);
			queryResult.addQueryResult(webQueryResult);
		//}
		/*
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
		}*/
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
	
/*
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
	
*/
	private ResultItem convertWebResult(WebResult webResult,
            int index,
            long totalResultCount)
	{
		ResultItem resultItem = new ResultItem(getName());
		resultItem.setType(Query.CT_TEXT);
		if(webResult.getTitle() != null)
		resultItem.setTitle(webResult.getTitle().replace(""+(char)57344, "<b>").replace(""+(char)57345, "</b>"));
		if(webResult.getDescription() != null)
			resultItem.setDescription(webResult.getDescription().replace(""+(char)57344, "<b>").replace(""+(char)57345, "</b>"));
		resultItem.setUrl(webResult.getUrl());
		resultItem.setRank(index);
		resultItem.setTotalResultCount(totalResultCount);
		return resultItem;
	}
	
	private ResultItem convertImageResult(ImageResult imageResult,
            int index,
            long totalResultCount)
	{
		ResultItem resultItem = new ResultItem(getName());
		resultItem.setType(Query.CT_IMAGE);
		resultItem.setTitle(imageResult.getTitle());
		resultItem.setUrl(imageResult.getUrl());
		resultItem.setRank(index);
		resultItem.setTotalResultCount(totalResultCount);
		resultItem.setThumbnails(createThumbnails(imageResult.getThumbnail()));

		return resultItem;
	}
	
	private ResultItem convertVideoResult(VideoResult videoResult,
            int index,
            long totalResultCount)
	{
		ResultItem resultItem = new ResultItem(getName());
		resultItem.setType(Query.CT_VIDEO);
		resultItem.setTitle(videoResult.getTitle());
		resultItem.setUrl(videoResult.getPlayUrl());
		resultItem.setRank(index);
		resultItem.setTotalResultCount(totalResultCount);
		resultItem.setThumbnails(createThumbnails(videoResult.getStaticThumbnail()));

		return resultItem;
	}
	

	private Set<Thumbnail> createThumbnails(com.google.code.bing.search.schema.multimedia.StaticThumbnail tn)
	{
		SortedSet<Thumbnail> thumbnails = new TreeSet<Thumbnail>();
		
		//com.google.code.bing.search.schema.multimedia.Thumbnail tn = imageResult.getThumbnail();
		thumbnails.add(new Thumbnail(tn.getUrl(),
		                             tn.getWidth().intValue(),
		                             tn.getHeight().intValue()));
		return thumbnails;
	}
	
	private Set<Thumbnail> createThumbnails(com.google.code.bing.search.schema.multimedia.Thumbnail tn)
	{
		SortedSet<Thumbnail> thumbnails = new TreeSet<Thumbnail>();
		
		//com.google.code.bing.search.schema.multimedia.Thumbnail tn = imageResult.getThumbnail();
		thumbnails.add(new Thumbnail(tn.getUrl(),
		                             tn.getWidth().intValue(),
		                             tn.getHeight().intValue()));
		return thumbnails;
	}
	
	private QueryResult getWebResults(Query query,
	                                  AuthCredentials authCredentials)
	{
		notNull(query, "query");
		QueryResult queryResult = new QueryResult(query);
		
		BingSearchServiceClientFactory factory = BingSearchServiceClientFactory.newInstance();
		BingSearchClient client = factory.createBingSearchClient();

		SearchRequestBuilder builder = client.newSearchRequestBuilder();
		builder.withAppId(APP_ID);
		builder.withQuery(query.getQuery());
		
		builder.withVersion("2.0");
		builder.withMarket("en-us");
		builder.withAdultOption(AdultOption.MODERATE);
		builder.withSearchOption(SearchOption.ENABLE_HIGHLIGHTING);
		
		
		if (query.getContentTypes().contains(Query.CT_TEXT))
			builder.withSourceType(SourceType.WEB);
		if (query.getContentTypes().contains(Query.CT_IMAGE)) 
			builder.withSourceType(SourceType.IMAGE);
		if (query.getContentTypes().contains(Query.CT_VIDEO))
			builder.withSourceType(SourceType.VIDEO);		
		
		long requestCount = (long) query.getResultCount();
		long requestOffset = (query.getPage()-1)*(long) query.getResultCount();
		
		if(requestCount > 50L)
			requestCount = 50L;
		
		builder.withWebRequestCount(requestCount);
		builder.withWebRequestOffset(requestOffset); 
		
		builder.withVideoRequestCount(requestCount);
		builder.withVideoRequestOffset(requestOffset);
		
		builder.withImageRequestCount(requestCount);
		builder.withImageRequestOffset(requestOffset);
		
		builder.withWebRequestSearchOption(WebSearchOption.DISABLE_HOST_COLLAPSING);
		builder.withWebRequestSearchOption(WebSearchOption.DISABLE_QUERY_ALTERATIONS);

		SearchResponse response = client.search(builder.getResult());

		long totalResultCount = 0;
		int index = 0;
		
		if(response.getWeb() != null)
		{
			totalResultCount = response.getWeb().getTotal();
			queryResult.setTotalResultCount(totalResultCount);
			
			for (WebResult result : response.getWeb().getResults()) 
			{
				queryResult.addResultItem(convertWebResult(result, index, totalResultCount));
				index++;
			}
		}
		
		if(response.getImage() != null)
		{
			totalResultCount += response.getImage().getTotal();
			queryResult.setTotalResultCount(totalResultCount);
			index = 0;
			for (ImageResult result : response.getImage().getResults()) 
			{
				queryResult.addResultItem(convertImageResult(result, index, totalResultCount));
				index++;
			}
		}
		
		if(response.getVideo() != null)
		{
			totalResultCount += response.getVideo().getTotal();
			queryResult.setTotalResultCount(totalResultCount);
			index = 0;
			for (VideoResult result : response.getVideo().getResults()) 
			{
				queryResult.addResultItem(convertVideoResult(result, index, totalResultCount));
				index++;
			}
		}
		
		
		//GoogleSearchQueryFactory factory = GoogleSearchQueryFactory.newInstance(API_KEY);
		//WebSearchQuery webSearchQuery = factory.newWebSearchQuery();
		/*
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
		}*/
		return queryResult;
	}
	

	public static void main(String[] args)
	    throws Exception
	{
		long time = System.currentTimeMillis();
		File configFile = new File("connector-config.xml");
		Configuration configuration = new Configuration(new FileInputStream(configFile));
		BingConnector gc = new BingConnector(configuration);
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
