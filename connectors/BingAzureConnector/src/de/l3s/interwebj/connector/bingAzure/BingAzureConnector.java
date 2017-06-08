package de.l3s.interwebj.connector.bingAzure;

import static de.l3s.interwebj.util.Assertions.notNull;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import de.l3s.bingService.models.BingResponse;
import de.l3s.bingService.models.Image;
import de.l3s.bingService.models.ImageHolder;
import de.l3s.bingService.models.WebPage;
import de.l3s.bingService.models.WebPagesMainHolder;
import de.l3s.bingService.models.query.BingQuery;
import de.l3s.bingService.services.BingApiService;
import de.l3s.interwebj.AuthCredentials;
import de.l3s.interwebj.InterWebException;
import de.l3s.interwebj.Parameters;
import de.l3s.interwebj.config.Configuration;
import de.l3s.interwebj.core.AbstractServiceConnector;
import de.l3s.interwebj.core.Environment;
import de.l3s.interwebj.core.ServiceConnector;
import de.l3s.interwebj.query.Query;
import de.l3s.interwebj.query.QueryResult;
import de.l3s.interwebj.query.ResultItem;
import de.l3s.interwebj.query.Thumbnail;
import de.l3s.interwebj.query.UserSocialNetworkResult;
import de.l3s.interwebj.socialsearch.SocialSearchQuery;
import de.l3s.interwebj.socialsearch.SocialSearchResult;

public class BingAzureConnector extends AbstractServiceConnector
{
    public BingAzureConnector()
    {
	super();
    }

    public BingAzureConnector(Configuration configuration)
    {
	this(configuration, null);
    }

    public BingAzureConnector(de.l3s.interwebj.config.Configuration configuration, AuthCredentials consumerAuthCredentials)
    {
	super(configuration);
	setAuthCredentials(consumerAuthCredentials);
    }

    @Override
    public Parameters authenticate(String callbackUrl) throws InterWebException
    {
	// authentication is not supported. do nothing
	return null;
    }

    @Override
    public ServiceConnector clone()
    {
	return new BingAzureConnector(getConfiguration(), getAuthCredentials());
    }

    @Override
    public AuthCredentials completeAuthentication(Parameters params) throws InterWebException
    {
	// authentication is not supported. do nothing
	return null;
    }

    @Override
    public QueryResult get(Query query, AuthCredentials authCredentials) throws InterWebException
    {
	notNull(query, "query");

	authCredentials = getAuthCredentials();
	Environment.logger.info("crendet " + authCredentials.getKey());

	try
	{
	    QueryResult results = new QueryResult(query);

	    long totalResultCount = 0;
	    BingResponse response = getWeb(query, authCredentials);

	    if(query.getContentTypes().contains(Query.CT_TEXT))
	    {
		QueryResult queryResult = new QueryResult(null);

		WebPagesMainHolder pages = response.getWebPages();
		int index = 1;
		int intResultCount = 0;
		try
		{
		    long totalPages = Long.parseLong(pages.getTotalEstimatedMatches());
		    totalResultCount += totalPages;

		    if(totalPages > Integer.MAX_VALUE)
			intResultCount = 2100000000;
		    else
			intResultCount = (int) totalPages;
		}
		catch(Exception e)
		{
		}

		if(pages != null)
		{
		    for(WebPage page : pages.getValue())
		    {
			ResultItem resultItem = new ResultItem(getName());
			resultItem.setType(Query.CT_TEXT);
			resultItem.setTitle(page.getName());
			resultItem.setDescription(page.getSnippet());
			resultItem.setUrl(page.getUrl());
			resultItem.setRank(index++);
			resultItem.setTotalResultCount(intResultCount);

			queryResult.addResultItem(resultItem);
		    }
		    results.addQueryResult(queryResult);

		    if(pages.getValue().size() == 0)
			Environment.logger.warning("No text results found; response: " + response.getJsonContent());
		}
		else
		    Environment.logger.warning("Result pages are null");
	    }

	    if(query.getContentTypes().contains(Query.CT_IMAGE))
	    {
		ImageHolder images = response.getImages();

		if(images == null)
		    Environment.logger.warning("images is null");
		else
		{
		    QueryResult queryResult = new QueryResult(null);
		    int intResultCount = 100;
		    int index = 1;

		    if(images.getValue().size() == 0)
			Environment.logger.warning("No image results found; response: " + response.getJsonContent());

		    for(Image image : images.getValue())
		    {
			ResultItem resultItem = new ResultItem(getName());
			resultItem.setType(Query.CT_IMAGE);
			resultItem.setTitle(image.getMedia().getName());
			resultItem.setUrl(image.getMedia().getContentUrl());
			resultItem.setRank(index++);
			resultItem.setTotalResultCount(intResultCount);

			Set<Thumbnail> thumbnails = new LinkedHashSet<Thumbnail>();

			String url = null;
			Integer width = null;
			Integer height = null;

			try
			{
			    url = image.getMedia().getThumbnailUrl();
			    width = Integer.parseInt(image.getMedia().getThumbnail().getWidth());
			    height = Integer.parseInt(image.getMedia().getThumbnail().getHeight());

			    if(url != null && height != null && width != null)
			    {
				thumbnails.add(new Thumbnail(url, width, height));

				resultItem.setEmbeddedSize1("<img src=\"" + url + "\" height=\"" + height + "\" width=\"" + width + "\"/>");

			    }
			}
			catch(Exception e)
			{
			    Environment.logger.warning(e.getMessage());
			}

			try
			{
			    url = image.getMedia().getContentUrl();
			    width = Integer.parseInt(image.getMedia().getWidth());
			    height = Integer.parseInt(image.getMedia().getHeight());

			    if(url != null && height != null && width != null)
			    {
				thumbnails.add(new Thumbnail(url, width, height));
			    }
			}
			catch(Exception e)
			{
			    Environment.logger.warning(e.getMessage());
			}

			resultItem.setThumbnails(thumbnails);

			queryResult.addResultItem(resultItem);
		    }
		    results.addQueryResult(queryResult);
		}
	    }
	    results.setTotalResultCount(totalResultCount);
	    return results;
	}
	catch(Exception e)
	{
	    throw new InterWebException(e);
	}
    }

    private BingResponse getWeb(Query query, AuthCredentials authCredentials) throws InterWebException, UnsupportedOperationException, IOException
    {
	int count = query.getResultCount();//>20? query.getResultCount():20; // min 20 results per request

	BingQuery bingQuery = new BingQuery();
	bingQuery.setQuery(query.getQuery());
	bingQuery.setCount(count);
	bingQuery.setOffset((query.getPage() - 1) * count);
	bingQuery.setMarket(createMarket(query.getLanguage()));

	return new BingApiService(authCredentials.getKey()).getResponseFromBingApi(bingQuery);

    }

    @Override
    public String getEmbedded(AuthCredentials authCredentials, String url, int maxWidth, int maxHeight) throws InterWebException
    {
	return null;
    }

    @Override
    public String getUserId(AuthCredentials authCredentials) throws InterWebException
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
    public ResultItem put(byte[] data, String contentType, Parameters params, AuthCredentials authCredentials) throws InterWebException
    {
	// not supported. do nothing
	return null;
    }

    @Override
    public void revokeAuthentication() throws InterWebException
    {
	// not supported. do nothing
    }

    private static String createMarket(String language)
    {
	if(language.equalsIgnoreCase("ar"))
	    return "ar-XA";
	if(language.equalsIgnoreCase("bg"))
	    return "bg-BG";
	if(language.equalsIgnoreCase("cs"))
	    return "cs-CZ";
	if(language.equalsIgnoreCase("da"))
	    return "da-DK";
	if(language.equalsIgnoreCase("de"))
	    return "de-DE";
	if(language.equalsIgnoreCase("el"))
	    return "el-GR";
	if(language.equalsIgnoreCase("es"))
	    return "es-ES";
	if(language.equalsIgnoreCase("et"))
	    return "et-EE";
	if(language.equalsIgnoreCase("fi"))
	    return "fi-FI";
	if(language.equalsIgnoreCase("fr"))
	    return "fr-FR";
	if(language.equalsIgnoreCase("he"))
	    return "he-IL";
	if(language.equalsIgnoreCase("hr"))
	    return "hr-HR";
	if(language.equalsIgnoreCase("hu"))
	    return "hu-HU";
	if(language.equalsIgnoreCase("it"))
	    return "it-IT";
	if(language.equalsIgnoreCase("ja"))
	    return "ja-JP";
	if(language.equalsIgnoreCase("ko"))
	    return "ko-KR";
	if(language.equalsIgnoreCase("lt"))
	    return "lt-LT";
	if(language.equalsIgnoreCase("lv"))
	    return "lv-LV";
	if(language.equalsIgnoreCase("nb"))
	    return "nb-NO";
	if(language.equalsIgnoreCase("nl"))
	    return "nl-NL";
	if(language.equalsIgnoreCase("pl"))
	    return "pl-PL";
	if(language.equalsIgnoreCase("pt"))
	    return "pt-PT";
	if(language.equalsIgnoreCase("ro"))
	    return "ro-RO";
	if(language.equalsIgnoreCase("ru"))
	    return "ru-RU";
	if(language.equalsIgnoreCase("sk"))
	    return "sk-SK";
	if(language.equalsIgnoreCase("sl"))
	    return "sl-SL";
	if(language.equalsIgnoreCase("sv"))
	    return "sv-SE";
	if(language.equalsIgnoreCase("th"))
	    return "th-TH";
	if(language.equalsIgnoreCase("tr"))
	    return "tr-TR";
	if(language.equalsIgnoreCase("uk"))
	    return "uk-UA";
	if(language.equalsIgnoreCase("zh"))
	    return "zh-CN";
	return "en-US";
    }

    @Override
    public Set<String> getTags(String username, int maxCount) throws IllegalArgumentException, IOException
    {
	throw new RuntimeException("not implemented");
    }

    @Override
    public Set<String> getUsers(Set<String> tags, int maxCount) throws IOException, InterWebException
    {
	throw new InterWebException("not implemented");
    }

    @Override
    public UserSocialNetworkResult getUserSocialNetwork(String userid, AuthCredentials authCredentials) throws InterWebException
    {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public SocialSearchResult get(SocialSearchQuery query, AuthCredentials authCredentials)
    {
	// TODO Auto-generated method stub
	return null;
    }
}
