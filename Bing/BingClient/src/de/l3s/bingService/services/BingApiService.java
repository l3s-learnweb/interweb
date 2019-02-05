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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonSyntaxException;

import de.l3s.bingService.JsonParser;
import de.l3s.bingService.models.BingResponse;
import de.l3s.bingService.models.Image;
import de.l3s.bingService.models.ImageHolder;
import de.l3s.bingService.models.WebPage;
import de.l3s.bingService.models.WebPagesMainHolder;
import de.l3s.bingService.models.query.BingQuery;

public class BingApiService implements BingRequestConstants
{
    private final static Logger log = LogManager.getLogger(BingApiService.class);

    public static void main(String[] args) throws UnsupportedOperationException, IOException
    {
	BingQuery query = new BingQuery();
	query.setQuery("obama");
	query.setMarket("en-US");
	query.setCount(7);
	query.setOffset(50);

	BingResponse response = new BingApiService("***REMOVED***").getResponseFromBingApi(query);

	WebPagesMainHolder pages = response.getWebPages();
	log.debug("web pages: " + pages.getTotalEstimatedMatches());

	int counter = 1;
	for(WebPage page : pages.getValue())
	{
	    log.debug(page);
	    log.debug(counter++);
	}

	log.debug("-----------");
	ImageHolder images = response.getImages();

	counter = 1;
	if(images != null)
	{
	    for(Image image : images.getValue())
	    {
		log.debug(image.getContentSize());
		log.debug(image.getMedia());
		log.debug(counter++);
	    }
	}

	log.debug(response.getJsonContent());

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

	try
	{
	    bingResponse = JsonParser.fromJson(rawBingResponse);
	}
	catch(JsonSyntaxException e)
	{
	    log.fatal("Can't parse response: "+rawBingResponse+"\nFor BingQuery: "+bingQuery);
	    throw e;
	}
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
	URI uri = builder.build();
	log.debug("Request Uri {}", uri);
	return uri;
    }

}
