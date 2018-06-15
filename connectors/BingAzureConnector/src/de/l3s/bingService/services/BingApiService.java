package de.l3s.bingService.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import de.l3s.bingService.JsonParser;
import de.l3s.bingService.models.BingResponse;
import de.l3s.bingService.models.Image;
import de.l3s.bingService.models.ImageHolder;
import de.l3s.bingService.models.WebPage;
import de.l3s.bingService.models.WebPagesMainHolder;
import de.l3s.bingService.models.query.BingQuery;

public class BingApiService implements BingRequestConstants
{

    public static void main(String[] args) throws UnsupportedOperationException, IOException
    {
	BingQuery query = new BingQuery();
	query.setQuery("auto");
	query.setMarket("en-US");
	query.setCount(10);
	query.setOffset(0);

	// "6e4a0b0056fd4d308dfc2e54763e1a5c"; kemkes
	// "2182cb79d51e4fd3a8df9df7982f167c"; maria

	BingResponse response = new BingApiService("195f994bbef145c587ae13df58b33c9a").getResponseFromBingApi(query);

	WebPagesMainHolder pages = response.getWebPages();
	System.out.println("web pages: " + pages.getTotalEstimatedMatches());

	int counter = 1;
	for(WebPage page : pages.getValue())
	{
	    System.out.println(page);
	    System.out.println(counter++);
	}

	System.out.println("-----------");
	ImageHolder images = response.getImages();

	if(images != null)
	{
	    for(Image image : images.getValue())
	    {
		System.out.println(image.getContentSize());
		System.out.println(image.getMedia());
	    }
	}

	System.out.println(response.getJsonContent());

    }

    private String apiKey;
    private String rawBingResponse;

    public BingApiService(String apiKey)
    {
	super();
	this.apiKey = apiKey;
    }

    public BingResponse getResponseFromBingApi(BingQuery bingQuery) throws UnsupportedOperationException, IOException
    {
	// logger.debug("Receiving json from bing api...");
	HttpResponse response = null;
	HttpClient client = HttpClientBuilder.create().build();

	response = client.execute(createRequest(bingQuery));

	BingResponse bingResponse = null;
	BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

	rawBingResponse = readJsonResponseAsString(reader);
	
	bingResponse = JsonParser.fromJson(rawBingResponse);
	bingResponse.setJsonContent(rawBingResponse);
	response.getAllHeaders();

	reader.close();

	return bingResponse;
    }
    
    public String getRawBingResponse()
    {
        return rawBingResponse;
    }

    private String readJsonResponseAsString(BufferedReader rd) throws IOException
    {
	StringBuilder result = new StringBuilder();
	String line;
	while((line = rd.readLine()) != null)
	{
	    result.append(line);
	}
	return result.toString();
    }

    private HttpUriRequest createRequest(BingQuery bingUrl)
    {
	HttpGet request = null;
	try
	{
	    request = new HttpGet(createUri(bingUrl));
	}
	catch(URISyntaxException e)
	{
	    // logger
	}
	request.addHeader(KEY_HEADER_NAME, apiKey);
	// request.addHeader(X_MSEDGE_CLIENT_ID_HEADER, CLIENT_ID);
	return request;
    }

    private URI createUri(BingQuery bingUrl) throws URISyntaxException
    {
	URIBuilder builder = new URIBuilder();
	builder.setScheme(SCHEME).setHost(HOST).setPath(PATH).setParameter(PARAMETER_QUERY, bingUrl.getQuery());

	builder.setParameter(PARAMETER_COUNT, bingUrl.getCount() + "");
	builder.setParameter(PARAMETER_OFFSET, bingUrl.getOffset() + "");
	builder.setParameter(PARAMETER_TEXT_FORMAT, PARAMETER_VALUE_HTML);
	builder.setParameter(PARAMETER_TEXT_DECORATIONS, PARAMETER_VALUE_TRUE);

	if(bingUrl.hasMarket())
	{
	    builder.setParameter(PARAMETER_MKT, bingUrl.getMarket());
	}
	if(bingUrl.hasSafesearch())
	{
	    builder.setParameter(PARAMETER_SAFESEARCH, bingUrl.getSafesearch().getValue());
	}
	if(bingUrl.hasFreshness())
	{
	    builder.setParameter(PARAMETER_FRESHNESS, bingUrl.getFreshness().getValue());
	}
	if(bingUrl.hasResponseFilter())
	{
	    builder.setParameter(PARAMETER_RESPONSE_FILTER, bingUrl.getResponseFilter().getValue());
	}
	if(bingUrl.hasLanguage())
	{
	    builder.setParameter(PARAMETER_LANGUAGE, bingUrl.getLanguage());
	}
	return builder.build();
    }

}
