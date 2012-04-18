package de.l3s.interwebj.connector.bing;


import static de.l3s.interwebj.util.Assertions.notNull;

import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.NotImplementedException;

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
import de.l3s.interwebj.query.QueryResult;
import de.l3s.interwebj.query.ResultItem;
import de.l3s.interwebj.query.Thumbnail;


public class BingConnector
    extends AbstractServiceConnector
{
	
	private static final long MAX_RESULT_COUNT = 50L;	
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
		
		BingSearchServiceClientFactory factory = BingSearchServiceClientFactory.newInstance();
		BingSearchClient client = factory.createBingSearchClient();

		SearchRequestBuilder builder = client.newSearchRequestBuilder();
		builder.withAppId(APP_ID);
		builder.withQuery(query.getQuery());		
		builder.withVersion("2.0");
		builder.withMarket(createMarket(query.getLanguage()));
		builder.withAdultOption(AdultOption.STRICT);
		builder.withSearchOption(SearchOption.ENABLE_HIGHLIGHTING);		
		
		boolean supportedContent = false;
		if (query.getContentTypes().contains(Query.CT_TEXT))
		{
			builder.withSourceType(SourceType.WEB);
			supportedContent = true;
		}
		if (query.getContentTypes().contains(Query.CT_IMAGE)) 
		{
			builder.withSourceType(SourceType.IMAGE);
			supportedContent = true;
		}
		if (query.getContentTypes().contains(Query.CT_VIDEO))
		{
			builder.withSourceType(SourceType.VIDEO);	
			supportedContent = true;
		}	
		if(!supportedContent)
			return queryResult;
		
		long requestCount = (long) query.getResultCount();
		long requestOffset = (query.getPage()-1)*(long) query.getResultCount();

		if(requestCount > MAX_RESULT_COUNT)
			requestCount = MAX_RESULT_COUNT;
		
		builder.withWebRequestCount(requestCount);
		builder.withWebRequestOffset(requestOffset); 
		
		builder.withVideoRequestCount(requestCount);
		builder.withVideoRequestOffset(requestOffset);
		
		builder.withImageRequestCount(requestCount);
		builder.withImageRequestOffset(requestOffset);
		
		//builder.withWebRequestSearchOption(WebSearchOption.DISABLE_HOST_COLLAPSING);
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
	public ResultItem put(byte[] data,
	                String contentType,
	                Parameters params,
	                AuthCredentials authCredentials)
	    throws InterWebException
	{
		// not supported. do nothing
		return null;
	}
	

	@Override
	public void revokeAuthentication()
	    throws InterWebException
	{
		// not supported. do nothing
	}

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
	

	private static Set<Thumbnail> createThumbnails(com.google.code.bing.search.schema.multimedia.StaticThumbnail tn)
	{
		SortedSet<Thumbnail> thumbnails = new TreeSet<Thumbnail>();
		
		thumbnails.add(new Thumbnail(tn.getUrl(),
		                             tn.getWidth().intValue(),
		                             tn.getHeight().intValue()));
		return thumbnails;
	}
	
	private static Set<Thumbnail> createThumbnails(com.google.code.bing.search.schema.multimedia.Thumbnail tn)
	{
		SortedSet<Thumbnail> thumbnails = new TreeSet<Thumbnail>();
		
		thumbnails.add(new Thumbnail(tn.getUrl(),
		                             tn.getWidth().intValue(),
		                             tn.getHeight().intValue()));
		return thumbnails;
	}
	
	private static String createMarket(String language) 
	{
		if(language.equalsIgnoreCase("ar")) return "ar-XA";
		if(language.equalsIgnoreCase("bg")) return "bg-BG";
		if(language.equalsIgnoreCase("cs")) return "cs-CZ";
		if(language.equalsIgnoreCase("da")) return "da-DK";
		if(language.equalsIgnoreCase("de")) return "de-DE";
		if(language.equalsIgnoreCase("el")) return "el-GR";
		if(language.equalsIgnoreCase("es")) return "es-ES";
		if(language.equalsIgnoreCase("et")) return "et-EE";
		if(language.equalsIgnoreCase("fi")) return "fi-FI";
		if(language.equalsIgnoreCase("fr")) return "fr-FR";
		if(language.equalsIgnoreCase("he")) return "he-IL";
		if(language.equalsIgnoreCase("hr")) return "hr-HR";
		if(language.equalsIgnoreCase("hu")) return "hu-HU";
		if(language.equalsIgnoreCase("it")) return "it-IT";
		if(language.equalsIgnoreCase("ja")) return "ja-JP";
		if(language.equalsIgnoreCase("ko")) return "ko-KR";
		if(language.equalsIgnoreCase("lt")) return "lt-LT";
		if(language.equalsIgnoreCase("lv")) return "lv-LV";
		if(language.equalsIgnoreCase("nb")) return "nb-NO";
		if(language.equalsIgnoreCase("nl")) return "nl-NL";
		if(language.equalsIgnoreCase("pl")) return "pl-PL";
		if(language.equalsIgnoreCase("pt")) return "pt-PT";
		if(language.equalsIgnoreCase("ro")) return "ro-RO";
		if(language.equalsIgnoreCase("ru")) return "ru-RU";
		if(language.equalsIgnoreCase("sk")) return "sk-SK";
		if(language.equalsIgnoreCase("sl")) return "sl-SL";
		if(language.equalsIgnoreCase("sv")) return "sv-SE";
		if(language.equalsIgnoreCase("th")) return "th-TH";
		if(language.equalsIgnoreCase("tr")) return "tr-TR";
		if(language.equalsIgnoreCase("uk")) return "uk-UA";
		if(language.equalsIgnoreCase("zh")) return "zh-CN";		
		return "en-US";				
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
}
