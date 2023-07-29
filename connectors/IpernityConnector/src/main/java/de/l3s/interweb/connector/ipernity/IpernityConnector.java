package de.l3s.interweb.connector.ipernity;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import jakarta.enterprise.context.Dependent;
import jakarta.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.l3s.interweb.connector.ipernity.entity.Doc;
import de.l3s.interweb.connector.ipernity.entity.IpernityResponse;
import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.search.*;
import de.l3s.interweb.core.util.Assertions;

/**
 * Ipernity is a non-commercial photo sharing community which is financed exclusively by membership dues without any intention of making a profit.
 * TODO missing search implementations: extras, language.
 *
 * @see <a href="http://www.ipernity.com/help/api/method/doc.search">Ipernity Search API</a>
 */
@Dependent
public class IpernityConnector implements SearchConnector {
    private static final Logger log = Logger.getLogger(IpernityConnector.class);
    private static final String IPERNITY_BASE = "http://api.ipernity.com/api/";

    @Override
    public String getName() {
        return "Ipernity";
    }

    @Override
    public String getBaseUrl() {
        return "http://www.ipernity.com/";
    }

    @Override
    public ContentType[] getSearchTypes() {
        return new ContentType[]{ContentType.image};
    }

    /**
     * API Docs:
     * http://www.ipernity.com/help/api/method/doc.search
     */
    @Override
    public SearchConnectorResults search(SearchQuery query, AuthCredentials credentials) throws ConnectorException {
        Assertions.notNull(query, "query");

        SearchConnectorResults queryResult = new SearchConnectorResults();
        UriBuilder uriBuilder = UriBuilder.fromUri(IPERNITY_BASE + "doc.search/json");
        uriBuilder.queryParam("api_key", credentials.getKey());
        if (query.getSearchScope() == SearchScope.text) {
            uriBuilder.queryParam("text", query.getQuery());
        } else if (query.getSearchScope() == SearchScope.tags) {
            uriBuilder.queryParam("tags", query.getQuery());
        }

        uriBuilder.queryParam("media", "photo");
        uriBuilder.queryParam("page", query.getPage());
        uriBuilder.queryParam("per_page", query.getPerPage());
        uriBuilder.queryParam("sort", convertRanking(query.getRanking()));
        uriBuilder.queryParam("thumbsize", "1024"); // 75x, 100, 240, 250x, 500, 560, 640, 800, 1024, 1600 or 2048
        uriBuilder.queryParam("share", "4"); // 4 - only public docs
        uriBuilder.queryParam("extra", "count,dates"); // owner, dates, count, license, medias, geo, original

        if (query.getDateFrom() != null) {
            try {
                uriBuilder.queryParam("created_min", query.getDateFrom());
            } catch (Exception e) {
                log.error("Error parsing from date", e);
            }
        }

        if (query.getDateTill() != null) {
            try {
                uriBuilder.queryParam("created_max", query.getDateTill());
            } catch (Exception e) {
                log.error("Error parsing to date", e);
            }
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(uriBuilder.build()).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            IpernityResponse sr = objectMapper.readValue(response.body(), IpernityResponse.class);

            long totalResultCount = sr.docs.total;
            queryResult.setTotalResults(totalResultCount);
            int count = (sr.docs.page - 1) * sr.docs.perPage;

            List<Doc> docs = sr.docs.doc;
            for (Doc doc : docs) {
                SearchItem resultItem = new SearchItem(count++);
                resultItem.setType(ContentType.image);
                resultItem.setId(Integer.toString(doc.docId));
                resultItem.setTitle(doc.title);
                resultItem.setUrl("http://ipernity.com/doc/" + doc.owner.userId + "/" + doc.docId);
                resultItem.setCommentsCount(doc.count.comments);
                resultItem.setViewsCount(doc.count.visits);

                if (doc.thumb != null) {
                    resultItem.setThumbnailLarge(new Thumbnail(doc.thumb.url, doc.thumb.w, doc.thumb.h));
                    resultItem.setWidth(doc.thumb.w);
                    resultItem.setHeight(doc.thumb.h);
                }

                queryResult.addResultItem(resultItem);
            }

            return queryResult;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Supported values are: relevance, popular, posted-desc, posted-asc.
     */
    private static String convertRanking(SearchRanking ranking) {
        return switch (ranking) {
            case date -> "posted-desc";
            case interestingness -> "popular";
            default -> "relevance";
        };
    }
}
