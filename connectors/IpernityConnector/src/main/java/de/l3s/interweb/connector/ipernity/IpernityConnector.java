package de.l3s.interweb.connector.ipernity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.l3s.interweb.connector.ipernity.entity.*;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.describe.DescribeConnector;
import de.l3s.interweb.core.describe.DescribeQuery;
import de.l3s.interweb.core.describe.DescribeResults;
import de.l3s.interweb.core.search.*;
import de.l3s.interweb.core.util.DateUtils;

@Dependent
public class IpernityConnector implements SearchConnector, DescribeConnector {
    private static final Logger log = Logger.getLogger(IpernityConnector.class);
    private static final Pattern pattern = Pattern.compile("(?:https?:)?//(?:www\\.)?ipernity\\.com/(?:doc/[^/]+/(\\d+))", Pattern.CASE_INSENSITIVE);
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
        return new ContentType[]{ContentType.image, ContentType.audio, ContentType.video};
    }

    @Override
    public Pattern getLinkPattern() {
        return pattern;
    }

    @Override
    public Uni<DescribeResults> describe(DescribeQuery query) throws ConnectorException {
        return searchClient.get(query.getId()).map(Unchecked.function(body -> {
            try {
                return mapper.readValue(body, GetResponse.class);
            } catch (JsonProcessingException e) {
                throw new ConnectorException("Failed to parse response", e);
            }
        })).map(Unchecked.function(response -> {
            if (response.doc() == null) {
                throw new ConnectorException("No results");
            }

            return new DescribeResults(createSearchItem(response.doc(), null));
        }));
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
                return mapper.readValue(body, SearchResponse.class);
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

    private static SearchItem createSearchItem(Doc doc, Integer rank) {
        SearchItem resultItem = new SearchItem(rank);
        resultItem.setType(convertMedia(doc.media()));
        resultItem.setId(doc.docId());
        resultItem.setTitle(doc.title());
        resultItem.setDescription(doc.description());
        resultItem.setDate(DateUtils.parse(doc.dates().created()));
        resultItem.setCommentsCount(doc.count().comments());
        resultItem.setViewsCount(doc.count().visits());

        if (doc.owner() != null) {
            resultItem.setUrl("http://ipernity.com/doc/" + doc.owner().userId() + "/" + doc.docId());
            resultItem.setAuthor(doc.owner().username());
            resultItem.setAuthorUrl("http://ipernity.com/home/" + doc.owner().userId());
        }

        if (doc.thumb() != null) {
            resultItem.setThumbnailLarge(new Thumbnail(doc.thumb().url(), doc.thumb().width(), doc.thumb().height()));
            resultItem.setWidth(doc.thumb().width());
            resultItem.setHeight(doc.thumb().height());
        }
        if (doc.thumbs() != null) {
            for (Thumb thumb : doc.thumbs().thumb()) {
                resultItem.setThumbnail(new Thumbnail(thumb.url(), thumb.width(), thumb.height()));
            }
        }

        if (doc.tags() != null && doc.tags().tags() != null) {
            for (Tag tag : doc.tags().tags()) {
                resultItem.addTag(tag.tag());
            }
        }

        return resultItem;
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

    private static ContentType convertMedia(String media) {
        return switch (media) {
            case "audio" -> ContentType.audio;
            case "video" -> ContentType.video;
            default -> ContentType.image;
        };
    }

    private static String convertSort(SearchSort sort) {
        return switch (sort) {
            case date -> "created-desc";
            case popularity -> "popular";
            default -> "relevance";
        };
    }
}
