package de.l3s.interweb.connector.ipernity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.l3s.interweb.connector.ipernity.entity.Doc;
import de.l3s.interweb.connector.ipernity.entity.IpernityResponse;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.search.*;
import de.l3s.interweb.core.util.DateUtils;

@Dependent
public class IpernityConnector implements SearchConnector {
    private static final Logger log = Logger.getLogger(IpernityConnector.class);

    @Inject
    ObjectMapper mapper;

    @RestClient
    IpernitySearchClient searchClient;

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
        return new ContentType[]{ContentType.image, ContentType.audio, ContentType.video};
    }

    @Override
    public SearchConnectorResults search(SearchQuery query) throws ConnectorException {
        try {
            String responseBody = searchClient.searchTextPlain(
                    query.getQuery(),
                    convertContentTypes(query.getContentTypes()),
                    query.getPage(),
                    query.getPerPage(100),
                    convertRanking(query.getRanking()),
                    DateUtils.toEpochSecond(query.getDateFrom()),
                    DateUtils.toEpochSecond(query.getDateTill())
            ).await().indefinitely();

            IpernityResponse response = mapper.readValue(responseBody, IpernityResponse.class);

            SearchConnectorResults queryResult = new SearchConnectorResults();
            long totalResultCount = response.docs().total();
            queryResult.setTotalResults(totalResultCount);
            int count = (response.docs().page() - 1) * response.docs().perPage();

            List<Doc> docs = response.docs().doc();
            for (Doc doc : docs) {
                SearchItem resultItem = new SearchItem(count++);
                resultItem.setType(ContentType.image);
                resultItem.setId(doc.docId());
                resultItem.setTitle(doc.title());
                resultItem.setUrl("http://ipernity.com/doc/" + doc.owner().userId() + "/" + doc.docId());
                resultItem.setCommentsCount(doc.count().comments());
                resultItem.setViewsCount(doc.count().visits());

                if (doc.thumb() != null) {
                    resultItem.setThumbnailLarge(new Thumbnail(doc.thumb().url(), doc.thumb().width(), doc.thumb().height()));
                    resultItem.setWidth(doc.thumb().width());
                    resultItem.setHeight(doc.thumb().height());
                }

                queryResult.addResultItem(resultItem);
            }

            return queryResult;
        } catch (Exception e) {
            throw new ConnectorException("Error while processing response", e);
        }
    }

    private static String convertContentTypes(Set<ContentType> contentTypes) {
        ArrayList<String> media = new ArrayList<>();
        if (contentTypes.contains(ContentType.image)) {
            media.add("photo"); // photo, audio, video, other
        }
        if (contentTypes.contains(ContentType.audio)) {
            media.add("audio");
        }
        if (contentTypes.contains(ContentType.video)) {
            media.add("video");
        }
        return media.isEmpty() ? null : String.join(",", media);
    }

    private static String convertRanking(SearchRanking ranking) {
        return switch (ranking) {
            case date -> "created-desc";
            case dateReverse -> "created-asc";
            case interestingness, interestingnessReverse -> "popular";
            default -> "relevance";
        };
    }
}
