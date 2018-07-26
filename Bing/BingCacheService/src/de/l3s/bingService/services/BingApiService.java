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

import de.l3s.bingService.JsonParser;
import de.l3s.bingService.models.BingResponse;
import de.l3s.bingService.models.query.BingQuery;

public class BingApiService implements BingRequestConstants {
	private final static Logger log = LogManager.getLogger(BingApiService.class);

	private static String readJsonResponseAsString(BufferedReader rd) throws IOException {
		StringBuilder result = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}

	private static HttpUriRequest createRequest(BingQuery bingUrl, String clientKey) {
		HttpGet request = null;
		try {
			request = new HttpGet(createUri(bingUrl));
		} catch (URISyntaxException e) {
			log.error("URI syntax exception:", e);
		}
		request.addHeader(KEY_HEADER_NAME, clientKey);

		return request;
	}

	private static URI createUri(BingQuery bingUrl) throws URISyntaxException {
		URIBuilder builder = new URIBuilder();
		builder.setScheme(SCHEME).setHost(HOST).setPath(PATH).setParameter(PARAMETER_QUERY, bingUrl.getQuery());

		builder.setParameter(PARAMETER_COUNT, bingUrl.getCount() + "");
		builder.setParameter(PARAMETER_OFFSET, bingUrl.getOffset() + "");
		builder.setParameter(PARAMETER_TEXT_FORMAT, PARAMETER_VALUE_HTML);
		builder.setParameter(PARAMETER_TEXT_DECORATIONS, PARAMETER_VALUE_TRUE);

		if (bingUrl.hasMarket()) {
			builder.setParameter(PARAMETER_MKT, bingUrl.getMarket());
		}
		if (bingUrl.hasSafesearch()) {
			builder.setParameter(PARAMETER_SAFESEARCH, bingUrl.getSafesearch().getValue());
		}
		if (bingUrl.hasFreshness()) {
			builder.setParameter(PARAMETER_FRESHNESS, bingUrl.getFreshness().getValue());
		}
		if (bingUrl.hasResponseFilter()) {
			builder.setParameter(PARAMETER_RESPONSE_FILTER, bingUrl.getResponseFilter().getValue());
		}
		if (bingUrl.hasLanguage()) {
			builder.setParameter(PARAMETER_LANGUAGE, bingUrl.getLanguage());
		}

		return builder.build();
	}

	public static BingResponse stringToBingResponse(String stringResponse) throws UnsupportedOperationException, IOException {
		BingResponse bingResponse = JsonParser.fromJson(stringResponse);
		bingResponse.setJsonContent(stringResponse);

		return bingResponse;
	}

	public static String getResponseString(String query, String mkt, String lang, int offset, String freshness, String safeSearch, String clientKey){
		BingQuery q = new BingQuery(query, mkt, lang, offset, freshness, safeSearch);
		HttpUriRequest queryRequest = createRequest(q, clientKey);

		HttpClient client = HttpClientBuilder.create().build();

		HttpResponse response;
		try {
			response = client.execute(queryRequest);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			return readJsonResponseAsString(reader);

		} catch (IOException e) {
			log.error("Error during HTTP request. Error message: ",e);
		}

		return null;
	}

}
