package de.l3s.interweb.connector.ipernity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.l3s.interweb.connector.ipernity.entity.Doc;
import de.l3s.interweb.connector.ipernity.entity.IpernityResponse;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.search.*;
import de.l3s.interweb.core.util.DateUtils;

@Dependent
public class IpernityConnector implements SearchConnector {
    private static final Logger log = Logger.getLogger(IpernityConnector.class);
    private static final int fallbackPerPage = 100;

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
        return new ContentType[]{ContentType.images, ContentType.audios, ContentType.videos};
    }

    @Override
    public Uni<SearchConnectorResults> search(SearchQuery query) throws ConnectorException {
        return searchClient.search(
                query.getQuery(),
                convertContentTypes(query.getContentTypes()),
                query.getPage(),
                query.getPerPage(fallbackPerPage),
                convertSort(query.getSort()),
                DateUtils.toEpochSecond(query.getDateFrom()),
                DateUtils.toEpochSecond(query.getDateTo())
        ).map(Unchecked.function(body -> {
            try {
                return mapper.readValue(body, IpernityResponse.class);
            } catch (JsonProcessingException e) {
                throw new ConnectorException("Failed to parse response", e);
            }
        })).map(response -> {
            SearchConnectorResults queryResult = new SearchConnectorResults();
            long totalResultCount = response.docs().total();
            queryResult.setTotalResults(totalResultCount);
            int rank = query.getOffset(fallbackPerPage);

            List<Doc> docs = response.docs().doc();
            for (Doc doc : docs) {
                queryResult.addResultItem(createSearchItem(doc, ++rank));
            }
            return queryResult;
        });
    }

    private static SearchItem createSearchItem(Doc doc, int rank) {
        SearchItem resultItem = new SearchItem(rank);
        resultItem.setType(ContentType.images);
        resultItem.setId(doc.docId());
        resultItem.setTitle(doc.title());
        resultItem.setDate(DateUtils.parse(doc.dates().created()));
        resultItem.setUrl("http://ipernity.com/doc/" + doc.owner().userId() + "/" + doc.docId());
        resultItem.setCommentsCount(doc.count().comments());
        resultItem.setViewsCount(doc.count().visits());

        if (doc.thumb() != null) {
            resultItem.setThumbnailLarge(new Thumbnail(doc.thumb().url(), doc.thumb().width(), doc.thumb().height()));
            resultItem.setWidth(doc.thumb().width());
            resultItem.setHeight(doc.thumb().height());
        }

        return resultItem;
    }

    private static String convertContentTypes(Set<ContentType> contentTypes) {
        ArrayList<String> media = new ArrayList<>();
        if (contentTypes.contains(ContentType.images)) {
            media.add("photo"); // photo, audio, video, other
        }
        if (contentTypes.contains(ContentType.audios)) {
            media.add("audio");
        }
        if (contentTypes.contains(ContentType.videos)) {
            media.add("video");
        }
        return media.isEmpty() ? null : String.join(",", media);
    }

    private static String convertSort(SearchSort sort) {
        return switch (sort) {
            case date -> "created-desc";
            case popularity -> "popular";
            default -> "relevance";
        };
    }
}
